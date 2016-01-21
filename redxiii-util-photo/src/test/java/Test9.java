import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redxiii.util.photo.x9.BufferedImageUtil;


public class Test9 implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) throws IOException {
		System.setProperty("java.awt.headless", "true"); 
		new Test9().run();
	}
	
	final File frameA = new File("/Users/df/Documents/Photos/Edicao/uk-phone-red.jpg");
	final File frameB = new File("/Users/df/Documents/Photos/Edicao/NY-2015-223.JPG");
	final File frameC = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1403.JPG");
	final File frameD = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1258.JPG");
	
	final File frameE = new File("/Users/df/Documents/Photos/Edicao/NY-2015-244.JPG");
	final File frameF = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0235.JPG"); ///Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0542.JPG
	final File frameG = new File("/Users/df/Documents/Photos/Edicao/NY-2015-150.JPG");
	final File frameH = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0897.JPG");
	final File frameI = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1529.JPG");
	
	final float ratio = 10.85f;
	
	final int sizeMicroFrame = (int) (ratio * 7.5);
	final int sizeMiniFrame = (int) (ratio * 15);
	final int sizeSmallFrame = (int) (ratio * 30);
	final int sizeSmallFrame2 = (int) (ratio * 35);
	final int sizeMedFrame = (int) (ratio * 45);
	final int sizeBigFrame = (int) (ratio * 60);
//	final int sizeHugeFrame = (int) (ratio * 75);
	final int espaco = (int) (ratio * 5f);
	
	@Override
	public void run() {
		try {
			drawSchemaC3();
		} catch (Exception e) {
			logger.error("Falha ao desenhar imagem", e);
		}
	}
	
	public void drawSchemaC3() throws Exception {
		
		logger.debug("Criando imagem em memoria");
		BufferedImage image = ImageIO.read(Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("img/fundo.jpg"));
		
		logger.debug("Obtendo Graphics2D");
		Graphics2D graphics = image.createGraphics();
		
		final int baseStartX = image.getWidth() / 2;
		final int baseStartY = image.getHeight() / 2;
		
		{
			int frameX = baseStartX - (sizeMedFrame / 2);
			int frameY = baseStartY - (sizeBigFrame + sizeMiniFrame);
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeSmallFrame2);
			drawFrame(graphics, frameB, frameX, frameY, sizeMedFrame, sizeSmallFrame2);
			frameY += sizeMedFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeBigFrame);
			drawFrame(graphics, frameA, frameX, frameY, sizeMedFrame, sizeBigFrame);
			
			frameX += sizeMedFrame + espaco;
			frameY = baseStartY - (sizeBigFrame + sizeMiniFrame);
			frameY += (sizeMedFrame / 2);
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeMedFrame);
			drawFrame(graphics, frameC, frameX, frameY, sizeBigFrame, sizeMedFrame);
			frameY += (sizeMedFrame + espaco);
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeMedFrame);
			drawFrame(graphics, frameE, frameX, frameY, sizeMedFrame, sizeMedFrame);
			frameX += sizeMedFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeSmallFrame);
			drawFrame(graphics, frameF, frameX, frameY, sizeBigFrame, sizeSmallFrame);
			
			frameX = baseStartX - (sizeMedFrame / 2) + sizeMedFrame + sizeBigFrame + (espaco * 2);
			frameY = frameY - sizeMedFrame + (espaco * 2);
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeSmallFrame, sizeSmallFrame);
			drawFrame(graphics, frameD, frameX, frameY, sizeSmallFrame, sizeSmallFrame);
		}
		{
			int frameX = baseStartX - (sizeMedFrame / 2);
			int frameY = baseStartY - (sizeBigFrame + sizeMiniFrame);
			
			frameX -= sizeBigFrame + sizeSmallFrame + (espaco * 2);
			frameY += sizeSmallFrame - sizeMicroFrame;
			
			frameX += sizeSmallFrame + espaco;
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeSmallFrame);
			drawFrame(graphics, frameG, frameX, frameY, sizeBigFrame, sizeSmallFrame);
			
			frameY += sizeSmallFrame + espaco;
			frameX += sizeMiniFrame;
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeMedFrame);
			drawFrame(graphics, frameH, frameX, frameY, sizeMedFrame, sizeMedFrame);
			
			frameX -= sizeSmallFrame + espaco;
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeSmallFrame, sizeSmallFrame);
			drawFrame(graphics, frameI, frameX, frameY, sizeSmallFrame, sizeSmallFrame);
		}
		
		logger.debug("Finalizando desenho");
		graphics.dispose();
		
		image = getParede(image, 30);
		logger.debug("Salvando imagem");
		ImageIO.write(image, "JPG", new File("output", "wall-d.jpg"));
		
		logger.debug("Fim");
	}
	
	public BufferedImage getParede(BufferedImage original, int espaco) {
		logger.debug("Criando imagem da parede");
		BufferedImage image = new BufferedImage(
				original.getWidth() + (espaco * 2), 
				original.getHeight() + (espaco * 2),
				BufferedImage.TYPE_INT_RGB);
		
		logger.debug("Obtendo Graphics2D");
		Graphics2D graphics = image.createGraphics();
		
		logger.debug("Desenhando fundo");
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.drawImage(original, espaco, espaco, original.getWidth(), original.getHeight(), null);
		
		return image;
	}
	
	public void drawFrame(Graphics2D graphics, File frame, int x, int y, int width, int height) throws IOException {

		logger.debug("Drawing at {}x{} - {}x{} - BLACK", x, y, width, height);
		{
			final int reducao = 5;
			x += reducao;
			y += reducao;
			width -= (reducao * 2);
			height -= (reducao * 2);
		}
		logger.debug("Drawing at {}x{} - {}x{} - WHITE", x, y, width, height);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(x, y, width, height);
		
		{
			final int reducao = 30;
			x += reducao;
			y += reducao;
			width -= (reducao * 2);
			height -= (reducao * 2);
		}
		
		logger.debug("Drawing at {}x{} - {}x{} - {}", x, y, width, height, frame.getName());
		BufferedImage frameImage = ImageIO.read(frame);
		frameImage = BufferedImageUtil.scaleImage(frameImage, width, height);
		graphics.drawImage(frameImage, x, y, width, height, null);
	}

}
