package ClientServer;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.DefaultListModel;

import Auxillary.FileLogger;
import Auxillary.InvalidNickNameException;
import Auxillary.JSON;
import Auxillary.MyEvent;
import Auxillary.utils;

//Класс сервера
//Сервер ожидает появления новых клиентов - Server.Connection
public class Server extends Thread {

	private ServerSocket serverSocket;
	private DefaultListModel<Connection> connections;
	private FileLogger logger = new FileLogger("connections");
	public static MyEvent onStart = new MyEvent();
	public static MyEvent onStop = new MyEvent();

	public Server(int port, DefaultListModel<Connection> list) throws IOException {
		serverSocket = new ServerSocket(port);
		connections = list;
		start();
	}

	public void run() {
		onStart.evaluate();
		System.out.println("Server is running . . .");
		try {
			while (!isInterrupted()) {
				Socket client = serverSocket.accept();
				try {
					Connection connection = new Connection(client);
					EventQueue.invokeLater(()->onClientConnect(connection));
					
					System.out.println("client [" + connection + "] connected!");
				} catch (IOException e) {
					System.err.println("connection is brocken!");
				} catch (InvalidNickNameException e) {
					System.err.println("nickname is already used!");
				}
			}
		} catch (IOException e) {
			fail(e, "Not listening");
		}
		System.out.println("Server stopped!");
		onStop.evaluate();
	}
	

	@Override
	public void interrupt() {
		try {
			closeConnections();
			serverSocket.close();
			logger.close();
		} catch (IOException e) {
			fail(e, "server interruption");
		}
		super.interrupt();
	}

	public static void fail(Exception e, String str) {
		System.out.println(str + ": " + e);
	}


	private void closeConnections()
	{
		Enumeration<Connection> list = connections.elements();
		while (list.hasMoreElements()) {
			Connection connection = list.nextElement();
			System.out.println("try close client [" + connection + "]");
			connection.interrupt();
		}
		connections.clear();
	}
	
	//сообщение всем подключенным пользователям о появлении нового пользователя
	//и сообщение новому пользователю об уже подключенных пользователях
	private void onClientConnect(Server.Connection newConnection)
	{	
		UserPack userPack = new UserPack(newConnection.getUser(), User.Status.Connect);
	
		Enumeration<Connection> list = connections.elements();
		while (list.hasMoreElements()) {
			Connection connection = list.nextElement();
			connection.send(userPack);
			newConnection.send(new UserPack(connection.getUser(), User.Status.Connect));
		}
		
		connections.addElement(newConnection);
		logger.log(JSON.toString(userPack));
	}
		
	//сообщение всем пользователям об отключении пользователя
	private void onDisconnect(Server.Connection removedConnection)
	{
		connections.removeElement(removedConnection);
		
		UserPack userPack = new UserPack(removedConnection.getUser(), User.Status.Disconnect);
		logger.log(JSON.toString(userPack));

		new Thread(()->notifyClients(userPack)).start();
	}
	
	//сообщение всем пользователям о подключении/отключении пользователе
	private void notifyClients(UserPack userPack)
	{
		Enumeration<Connection> list = connections.elements();
		while (list.hasMoreElements()) {
			Connection connection = list.nextElement();
			connection.send(userPack);
		}
	}
	
	//сообщение новому пользователю об уже подключенных пользователях
	private void notifyNewClientAboutAlreadyConnected(Server.Connection newConnection)
	{
		Enumeration<Connection> list = connections.elements();
		while (list.hasMoreElements()) {
			Connection connection = list.nextElement();
			newConnection.send(new UserPack(connection.getUser(), User.Status.Connect));
		}
	}
	
	
	//перенаправление принятого сообщения
	private void onMessage(MessagePack msgPack)
	{
		Enumeration<Connection> list = connections.elements();
		while (list.hasMoreElements()) {
			Connection connection = list.nextElement();
			if (connection.toString().equals(msgPack.receiver.nickname))
			{
				connection.send(msgPack/* .message */);
				break;
			}
		}
	}
	
	
	//Соединение со стороны сервера
	public class Connection extends Thread {

		protected User user;
		protected Socket socket;
		protected ObjectOutputStream out;
		protected ObjectInputStream in;

		public Connection(Socket client) throws IOException, InvalidNickNameException {
			socket = client;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				socket.close();
				out.close();
				in.close();
				logger.close();
				throw e;
			}

			receiveClientInfo();
		}
		
		private void receiveClientInfo() throws IOException, InvalidNickNameException
		{
			try {
				// пользователь присылает информацию о себе
				user = (User) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//проверяем зарегистрирован ли пользователь с таким ником
			if (!connections.contains(this)) {
				out.writeObject(true);
				this.start();
			} else {
				out.writeObject(false);
				throw new InvalidNickNameException(user.nickname);
			}
		}

		@Override
		public boolean equals(Object obj) {
			return user.equals(((Connection) obj).user);
		}
		
		@Override
		public String toString() {
			return user.nickname;
		}
		
		public User getUser()
		{
			return user;			
		}

		public void send(Object obj) {
			EventQueue.invokeLater( () -> {
				try {
					out.writeObject(obj);
				} catch (IOException e) {
					System.err.println("Отправка сообщения не удалась");
					e.printStackTrace();
				}
			});
		}

		// принимаем сообщения клиента
		public void run() {
			try {
				while (!isInterrupted()) {
					MessagePack message;
					message = (MessagePack) utils.receiveObject(in);
					System.err.print("Message received: ");
					System.out.println(message.message.text);
					EventQueue.invokeLater(()->onMessage(message));
				}
			} catch (SocketException e) {// срабатывает при закрытии сокета
//				e.printStackTrace();
				System.err.println("client " + this + " interrupted!");
			} catch (IOException e) {// срабатывает при разрыве соединения на другой стороне
//				e.printStackTrace();
				System.err.println("client " + this + " disconnected!");
				EventQueue.invokeLater(()->onDisconnect(this));
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	
		@Override
		public void interrupt() {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			super.interrupt();
		}
		
	}
}
