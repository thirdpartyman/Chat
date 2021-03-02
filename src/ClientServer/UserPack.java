package ClientServer;
import java.io.Serializable;

//Сообщение о подключении/отключении пользователя
public class UserPack implements Serializable {

	private static final long serialVersionUID = -8048827153697794470L;
	
	public User user;
	public User.Status status;

	UserPack(User user, User.Status status) {
		this.user = user;
		this.status = status;
	}
	
	UserPack(UserPack userPack) {
		this.user = userPack.user;
		this.status = userPack.status;
	}
}
