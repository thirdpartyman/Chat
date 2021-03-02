package Settings;
import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import Auxillary.JSON;

//Базовый класс настроек, 
//осуществляет сохранение класса в реестр при помощи Preferences
//HKEY_CURRENT_USER\Software\JavaSoft\Prefs
public class PreferencesSettings{
	private Preferences pref = Preferences.userRoot();

	public void Load(String filename) {
		Load(this, pref.node(filename));
	}

	public void Save(String filename) {
		Save(this, pref.node(filename));
	}
	
	@Override
	public String toString()
	{
		return JSON.toString(this);
	}
	
	private static boolean isModifierSet(int allModifiers, int specificModifier) {
        return (allModifiers & specificModifier) > 0;
    }
	
	private static void Load(Object obj, Preferences pref) {

		Class myClass = obj.getClass();
		Field[] fields = myClass.getFields();
		for (Field field : fields) {
			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();

			Class myclass = value != null ? value.getClass() : field.getType();

			try {
				if (myclass == Integer.class) {
					value = pref.getInt(name, value != null ? (int) value : 0);
					field.set(obj, value);
					continue;
				}
				if (myclass == String.class) {
					value = pref.get(name, value != null ? (String) value : (String) value);
					field.set(obj, value);
					continue;
				}
				if (myclass == Boolean.class) {
					value = pref.getBoolean(name, value != null ? (boolean) value : false);
					field.set(obj, value);
					continue;
				}
				if (myclass == Double.class) {
					value = pref.getDouble(name, value != null ? (double) value : 0.0);
					field.set(obj, value);
					continue;
				}
				if (myclass == Long.class) {
					value = pref.getLong(name, value != null ? (long) value : 0L);
					field.set(obj, value);
					continue;
				}
				if (myclass == Float.class) {
					value = pref.getFloat(name, value != null ? (float) value : 0.0F);
					field.set(obj, value);
					continue;
				}
				if (myclass.isEnum()) {
					value = Enum.valueOf(myclass, pref.get(name, value == null ? "" : value.toString()));
					field.set(obj, value);
					continue;
				}

			} catch (IllegalArgumentException | IllegalAccessException e) {
//				e.printStackTrace();
				continue;
			}

			
			if (value == null) {
				try {
					value = myclass.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| SecurityException e1) {
					e1.printStackTrace();
				}
			}
			Load(value, pref.node(name));
			try {
				field.set(obj, value);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}

		}
	}

	private static void Save(Object obj, Preferences pref) {

		Class myClass = obj.getClass();
		Field[] fields = myClass.getFields();
		for (Field field : fields) {
			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();

			if (value == null) {
				if (isPrimitive(field.getType())) {
					pref.remove(name);
				} else {
					try {
						pref.node(name).removeNode();
					} catch (BackingStoreException e) {
						e.printStackTrace();
					}
				}
				continue;
			}

			Class myclass = value.getClass();

			if (myclass == Integer.class) {
				pref.putInt(name, (int) value);
				continue;
			}
			if (myclass == String.class) {
				pref.put(name, (String) value);
				continue;
			}
			if (myclass == Boolean.class) {
				pref.putBoolean(name, (boolean) value);
				continue;
			}
			if (myclass == Double.class) {
				pref.putDouble(name, (double) value);
				continue;
			}
			if (myclass == Long.class) {
				pref.putLong(name, (long) value);
				continue;
			}
			if (myclass == Float.class) {
				pref.putFloat(name, (float) value);
				continue;
			}

			if (myclass.isEnum()) {
				pref.put(name, value.toString());
				continue;
			}
			
			Save(value, pref.node(name));

		}
	}

	private static <T> boolean isPrimitive(Class<T> myclass) {
		return myclass.isPrimitive() || myclass.isEnum() || myclass == Integer.class || myclass == Long.class
				|| myclass == Float.class || myclass == Double.class || myclass == Boolean.class
				|| myclass == String.class;
	}
}