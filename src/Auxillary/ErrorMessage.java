package Auxillary;
import java.awt.Component;
import java.awt.Window;

import javax.sound.sampled.AudioInputStream;
import javax.swing.JOptionPane;

//Окно сообщения об ошибке
public class ErrorMessage {
	
    private static final AudioInputStream onErrortSoundNotify = utils.loadAudioFromResources("error.wav");
    
	public static void show(String message)
	{
		utils.play(onErrortSoundNotify);
		JOptionPane.showMessageDialog(Window.getWindows()[0], 
				message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void show(Component parent, String message)
	{
		utils.play(onErrortSoundNotify);
		JOptionPane.showMessageDialog(parent, 
				message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
