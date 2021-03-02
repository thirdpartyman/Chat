package Components;
import javax.swing.JButton;

//Кнопка без отображения эффекта фокуса
public class MyButton extends JButton {
	public MyButton()
	{
		super();
		setFocusPainted(false);
	}
	public MyButton(String text)
	{
		super(text);
		setFocusPainted(false);
	}
}
