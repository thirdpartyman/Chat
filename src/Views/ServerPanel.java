package Views;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import Auxillary.ComponentExtras;
import Auxillary.ErrorMessage;
import ClientServer.Server;
import Components.MyButton;
import Components.NumberTextField;
import Main.Program;

//Панель запуска сервера
public class ServerPanel extends JPanel {
	private JLabel portLabel = new JLabel("Порт: ");
	private NumberTextField portField = new NumberTextField();
	private MyButton launchButton = new MyButton("Запустить");

	private Server server;
	private DefaultListModel<Server.Connection> model = new DefaultListModel<Server.Connection>();
	private JList<Server.Connection> userList = new JList<>(model);
	
	
	public ServerPanel()
	{
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panelPort = new JPanel(new BorderLayout());
		panelPort.add(portLabel, BorderLayout.WEST);
		panelPort.add(portField, BorderLayout.CENTER);
		portLabel.setLabelFor(portField);
		panel.add(panelPort, BorderLayout.NORTH);
		panel.add(launchButton);

		Border outsideBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border insideBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border border = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
		panel.setBorder(border);
		portLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 3));

		add(panel, BorderLayout.PAGE_START);
		add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
		JScrollPane scrollPanel = new JScrollPane(userList,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPanel, BorderLayout.CENTER);

		setButtonAction();
		setConnectOnEnter();
		
		portField.setText(Integer.toString(Program.Properties.port));
	}
	
	
	private void setButtonAction() {
		launchButton.addActionListener((event) -> {
			if (server == null) {
				startServer();
			} else {
				stopServer();
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
						launchButton.doClick();							
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
		launchButton.addKeyListener(event);
		addKeyListener(event);
		setFocusable(true);
	}
	
	
	private void startServer()
	{
		try {
			if (portField.getText().isEmpty())
			{
				ComponentExtras.Errorize(portField);
				return;
			}
			try
			{
				server = new Server(Integer.parseInt(portField.getText()), model);
				launchButton.setText("Отключить");
				portField.setEnabled(false);
			}
			catch(IllegalArgumentException e)
			{
				ComponentExtras.Errorize(portField);
				return;
			}
		} catch (IOException e1) {
			ErrorMessage.show(this, "Could not start server.");
			System.err.println("Could not start server.");
		}
	}
	
	public void stopServer()
	{
		if (server != null)
		{
			server.interrupt();
			server = null;
			launchButton.setText("Запустить");
			portField.setEnabled(true);
		}
	}
}
