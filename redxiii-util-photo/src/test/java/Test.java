import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new Test().run();
	}
	
	public Test() {
		try {
			File fOrig = new File("C:/Users/dfilgueiras/Downloads/storr.jpg");
			File fFrame = new File("C:/Users/dfilgueiras/Downloads/quiraing.jpg");
			File fDest = new File("C:/Users/dfilgueiras/Downloads/mosaic.jpg");
			BufferedImage imgOrig;
			BufferedImage imgFrame;
			try {
				logger.info("Lendo imagem original");
				imgOrig = ImageIO.read(fOrig);
				imgFrame = ImageIO.read(fFrame);
			} catch (IOException e) {
				logger.info("Falha lendo imagem", e);
				return;
			}
			
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			AlphaComposite nonAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			Color cMain = getMainColor(getColors(imgOrig));
			
			logger.info("Criando nova imagem");
			BufferedImage imgDest = new BufferedImage(imgOrig.getWidth(), imgOrig.getHeight(), imgOrig.getType());
			Graphics2D graphics = imgDest.createGraphics();
			
			logger.info("Desenhando nova imagem");
//			graphics.setColor(cMain);
			graphics.setColor(Color.WHITE);
			graphics.fill(new Rectangle(0, 0, imgOrig.getWidth(), imgOrig.getHeight()));
			
			graphics.setComposite(alpha);
			graphics.drawImage(imgOrig, 
					0, 0, imgOrig.getWidth(), imgOrig.getHeight(), 
					0, 0, imgOrig.getWidth(), imgOrig.getHeight(), null);
			
			imgFrame = scaleImage(imgFrame, imgOrig.getWidth() / 3);
			
			int wStart = imgOrig.getWidth() - imgFrame.getWidth();
			int hStart = imgOrig.getHeight() - imgFrame.getHeight();
			graphics.drawImage(imgFrame, 
					wStart, hStart, wStart + imgFrame.getWidth(), hStart + imgFrame.getHeight(), 
					0, 0, imgFrame.getWidth(), imgFrame.getHeight(), null);
			
			graphics.dispose();
			
			logger.info("Salvando nova imagem");
			ImageIO.write(imgDest, "JPG", fDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Finalizado");
	}
	
	private List<Color> getColors(BufferedImage image) {
		List<Color> colors = new ArrayList<Color>();
		
		final Random random = new Random();
		
		int xMax = image.getWidth();
		int yMax = image.getHeight();
		
		for (int c = 0; c < 1000; c++) {
			int rgb = image.getRGB(random.nextInt(xMax), random.nextInt(yMax));
			Color color = new Color(rgb);
			colors.add(color);
		}
		
		return colors;
	}
	
	private Color getMainColor(List<Color> colors) {
		double[] reds = new double[colors.size()];
		double[] greens = new double[colors.size()];
		double[] blues = new double[colors.size()];
		
		for (int c = 0; c < colors.size(); c++) {
			Color color = colors.get(c);
			reds[c] = color.getRed();
			greens[c] = color.getGreen();
			blues[c] = color.getBlue();
		}
		
		Mean mean = new Mean();
		int red = (int)mean.evaluate(reds);
		int green = (int)mean.evaluate(greens);
		int blue = (int)mean.evaluate(blues);
		
		return new Color(red, green, blue);
	}
	
	private static BufferedImage scaleImage(BufferedImage image, float maxSize) throws IOException {
		final float wRatio = maxSize / image.getWidth();
		final float hRatio = maxSize / image.getHeight();
		final float ratio = Math.min(wRatio, hRatio);
		
		int newWidth = (int) (image.getWidth() * ratio);
		int newHeight = (int) (image.getHeight() * ratio);
		
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		return bilinearScaleOp.filter(image, new BufferedImage(newWidth, newHeight, image.getType()));
	}
	
	@Override
	public void run() {
		
	}

}
