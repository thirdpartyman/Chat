package ClientServer;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

//Класс сообщения чата
public class ChatMessage implements Serializable
{
	private static final long serialVersionUID = -1483118912152891631L;
	public final String text;
	public final User sender;
	public final Date date = Calendar.getInstance().getTime();
	
	public ChatMessage(User sender, String text)
	{
		this.text = text;
		this.sender = sender;
	}
	public ChatMessage(String text, User sender)
	{
		this.text = text;
		this.sender = sender;
	}
}
