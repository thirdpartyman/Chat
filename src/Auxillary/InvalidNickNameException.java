package Auxillary;

//����������, ���� ��� ������������ ��� ���������������� �� �������
public class InvalidNickNameException extends Exception {
	public InvalidNickNameException(){super();}
	public InvalidNickNameException(String str)
	{
		super(str);
	}
}
