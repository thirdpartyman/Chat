package ClientServer;
import java.awt.image.BufferedImage;
import java.util.Vector;

//Класс чата - содержит список сообщений чата
public abstract class Chat {
	public User[] users;
	
	private int messageCount = 0;//количество прочитанных сообщений 
	public Vector<ChatMessage> messages = new Vector<>();	//сообщения, полученные от данного пользователя
	
	public Chat() { users = null;}
	public Chat(User[] users)
	{
		this.users = users;
	}
	
	
	public int getMessageCount()
	{
		return messageCount;	
	}
	
	public void updateMessageCount()
	{
		messageCount = messages.size();
	}
	
	public abstract String title();
	
	public abstract BufferedImage avatar();
}
