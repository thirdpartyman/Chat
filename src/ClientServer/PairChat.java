package ClientServer;

import java.awt.image.BufferedImage;

public class PairChat extends Chat
{
	public PairChat(User user1, User user2)
	{
		super(new User[] {user1, user2});
	}
	
	public String title()
	{
		return users[1].nickname;
	}
	
	public BufferedImage avatar()
	{
		return users[1].getAvatar().getImage();
	}
}