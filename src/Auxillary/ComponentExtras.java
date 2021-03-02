package Auxillary;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class ComponentExtras {
	
	//компонент "вспыхивает" красным
	public static void Errorize(Component component) {
		component.setBackground(Color.RED);
		int duration = 3;
		
		class MyActionListener implements ActionListener {
			private Timer timer = new Timer(duration, this);
			
			MyActionListener() { timer.start();}

			@Override
			public void actionPerformed(ActionEvent e) {
				int green = component.getBackground().getGreen();
				int blue = component.getBackground().getBlue();
				try {
					if (!component.getBackground().equals(Color.WHITE)) {
						component.setBackground(new Color(255, green + 1, blue + 1));
					} else {
						timer.stop();
					}
				} catch (IllegalArgumentException exception) {
					timer.stop();
				}
			}
		}
		
		new MyActionListener();
	}
}
