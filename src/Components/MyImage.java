package Components;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

//Сериализуемый класс-обертка класса изображения BufferedImage, требуется для передачи изображения
public class MyImage implements Serializable {
	private transient BufferedImage image;

	public MyImage(BufferedImage image) {
		this.setImage(image);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageIO.write(getImage(), "png", out); // png is lossless
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.defaultReadObject();
			setImage(ImageIO.read(in));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}