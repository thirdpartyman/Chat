package Settings;
import java.awt.Dimension;

import Main.Program;

//Параметры программы, которые не настраиваются пользователем
public class Properties extends PreferencesSettings {
	public Integer port;
	public String nickname;
	public String avatarPath;
	public Dimension mainFormSize = Program.DefaultSize;
}

