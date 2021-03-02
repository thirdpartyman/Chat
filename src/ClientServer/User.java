package ClientServer;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import Components.MyImage;

//Класс пользователя
public class User implements Serializable {
	private static final long serialVersionUID = 5555391298587175048L;
	public String nickname;
	private MyImage avatar;

	public User() {
		nickname = new String();
		this.setAvatar(null);
	}

	public User(String nickname) {
		this.nickname = nickname;
		this.setAvatar(null);
	}

	public User(String nickname, BufferedImage avatar) {
		this.nickname = nickname;
		this.setAvatar(new MyImage(avatar));
	}

	enum Status {
		Connect, Disconnect, Change
	}

	@Override
	public String toString() {
		return nickname;
	}

	@Override
	public boolean equals(Object obj) {
		return this.nickname.equals(((User) obj).nickname);
	}

	public MyImage getAvatar() {
		return avatar;
	}

	public void setAvatar(MyImage avatar) {
		this.avatar = avatar;
	}
	
}