package ClientServer;
import java.util.Vector;

//�����, ���������� ������ ������� ������� : ���� � ������������, ������������ ������������, ����������
public class NetInfo {
	public User currentUser = new User();
	public Vector<User> connectedUsers = new Vector<>();
	public Client.Connection connection;
}
