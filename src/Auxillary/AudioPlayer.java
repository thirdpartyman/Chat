package Auxillary;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

//����� ��������������� �����������
public class AudioPlayer {

	private static final int BUFFER_SIZE = 4096;

	Clip clip;

	AudioPlayer() {
		try {
			clip = AudioSystem.getClip();
			clip.addLineListener(new LineListener() {
				public void update(LineEvent myLineEvent) {
					if (myLineEvent.getType() == LineEvent.Type.STOP) {
						clip.close();
						try {
							audioStream.reset();
							audioStream = null;
						} catch (IOException | NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	AudioInputStream audioStream;

	void audio(AudioInputStream audioStream) {
		this.audioStream = audioStream;
		try {
			clip.open(audioStream);
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}
	
	void play() {
		clip.start();
	}
	
	//���������
	Volume volume()
	{
		return new Volume((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
	}
	
	//����� ��������� ���������
	class Volume
	{
		FloatControl �ontrol;
		
		Volume(FloatControl control)
		{
			this.�ontrol = control;
		}
		
		public void set(float value) {
			if (value < 0) value = 0;
			if (value > 1) value = 1;
			float min = �ontrol.getMinimum();
			float max = �ontrol.getMaximum();			
			�ontrol.setValue((max - min) * value + min);
		}
		
		// ���������� ������� ��������� (����� �� 0 �� 1)
		public float get() {
			float v = �ontrol.getValue();
			float min = �ontrol.getMinimum();
			float max = �ontrol.getMaximum();
			return (v-min)/(max-min);
		}
	}
}

