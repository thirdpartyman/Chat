package ClientServer;
import java.io.Serializable;


//Пакет с сообщением одного пользователя другому
public class MessagePack implements Serializable {

	private static final long serialVersionUID = 1358605228024272581L;
	
	public ChatMessage message;//сообщение
	public User receiver;		//адресант
	public String chatTitle;	//название чата
	
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