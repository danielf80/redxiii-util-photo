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


public class Test7 implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) throws IOException {
		System.setProperty("java.awt.headless", "true"); 
		new Test7().run();
	}
	
	@Override
	public void run() {
		try {
			draw();
		} catch (Exception e) {
			logger.error("Falha ao desenhar imagem", e);
		}
		
	}
	
	public void draw() throws Exception {
		final int width = (int)(4.0f * 100);	// 3500 - x1.458 - 4000 - 1.535
		final int height = (int)(3.0f * 100);	// 2400          - 2605

		final float ratio = 10.85f;
		
		final int wallWidth = 4000;//(int) (width * ratio);
		final int wallHeight = 3000;//(int) (height * ratio);
		
		final int sizeSmallFrame = (int) (ratio * 30);
		final int sizeMedFrame = (int) (ratio * 45);
		final int sizeBigFrame = (int) (ratio * 60);
		final int espaco = (int) (ratio * 5f);
		
		final int frameHeight = sizeMedFrame + sizeSmallFrame + espaco;
		final int frameBase = (int) (75 * ratio);
		final int middleWidth = wallWidth / 2;
		final int middleHeight = frameBase + (frameHeight / 2);
		
		final File frameA = new File("/Users/df/Documents/Photos/Edicao/NY-2015-244.JPG");
		final File frameB = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-1403.JPG");
		final File frameC = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0235.JPG");
		final File frameD = new File("/Users/df/Documents/Photos/Edicao/TRIP-UK-1592b.JPG");
		
		final File frameE = new File("/Users/df/Documents/Photos/Edicao/NY-2015-150.JPG");
		final File frameF = new File("/Users/df/Documents/Photos/Edicao/TRIP-UK-1676.JPG"); ///Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0542.JPG
		final File frameG = new File("/Users/df/Documents/Photos/Edicao/TRIP-US-0045.JPG");
		final File frameH = new File("/Users/df/Documents/Photos/Edicao/TRIP-EUROPA-0897.JPG");
		
		logger.debug("Criando imagem em memoria");
//		BufferedImage image = new BufferedImage(wallWidth, wallHeight, BufferedImage.TYPE_INT_RGB);
		BufferedImage image = ImageIO.read(Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("img/fundo.jpg"));
		
		logger.debug("Obtendo Graphics2D");
		Graphics2D graphics = image.createGraphics();
		
		logger.debug("Desenhando fundo");
//		graphics.setColor(Color.WHITE);
//		graphics.fillRect(0, 0, wallWidth, wallHeight);

//		{
//			logger.debug("Desenhando rotape");
//			int rodape = (int) (ratio * 10);
//			graphics.setColor(Color.LIGHT_GRAY);
//			graphics.fillRect(0, wallHeight - rodape, wallWidth, rodape);
//		}
		
		{
			logger.debug("Desenhando quadros da direita");
			final int startX = (int)(middleWidth + (espaco / 2f));
			final int startY = (int)(middleHeight - ((sizeMedFrame + sizeSmallFrame + espaco) / 2f));
			
			int frameX = startX;
			int frameY = startY;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeMedFrame);
			drawFrame(graphics, frameA, frameX, frameY, sizeBigFrame, sizeMedFrame);
			frameX += sizeBigFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeMedFrame);	
			drawFrame(graphics, frameB, frameX, frameY, sizeBigFrame, sizeMedFrame);
			frameX += sizeBigFrame + espaco;
			
			
			
//			frameY = (int) (middleHeight - (sizeBigFrame / 2f));
//			graphics.setColor(Color.GREEN);
//			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeBigFrame);
			
			frameX = startX;
			frameY = startY + sizeMedFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeSmallFrame, sizeSmallFrame);	
			drawFrame(graphics, frameD, frameX, frameY, sizeSmallFrame, sizeSmallFrame);
			frameX += sizeSmallFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeSmallFrame);	
			drawFrame(graphics, frameC, frameX, frameY, sizeBigFrame, sizeSmallFrame);
			frameX += sizeBigFrame + espaco;
		}
		{
			
		}
		{
			logger.debug("Desenhando quadros da esquerda");
			int startX = (int)(middleWidth - (espaco / 2f) - (sizeMedFrame * 2) - espaco);
			int startY = (int)(middleHeight - ((sizeMedFrame + sizeSmallFrame + espaco) / 2f));
			
			int frameX = startX;
			int frameY = startY;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeSmallFrame);
			drawFrame(graphics, frameE, frameX, frameY, sizeBigFrame, sizeSmallFrame);
			frameX += sizeBigFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeSmallFrame, sizeSmallFrame);
			drawFrame(graphics, frameF, frameX, frameY, sizeSmallFrame, sizeSmallFrame);
			frameX += sizeSmallFrame + espaco;
			
//			frameX = startX - sizeMedFrame - espaco;
//			frameY = (int) (middleHeight - (sizeBigFrame / 2f));
//			graphics.setColor(Color.GREEN);
//			graphics.fillRect(frameX, frameY, sizeMedFrame, sizeBigFrame);
			
			frameX = (int)(middleWidth - (espaco / 2f) - (sizeBigFrame * 2) - espaco);
			frameY = startY + sizeSmallFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeMedFrame);
			drawFrame(graphics, frameG, frameX, frameY, sizeBigFrame, sizeMedFrame);
			frameX += sizeBigFrame + espaco;
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(frameX, frameY, sizeBigFrame, sizeMedFrame);
			drawFrame(graphics, frameH, frameX, frameY, sizeBigFrame, sizeMedFrame);
			
		}
		logger.debug("Finalizando desenho");
		graphics.dispose();
		
		image = getParede(image, 30);
		logger.debug("Salvando imagem");
		ImageIO.write(image, "JPG", new File("output", "wall.jpg"));
		
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
