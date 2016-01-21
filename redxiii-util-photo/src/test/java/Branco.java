import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.redxiii.util.photo.x9.BufferedImageUtil;


public class Branco {

	public static void main(String[] args) throws IOException {
		System.setProperty("java.awt.headless", "true"); 
		
		final File photo = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1529b.JPG");
		
		BufferedImage original = ImageIO.read(photo);
		
		int espaco = 331;
		int offset = espaco / 2;
		
		BufferedImage novidade = null;
				
		novidade = new BufferedImage(
				original.getWidth() + espaco, 
				original.getHeight() + espaco,
				BufferedImage.TYPE_INT_RGB);
		
//		novidade = BufferedImageUtil.scaleImage(original, 2980, 2980);
		
		Graphics2D graphics = novidade.createGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, novidade.getWidth(), novidade.getHeight());
		graphics.drawImage(original, offset, offset, original.getWidth(), original.getHeight(), null);
		
		File newPhoto = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1529c.JPG");
		ImageIO.write(novidade, "JPG", newPhoto);
	}

}
