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


public class Test3 {

	public static void main(String[] args) throws IOException {
		BufferedImage imgSrc = ImageIO.read(
				Thread.currentThread().getContextClassLoader().getResourceAsStream("img/storr.jpg"));
		
		BufferedImage imgSnap = ImageIO.read(
				Thread.currentThread().getContextClassLoader().getResourceAsStream("img/sheep-1.jpg"));
		
		imgSnap = BufferedImageUtil.scaleImage(imgSnap, imgSrc.getWidth() / 4);
		
		int x = (imgSrc.getWidth() / 2) - (imgSnap.getWidth() / 2);
		int y = (imgSrc.getHeight() / 2) - (imgSnap.getHeight() / 2);
		{
			BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			
			Graphics2D graphics = imgDest.createGraphics();
			graphics.drawImage(imgSrc, 0, 0, null);
			graphics.setComposite(alpha);
			graphics.drawImage(BufferedImageUtil.toGray(imgSnap), x, y, null);
			graphics.dispose();
			
			ImageIO.write(imgDest, "JPG", new File("output", "layered-1.jpg"));
		}
		{
			BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			
			Graphics2D graphics = imgDest.createGraphics();
			graphics.drawImage(BufferedImageUtil.toGray(imgSrc), 0, 0, null);
			graphics.setComposite(alpha);
			
			graphics.drawImage(imgSnap, x, y, null);
			graphics.dispose();
			
			ImageIO.write(imgDest, "JPG", new File("output", "layered-2.jpg"));
		}

	}

	private static BufferedImage toColor(BufferedImage imgSrc, Color color, int i) {
		BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		Graphics graphics = imgDest.getGraphics();

		for (int w = 0; w < imgSrc.getWidth(); w++) {
			for (int h = 0; h < imgSrc.getHeight(); h++) {
				int rgb = imgSrc.getRGB(w, h);
				Color cSrc = new Color(rgb);
				Color dSrc = new Color(
						(cSrc.getRed() + (color.getRed() * i)) / (i + 1),
						(cSrc.getGreen() + (color.getGreen() * i)) / (i + 1),
						(cSrc.getBlue() + (color.getBlue() * i)) / (i + 1));
				graphics.setColor(dSrc);
				graphics.fillRect(w, h, 1, 1);
			}
		}
		
		graphics.dispose();
		
		return imgDest;
	}
	
	private static BufferedImage toColor2(BufferedImage imgSrc, Color color, int i) {
		BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		
		Graphics2D graphics = imgDest.createGraphics();
		graphics.drawImage(imgSrc, 0, 0, null);
		graphics.setComposite(alpha);
		graphics.setColor(color);
		graphics.fillRect(0, 0, imgSrc.getWidth(), imgSrc.getHeight());
		
		graphics.dispose();
		
		return imgDest;
	}
}
