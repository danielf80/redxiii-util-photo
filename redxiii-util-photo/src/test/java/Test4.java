import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.redxiii.util.photo.x9.BufferedImageUtil;


public class Test4 {

	public static void main(String[] args) throws IOException {
		BufferedImage imgSrc = ImageIO.read(
				Thread.currentThread().getContextClassLoader().getResourceAsStream("img/uk-phone.jpg"));
		
		{
			BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			Graphics2D graphics = imgDest.createGraphics();
			graphics.drawImage(imgSrc, 0, 0, null);
			graphics.drawImage(keepRed(imgDest), 0, 0, null);
			graphics.dispose();
			
			ImageIO.write(imgDest, "JPG", new File("output", "uk-phone-red.jpg"));
		}
	

	}

	private static BufferedImage keepRed(BufferedImage imgSrc) {
		BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		Graphics graphics = imgDest.getGraphics();

		for (int w = 0; w < imgSrc.getWidth(); w++) {
			for (int h = 0; h < imgSrc.getHeight(); h++) {
				int rgb = imgSrc.getRGB(w, h);
				Color cSrc = new Color(rgb);
				Color dSrc;
				if (cSrc.getRed() > (cSrc.getBlue() + cSrc.getGreen()))
					dSrc = cSrc;
				else {
					int gray = (cSrc.getRed() + cSrc.getGreen() + cSrc.getBlue()) / 3;
					dSrc = new Color(gray, gray, gray);
				}
				
				graphics.setColor(dSrc);
				graphics.fillRect(w, h, 1, 1);
			}
		}
		
		graphics.dispose();
		
		return imgDest;
	}
	
}
