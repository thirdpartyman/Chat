package Main;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import Auxillary.ErrorMessage;
import Auxillary.utils;
import ClientServer.Chat;
import ClientServer.ChatMessage;
import ClientServer.Client;
import ClientServer.NetInfo;
import ClientServer.PairChat;
import ClientServer.Server;
import Components.CircleImagePanel;
import Components.MyButton;
import Components.StatusBar;
import Settings.SettingsPanel;
import Views.AuthorizationPanel;
import Views.ChatListPanel;
import Views.ChatPanel;
import Views.ServerPanel;


//Главная форма программы
public class MainForm extends JFrame {

	private final String title = "Чат";
	private NetInfo netInfo = new NetInfo();
	private StatusBar statusBar = new StatusBar();

	private JPanel mainPanel = new JPanel(new CardLayout());
	private AuthorizationPanel authPanel = new AuthorizationPanel(netInfo);
	private ChatListPanel chatListPanel = new ChatListPanel();
	private ChatPanel chatPanel = new ChatPanel(netInfo, chatListPanel.chats);
	private ServerPanel serverPanel = new ServerPanel();
	private SettingsPanel settingsPanel = new SettingsPanel(Program.Settings);

	
	public MainForm() {

		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowWatcher());

		createEvents();
		createMenu();
		createLayout();
		createMessageHandlers();	
		pack();
		setIcon();
		authPanel.grabFocus();
	}
	

	private class WindowWatcher implements WindowListener {
	
		@Override
		public void windowClosing(WindowEvent e) {
			authPanel.disconnect();
			serverPanel.stopServer();
			
			Program.Properties.mainFormSize = getSize();
			Program.Properties.Save(Program.Name);
		}
	
		@Override public void windowActivated(WindowEvent arg0) {}
		@Override public void windowClosed(WindowEvent arg0) {}
		@Override public void windowDeactivated(WindowEvent arg0) {}
		@Override public void windowDeiconified(WindowEvent arg0) {}
		@Override public void windowIconified(WindowEvent arg0) {}
		@Override public void windowOpened(WindowEvent arg0) {}
	}
	
	@Override
	public void pack()
	{
		super.pack();
		Dimension size = getSize();
		size.width += 29;
		setMinimumSize(size);
		setSize(Program.Settings.saveFormSize ? Program.Properties.mainFormSize : Program.DefaultSize);
		setLocationRelativeTo(null);
	}
	
    private void setIcon() {
        setIconImage(utils.loadImageFromResources("chat.png"));
    }
	
    
    private static final AudioInputStream onUserConnectSoundNotify = utils.loadAudioFromResources("user_join.wav");
	private static final AudioInputStream onUserDisconnectSoundNotify = utils.loadAudioFromResources("user_leave.wav");
	private static final AudioInputStream onMessageReceiveSoundNotify = utils.loadAudioFromResources("icq-message.wav");
	
	
	private Runnable showAuthPanel;
	private Runnable showChatListPanel;
	private Runnable showChatPanel;
	private Runnable showServerPanel;
	private Runnable showSettingsPanel;
	private Runnable updateUserCountLabel;
	
	
	private void createEvents()
	{
		CardLayout layout = (CardLayout) (mainPanel.getLayout());
		showAuthPanel = () ->
		{
			chatPanel.setChat(null);
			layout.first(mainPanel);
			layout.next(mainPanel);
			authPanel.grabFocus();
		};
		showChatListPanel = () ->
		{
			chatPanel.setChat(null);
			chatListPanel.update();
			layout.last(mainPanel);
		};
		showChatPanel = () ->
		{
			layout.first(mainPanel);
			chatPanel.update();
		};
		showServerPanel = () ->
		{
			chatPanel.setChat(null);
			layout.last(mainPanel);
			layout.previous(mainPanel);
			serverPanel.grabFocus();
		};
		showSettingsPanel = () ->
		{
			if (settingsPanel.show())
			{	
				Program.Settings.Save(Program.Name);
				System.out.println(Program.Settings);
			}
		};
		
		chatListPanel.itemDoubleClickEventHandler = (chat) -> {
			chatPanel.setChat(chat);
			showChatPanel.run();
		};

		updateUserCountLabel = () -> {
			statusBar.setUserCount(netInfo.connectedUsers.size() + 1);
		};
		
		netInfo.connection.onConnect.add(() -> 
		{	
			setTitle(title + ' ' + '[' + netInfo.currentUser.nickname + ']');
		});
		netInfo.connection.onConnect.add(() -> 
		{	
			statusBar.setConnectionStatus(true);
		});
		netInfo.connection.onConnect.add(() -> 
		{	
			updateUserCountLabel.run();
		});
		netInfo.connection.onDisconnect.add(() -> 
		{	
			netInfo.connectedUsers.clear();
			chatListPanel.chats.clear();
			if (chatPanel.getChat() != null)
			{	
				chatPanel.setChat(null);
//				showChatListPanel.run();
			}
			showAuthPanel.run();
		});
		netInfo.connection.onDisconnect.add(() -> 
		{	
			setTitle(title);
		});
		netInfo.connection.onDisconnect.add(() -> 
		{	
			statusBar.setConnectionStatus(false);
		});
		netInfo.connection.onDisconnect.add(() -> 
		{	
			statusBar.setUserCount(netInfo.connectedUsers.size());
		});
			
		netInfo.connection.onConnect.add(() -> 
		{	
			try {
				CircleImagePanel imagePanel = new CircleImagePanel(ImageIO.read(new File(Program.Properties.avatarPath)));
				ImageIcon icon = new ImageIcon(imagePanel.getImage());
				setIconImage(icon.getImage());
			} catch (IOException | NullPointerException e) {
				setIcon();
			}
		});	
		netInfo.connection.onDisconnect.add(() -> 
		{	
			setIcon();
		});
	}

	
	private void createMenu() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 5));

		add(toolBar, BorderLayout.PAGE_START);

		MyButton authMenu = new MyButton("Авторизация");
		MyButton chatMenu = new MyButton("Мессенджер");
		JToggleButton serverMenu = new JToggleButton("Сервер");
		MyButton settingsMenu = new MyButton("Настройки");

		Dimension weight = new Dimension(5, 5);
		toolBar.add(authMenu);
		toolBar.addSeparator(weight);
		toolBar.add(chatMenu);
		toolBar.addSeparator(weight);
		toolBar.add(serverMenu);
        toolBar.add(Box.createHorizontalGlue());
		toolBar.add(settingsMenu);

		authMenu.addActionListener((e) -> showAuthPanel.run());
		chatMenu.addActionListener((e) -> showChatListPanel.run());
		serverMenu.addActionListener((e) -> showServerPanel.run());
		settingsMenu.addActionListener((e) -> showSettingsPanel.run());
		
		serverMenu.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				serverMenu.setSelected(!serverMenu.isSelected());
			}
		});

		Server.onStart.add(()->serverMenu.setSelected(true));
		Server.onStop.add(()->serverMenu.setSelected(false));
	}
	
	
	private void createLayout()
	{
		add(mainPanel, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		CardLayout layout = (CardLayout) (mainPanel.getLayout());

		mainPanel.add(chatPanel);
		mainPanel.add(authPanel);
		mainPanel.add(serverPanel);
		mainPanel.add(chatListPanel);
		layout.next(mainPanel);
	}

	
	private void createMessageHandlers() {

		Client.Connection.onNewUserConnection = (user) -> {
			netInfo.connectedUsers.add(user);
			chatListPanel.chats.addElement(new PairChat(netInfo.currentUser, user ));
			updateUserCountLabel.run();
			utils.play(onUserConnectSoundNotify);
		};

		Client.Connection.onUserDisconnect = (user) -> {
			netInfo.connectedUsers.remove(user);
			Enumeration<Chat> list = chatListPanel.chats.elements();
			boolean flag = false;
			while (list.hasMoreElements()) {
				Chat chat = list.nextElement();
				if (Arrays.asList(chat.users).contains(user)) {
					chatListPanel.chats.removeElement(chat);
					if (chat.equals(chatPanel.getChat()))
						flag = true;
				}
			}
			updateUserCountLabel.run();
			utils.play(onUserDisconnectSoundNotify);
			if (flag)
			{
				showChatListPanel.run();
				ErrorMessage.show(this, user + " disconnected.");
			}
		};

		Client.Connection.onMessageReceived = (msgPack) -> {
			Enumeration<Chat> list = chatListPanel.chats.elements();
			while (list.hasMoreElements()) {
				Chat chat = list.nextElement();
				if (chat.title().equals(msgPack.chatTitle)) {
					ChatMessage message = new ChatMessage(msgPack.message.text, chat.users[Arrays.asList(chat.users).indexOf(msgPack.message.sender)]);
					if (chat.equals(chatPanel.getChat()))
						chatPanel.addMessage(message);
					else
						chat.messages.add(message);
					
					if (chatListPanel.chats.indexOf(chat) > 0)
					{
						chatListPanel.chats.removeElement(chat);
						chatListPanel.chats.add(0, chat);
					}
					chatListPanel.update();
					
					utils.play(onMessageReceiveSoundNotify);
					return;
				}
			}
		};
	}
}

