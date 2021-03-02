package Components;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


//Статусбар главной формы
public class StatusBar extends JPanel {
	private JLabel userCount = new JLabel();
	private JLabel connectionStatus = new JLabel();

	public StatusBar() {
		setLayout(new GridBagLayout());

		setBackground(new Color(231, 227, 227));
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(150, 150, 150)));

		setUserCount(0);
		setConnectionStatus(false);

		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		{
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 0.5;
			constraints.gridx = 0;
			add(userCount, constraints);
		}

		{
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.ipady = 0;
			constraints.weighty = 1.0;
			constraints.anchor = GridBagConstraints.PAGE_END;
			constraints.gridwidth = 2;
			constraints.gridx = 1;
			add(connectionStatus, constraints);
		}
	}

	public void setUserCount(int count) {
		userCount.setText("Пользователей в сети: " + count);
	}

	public void setConnectionStatus(boolean status) {
		connectionStatus.setText(status ? "Подключено" : "Отключено");
		connectionStatus.setForeground(status ? Color.green : Color.red);
	}
}
