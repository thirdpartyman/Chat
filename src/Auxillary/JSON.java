package Auxillary;
import java.lang.reflect.Field;

//Класс сериализации в JSON
public class JSON {
	static String tab = "    ";

	public static String toString(Object obj) {
		StringBuilder res = new StringBuilder();
		toString(obj, "", res);
		return res.toString();
	}

	private static void toString(Object obj, String tabString, StringBuilder res) {
		if (obj == null)
		{
			res.append(obj);
			return;
		}
		
		Class myClass = obj.getClass();
		Field[] fields = myClass.getFields();
		
		if (fields.length == 0)
		{
			res.append(obj);
			return;
		}
		
		res.append('{');
		for (Field field : fields) {
			res.append('\n');
			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();

			Class myclass = value != null ? value.getClass() : field.getType();
			
			if (isPrimitive(myclass))
				res.append(String.format(tabString + tab + "%s : %s", name, value));
			else {
				res.append(String.format(tabString + tab + "%s : ", name));
				toString(value, tabString + tab, res);
			}
			res.append(',');
		}
		res.deleteCharAt(res.length()-1);
		res.append(tabString + '\n' + tabString + '}');
	}

	private static <T> boolean isPrimitive(Class<T> myclass) {
		return myclass.isPrimitive() || myclass.isEnum() || myclass == Integer.class || myclass == Long.class
				|| myclass == Float.class || myclass == Double.class || myclass == Boolean.class
				|| myclass == String.class;
	}
}