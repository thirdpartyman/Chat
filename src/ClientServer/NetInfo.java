package ClientServer;
import java.util.Vector;

//Класс, содержащий важные объекты клиента : инфо о пользователе, подключенные пользователи, соединение
public class NetInfo {
	public User currentUser = new User();
	public Vector<User> connectedUsers = new Vector<>();
	public Client.Connection connection;
}
