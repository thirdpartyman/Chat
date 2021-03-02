package ClientServer;
import java.io.Serializable;


//����� � ���������� ������ ������������ �������
public class MessagePack implements Serializable {

	private static final long serialVersionUID = 1358605228024272581L;
	
	public ChatMessage message;//���������
	public User receiver;		//��������
	public String chatTitle;	//�������� ����
	
	public MessagePack(User reciever, ChatMessage message)
	{
		this.receiver = reciever;
		this.message = message;
		chatTitle = message.sender.nickname;
	}
	
	public MessagePack(User reciever, String chatTitle, ChatMessage message)
	{
		this.receiver = reciever;
		this.message = message;
		this.chatTitle = chatTitle;
	}
}