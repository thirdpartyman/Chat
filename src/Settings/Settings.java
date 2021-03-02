package Settings;

import Auxillary.MyRange;

//Пользовательские настройки программы
public class Settings extends PreferencesSettings {
	public boolean saveFormSize;
	public boolean enableSoundEffects = true;
	public MyRange soundEffectsVolume = new MyRange(0, 100, 100);
}
