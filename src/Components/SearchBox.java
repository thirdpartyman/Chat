package Components;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//Строка поиска
public class SearchBox extends JPanel {
	
	private HintTextField textField = new HintTextField("поиск");


	SearchBox() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(textField, BorderLayout.CENTER);
		textField.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.white));
		textField.setFont(new Font("Verdana", Font.PLAIN, 16));	
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(150, 150, 150)));
		
		setDocumentListener();
		setKeyListener();
	}
	
	//Событие изменения текста строки поиска
	private void setDocumentListener()
	{
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFieldState();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFieldState();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFieldState();
			}
			protected void updateFieldState() {
				textChangedEventHandler.accept(textField.getText());
			}
		});
	}
	
	//Событие нажатия клавиши Escape
	private void setKeyListener()
	{
		textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
    			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
    				textField.setText(""); 
            }
        });
	}
	
	
	String getText() { return textField.getText(); }

	
	//делегат, вызываемый при изменении текста строки поиска
	Consumer<String> textChangedEventHandler = (text) -> {};
}