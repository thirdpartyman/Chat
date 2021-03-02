package Auxillary;

public class StringUtilities {
	//Строка с заглавной буквы
	public static String Capitalize(String source)
	{
		return source.substring(0, 1).toUpperCase() + source.substring(1);
	}
}
