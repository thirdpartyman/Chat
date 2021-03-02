package Components;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextArea;

//Многострочное текстовое поле с подсказкой
public class HintTextArea extends JTextArea {

	private String hint;//подсказка
	
	public HintTextArea(int rows, int cols, String hint) {
		super(rows, cols);
		this.hint = hint;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public String getHint() {
		return hint;
	}
	

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (getText().isEmpty()) {
			int height = getHeight();
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Insets insets = getInsets();
			FontMetrics fm = g.getFontMetrics();
			int bgcolor = getBackground().getRGB();
			int fgcolor = getForeground().getRGB();
			int mask = 0xfefefefe;
			int color = ((bgcolor & mask) >>> 1) + ((fgcolor & mask) >>> 1);
			g.setColor(new Color(color, true));
			g.drawString(hint, insets.left, insets.top + fm.getAscent());
		}
	}
}