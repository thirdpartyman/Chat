package Main;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import Settings.Properties;
import Settings.Settings;

//Главный класс программы
//содержит настройки и функцию main
public class Program {
	public static final Properties Properties = new Properties();
	public static final Settings Settings = new Settings();
	public static final String Name = "MyAdorableChat";
	public static final Dimension DefaultSize = new Dimension(400, 640);

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, InterruptedException {
		Program.Properties.Load(Program.Name);
		Program.Settings.Load(Program.Name);
		EventQueue.invokeLater(() -> new MainForm().show());
	}
}
