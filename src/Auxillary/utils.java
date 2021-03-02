package Auxillary;
import static Main.Program.Settings;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class utils {

	public static Image loadImageFromResources(String resource) {
		return Toolkit.getDefaultToolkit().getImage(utils.class.getResource("/resources/image/" + resource));
	}
	
	//Загрузка аудиопотока из ресурсов
	public static AudioInputStream loadAudioFromResources(String resource) {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(AudioPlayer.class.getResourceAsStream("/resources/audio/" + resource)));
			audioStream.mark(audioStream.available());	
			return audioStream;
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	//Воспроизведение аудиопотока
	public static void play(AudioInputStream audioStream) {
		if (Settings.enableSoundEffects) {
			AudioPlayer player = new AudioPlayer();
			player.audio(audioStream);
			player.volume().set(Settings.soundEffectsVolume.value / 100F);
			player.play();
		}
	}

	//Получение объекта без ClassNotFoundException
	public static Object receiveObject(ObjectInputStream stream) throws IOException {
		try {
			return stream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
