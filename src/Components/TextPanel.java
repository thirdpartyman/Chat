package Components;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.UIManager;

//Паенль, отображающая текст
//используется в ChatListPanel, т.к. стандартные контролы не подошли
public class TextPanel extends JPanel {

	private String text = new String();
	private final boolean wordwrap;

	private static final Font font = UIManager.getFont("Label.font").deriveFont(14);
	public TextPanel() {
		setFont(font);
		this.wordwrap = false;
	}
	public TextPanel(boolean wordwrap) {
		setFont(font);
		this.wordwrap = wordwrap;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int height = getHeight();
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Insets insets = getInsets();
		FontMetrics metrics = g.getFontMetrics();

		int n;
		String drawtext = (wordwrap && ((n = text.indexOf('\n')) != -1)) ? text.substring(0, n) : text;
		g.drawString(drawtext, insets.left, height / 2 + metrics.getAscent() / 2 - 2);

	}
}