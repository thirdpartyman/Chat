package ClientServer;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import Auxillary.ErrorMessage;
import Auxillary.InvalidNickNameException;
import Auxillary.JSON;
import Auxillary.MyEvent;
import Auxillary.utils;

//Класс соединения  со стороны клиента - Client.Connection
public class Client {

	static public class Connection extends Thread {
		private Socket socket;
		private ObjectOutputStream out;
		private ObjectInputStream in;

		public Connection(int port, NetInfo netInfo)
				throws UnknownHostException, IOException, InvalidNickNameException, IllegalArgumentException {

			System.out.println("try connect...");
			socket = new Socket(InetAddress.getLocalHost(), port);

			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				socket.close();
				out.close();
				in.close();
				throw e;
			}

			//регистрация на сервере
			out.writeObject(netInfo.currentUser);

			boolean confirm = (boolean) utils.receiveObject(in);
			if (!confirm)
				throw new InvalidNickNameException();
			
			start();
		}

		public static MyEvent onConnect = new MyEvent();
		public static MyEvent onDisconnect = new MyEvent();
		public static Consumer<User> onNewUserConnection = (user) -> {};
		public static Consumer<User> onUserDisconnect = (user) -> {};
		public static Consumer<MessagePack> onMessageReceived = (message) -> {};

		// принимаем сообщения сервера
		public void run() {
			System.out.println("client socket started!");
			while (!isInterrupted()) {
				try {
					Object message = in.readObject();
					System.out.println(JSON.toString(message));

					EventQueue.invokeLater(() -> {
						System.err.println("object received");
						if (message instanceof MessagePack) {
							System.out.println("Message");
							onMessageReceived.accept((MessagePack) message);
							return;
						}
						if (message instanceof UserPack) {
							UserPack userInfo = (UserPack) message;
							if (userInfo.status == User.Status.Connect) {
								System.out.println("User Connect");
								onNewUserConnection.accept(userInfo.user);
								return;
							}
							if (userInfo.status == User.Status.Disconnect) {
								System.out.println("User Disconnect");
								onUserDisconnect.accept(userInfo.user);
								return;
							}
						}
					});

				} catch (SocketException e) {//соединение разорвано на стороне клиента
					System.err.println("connection interrupted");
					break;
				} catch (IOException e) {//соединение разорвано на стороне сервера
					System.err.println("server disconnected");
					onDisconnect.evaluate();
					EventQueue.invokeLater( () -> ErrorMessage.show("Server disconnected.") ); 
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			System.out.println("client socket stopped!");
		}

		@Override
		public void interrupt() {
			try {
				socket.close();
				out.close();
				in.close();
			} catch (IOException e) {
				fail(e, "interrupt");
			}
			super.interrupt();
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

		public static void fail(Exception e, String str) {
			System.out.println(str + ": " + e);
		}

	}

}
