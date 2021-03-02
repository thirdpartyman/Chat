package Auxillary;

//Класс Range для SettingsPanel. Преобразуется в прогрессбар.
public class MyRange {

	public final int min;
	public final int max;
	public int value;

	public MyRange(int min, int max) {
		this.min = min;
		this.max = max;
		this.value = 0;
	}

	public MyRange(int min, int max, int value) {
		this.min = min;
		this.max = max;
		this.value = value;
	}

	public void setValue(int value) {
		if (value < min || value > max)
			return;
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}