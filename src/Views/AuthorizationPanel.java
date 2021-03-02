package Views;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import Auxillary.ComponentExtras;
import Auxillary.ErrorMessage;
import Auxillary.FileTransferHandler;
import Auxillary.InvalidNickNameException;
import Auxillary.StringUtilities;
import ClientServer.Client;
import ClientServer.NetInfo;
import Components.ImagePanel;
import Components.MyButton;
import Components.MyImage;
import Components.NumberTextField;
import Main.Program;

//Панель авторизации (подключение клиента к серверу)
public class AuthorizationPanel extends JPanel {
	private JLabel portLabel = new JLabel("Порт:");
	private JLabel nicknameLabel = new JLabel("Ник:");
	private NumberTextField portField = new NumberTextField();
	private JTextField nicknameField = new JTextField();

	private MyButton connectionButton = new MyButton("Прервать соединение");
	private MyButton changeLoginButton = new MyButton("Изменить");

	private JPanel mainPanel = new JPanel();
	private JPanel loginPanel = new JPanel();
	
	private String avatarImagePath = Program.Properties.avatarPath;
	private ImagePanel avatarPanel = new ImagePanel();
		
	private NetInfo netInfo;
	private static final Dimension avatarPanelSize = new Dimension(100, 100);

	
	public AuthorizationPanel(NetInfo netInfo) {
		this.netInfo = netInfo;

		createLayout();
		setDecoration();
		setButtonAction();	
		setConnectOnEnter();
		setImageFileTransferHandler();
		setFileDialogOnImagePanelDoubleClick();
		setDelegates();
		initFields();
	}
	
	private void initFields() {
		try {
			portField.setText(Integer.toString(Program.Properties.port));
		} catch (NullPointerException e) {
//			e.printStackTrace();
		}
		try {
			nicknameField.setText(Program.Properties.nickname);
		} catch (NullPointerException e) {
//			e.printStackTrace();
		}
		try {
			avatarPanel.setImage(ImageIO.read(new File(Program.Properties.avatarPath)));
		} catch (IOException | NullPointerException e) {
//			e.printStackTrace();
		}
	}
	
	private void setDelegates()
	{
		netInfo.connection.onDisconnect.add(()->disconnect());
		netInfo.connection.onConnect.add(()->
		{
			Program.Properties.port = Integer.parseInt(portField.getText());
			Program.Properties.nickname = netInfo.currentUser.nickname;
			
			Program.Properties.avatarPath = avatarImagePath;
			Program.Properties.Save(Program.Name);
		});
		
		netInfo.connection.onConnect.add(() -> {
			portField.setEnabled(false);
			nicknameField.setEnabled(false);
			avatarPanel.setEnabled(false);
			connectionButton.setText("Прервать соедининие");
//			changeLoginButton.setVisible(true);
			removeImageFileTransferHandler();
			removeFileDialogOnImagePanelDoubleClick();
		});
		netInfo.connection.onDisconnect.add(() -> {
			portField.setEnabled(true);
			nicknameField.setEnabled(true);
			avatarPanel.setEnabled(true);
			connectionButton.setText("Подключиться");
			setImageFileTransferHandler();
			setFileDialogOnImagePanelDoubleClick();
		});
	}

	
	private void setDecoration()
	{
		setBackground(Color.white);
		
		Border outsideBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border insideBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border border = BorderFactory.createCompoundBorder(insideBorder, outsideBorder);
		outsideBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);		
		border = BorderFactory.createCompoundBorder(outsideBorder, border);
		outsideBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, getBackground());		
		border = BorderFactory.createCompoundBorder(outsideBorder, border);
		mainPanel.setBorder(border);
		
		border = BorderFactory.createTitledBorder("Логин");
		loginPanel.setBorder(border);
		
		avatarPanel.setToolTipText("перетащите изображение или двойной клик для вызова файлового диалога");	
		

		changeLoginButton.setVisible(false);
		connectionButton.setText("Подключиться");
	}
	
	private void createLayout() {
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(mainPanel, constraints);
		
		mainPanel.setLayout(new GridBagLayout());
		loginPanel.setLayout(new GridBagLayout());

		
		constraints = new GridBagConstraints();

		{
			constraints.insets = new Insets(0, 6, 0, 5);
			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridx = 0;
			constraints.gridy = 0;
			mainPanel.add(portLabel, constraints);
		}

		{
			constraints.insets = new Insets(0, 2, 0, 10);
			constraints.gridx = 0;
			constraints.gridy = 0;
			loginPanel.add(nicknameLabel, constraints);
		}
		
		{
			constraints.gridx = 0;
			constraints.gridy = 1;
			loginPanel.add(new JLabel("Аватар:"), constraints);
		}

		{
			constraints.insets = new Insets(0, 0, 0, 0);
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;

			constraints.gridx = 1;
			constraints.gridy = 0;
			mainPanel.add(portField, constraints);
		}
		
		{
			constraints.gridx = 1;
			constraints.gridy = 0;
			loginPanel.add(nicknameField, constraints);
		}
		

		
		{
			GridBagConstraints constraints2 = new GridBagConstraints();
			constraints2.gridx = 1;
			constraints2.gridy = 1;
			constraints2.anchor = GridBagConstraints.WEST;
			constraints2.gridwidth = 1;
			constraints2.fill = GridBagConstraints.NONE;
			constraints2.weightx = 0.0;
			loginPanel.add(avatarPanel, constraints2);

			avatarPanel.setPreferredSize(avatarPanelSize);		
			avatarPanel.setMinimumSize(avatarPanelSize);		
			avatarPanel.setBorder(nicknameField.getBorder());
		}
		
		
		{
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.weightx = 2;
			mainPanel.add(loginPanel, constraints);
		}

		{
			JPanel panel = new JPanel();
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.weightx = 2;
			mainPanel.add(panel, constraints);

			panel.setLayout(new GridBagLayout());


			{
				constraints.fill = GridBagConstraints.NONE;
				constraints.gridx = 1;
				constraints.gridy = 0;
				constraints.insets = new Insets(2, 2, 2, 2);
				constraints.weightx = 0.0;
				constraints.anchor = GridBagConstraints.EAST;

				panel.add(changeLoginButton, constraints);
			}
			
			{
				constraints.gridx = 2;
				constraints.gridy = 0;
				constraints.insets = new Insets(6, 2, 2, 2);
				constraints.weightx = 1.0;
				constraints.anchor = GridBagConstraints.EAST;

				panel.add(connectionButton, constraints);
			}
		}
	}


	private void setButtonAction() {
		connectionButton.addActionListener((event) -> {
			if (netInfo.connection == null) 
			{
				if (connect()) netInfo.connection.onConnect.evaluate();
			}
			else
			{	
				netInfo.connection.onDisconnect.evaluate();			
			}
		});
	}
	
	private void setConnectOnEnter() {
		KeyListener event = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch( keyCode ) { 
					case KeyEvent.VK_ENTER:
						connectionButton.doClick();
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
			
		};
		
		portField.addKeyListener(event);
		nicknameField.addKeyListener(event);
		connectionButton.addKeyListener(event);
		addKeyListener(event);
		setFocusable(true);
	}
	
	private void setImageFileTransferHandler()
	{
		avatarPanel.setTransferHandler(new FileTransferHandler((files)->{setImage(files.get(0));}));
	}
	private void removeImageFileTransferHandler()
	{
		avatarPanel.setTransferHandler(null);
	}
	
	private void setImage(File file)
	{
		try {
			final int weight = 100;
			BufferedImage img = ImageIO.read(file);
			Image tmp = img.getScaledInstance(weight, weight, Image.SCALE_SMOOTH);
		    BufferedImage image = new BufferedImage(weight, weight, BufferedImage.TYPE_INT_ARGB);

		    Graphics2D g2d = image.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    g2d.dispose();
			
			avatarPanel.setImage(image != null ? image : ImagePanel.emptyImage);
			avatarImagePath = file.getPath();
		} catch (IOException | NullPointerException e) {
			System.err.println("not image");
			avatarPanel.setImage(ImagePanel.emptyImage);
			avatarImagePath = null;
		} 
	}

	final JFileChooser fileDialog = new JFileChooser();
	
	private void chooseAvatarImageDialog()
	{
		fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileDialog.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "bmp", "gif");
		fileDialog.setFileFilter(filter);
		if (avatarImagePath != null) fileDialog.setCurrentDirectory(new File(avatarImagePath));
		Action details = fileDialog.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		fileDialog.setPreferredSize(Program.DefaultSize);

		int ret = fileDialog.showDialog(avatarPanel, "Выберите изображение");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileDialog.getSelectedFile();

			setImage(file);
		}
	}

	
	private final MouseAdapter avatarImageMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				chooseAvatarImageDialog();
			}
		}
	};
	
	private void setFileDialogOnImagePanelDoubleClick() {
		avatarPanel.addMouseListener(avatarImageMouseListener);
	}
	private void removeFileDialogOnImagePanelDoubleClick() {	
		avatarPanel.removeMouseListener(avatarImageMouseListener);
	}
	
	private boolean connect()
	{
		boolean error = false;
		int port = 0;
		try
		{
			port = Integer.parseInt(portField.getText());
		}
		catch(NumberFormatException e)
		{
			ComponentExtras.Errorize(portField);
			error = true;
		}
		if (nicknameField.getText().isEmpty())
		{
			ComponentExtras.Errorize(nicknameField);
			error = true;
		}	

		if (error) return false;
		
		netInfo.currentUser.nickname = nicknameField.getText();
		netInfo.currentUser.setAvatar(new MyImage(avatarPanel.getImage()));
		
		try {
			netInfo.connection = new Client.Connection(port, netInfo);
		} catch (IOException e) {
			System.err.println("unable connect to server!");
			ErrorMessage.show(this, "Unable to connect to the server. Probably incorrect port.");
			return false;
		}catch(IllegalArgumentException e) {
			System.err.println(e.getMessage());
			ErrorMessage.show(this, StringUtilities.Capitalize(e.getMessage()));
			return false;
		}catch (InvalidNickNameException e) {
			System.err.println("nickname is already used!");
			ErrorMessage.show(this, "Nickname is already used!");
			return false;
		}
		
		return true;
	}
	
	public void disconnect()
	{
		if (netInfo.connection != null)
		{
			netInfo.connection.interrupt();
			netInfo.connection = null;
		}
	}
	
}
