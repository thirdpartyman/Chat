package Components;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import javax.swing.JPanel;

//Панель, отображающая изображения
public class ImagePanel extends JPanel {
	protected BufferedImage image;
	
	protected static Supplier<BufferedImage> createEmptyImage = () -> {
		BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setBackground(new Color(0x00FFFFFF, true));
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		return image;
	};
	
	public static final BufferedImage emptyImage = createEmptyImage.get();


	
	public ImagePanel() {
		super();
		this.image = emptyImage;
	}
	
	public ImagePanel(BufferedImage image) {
		super();
		this.image = image;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Insets insets = this.getBorder() != null ? this.getBorder().getBorderInsets(this) : new Insets(0, 0, 0, 0);
		g.drawImage(getImage().getScaledInstance(
				getWidth() - (insets.left + insets.right), 
				getHeight() - (insets.top + insets.bottom),
				Image.SCALE_SMOOTH),
				insets.left,
				insets.top,
				null);
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
}

