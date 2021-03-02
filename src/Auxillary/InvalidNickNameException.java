package Auxillary;

//Исключение, если имя пользователя уже зарегистрировано на сервере
public class InvalidNickNameException extends Exception {
	public InvalidNickNameException(){super();}
	public InvalidNickNameException(String str)
	{
		super(str);
	}
}
