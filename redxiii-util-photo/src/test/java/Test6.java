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


public class Test6 implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) throws IOException {
		System.setProperty("java.awt.headless", "true"); 
		new Test6().run();
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
		final int width = (int)(3.5f * 100);
		final int height = (int)(2.4f * 100);

		final float ratio = 10;
		
		final int wallWidth = (int) (width * ratio);
		final int wallHeight = (int) (height * ratio);
		
		final int frameHeight = (int) (80 * ratio);
		final int frameBase = (int) (65 * ratio);
		final int middleWidth = wallWidth / 2;
		final int middleHeight = frameBase + (frameHeight / 2);
		
		final File frameA = new File("/Users/df/Documents/Photos/Edicao/NY-2015-244.JPG");
		
		logger.debug("Criando imagem em memoria");
		BufferedImage image = new BufferedImage(wallWidth, wallHeight, BufferedImage.TYPE_INT_RGB);
		
		logger.debug("Obtendo Graphics2D");
		Graphics2D graphics = image.createGraphics();
		
		logger.debug("Desenhando fundo");
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, wallWidth, wallHeight);

		{
			logger.debug("Desenhando rotape");
			int rodape = (int) (ratio * 10);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, wallHeight - rodape, wallWidth, rodape);
		}
		
		int quadroMed = (int) (ratio * 45);
		int quadroPeq = (int) (ratio * 30);
		int quadroBig = (int) (ratio * 60);
		int espaco = (int) (ratio * 8f);
		
		{
			logger.debug("Desenhando quadros da direita");
			final int startX = (int)(middleWidth + (espaco / 2f));
			final int startY = (int)(middleHeight - ((quadroMed + quadroPeq + espaco) / 2f));
			
			int frameX = startX;
			int frameY = startY;
			
			graphics.setColor(Color.BLUE);
			graphics.fillRect(frameX, frameY, quadroMed, quadroMed);	frameX += quadroMed + espaco;
			graphics.fillRect(frameX, frameY, quadroMed, quadroMed);	frameX += quadroMed + espaco;
			
			drawFrame(graphics, frameA, startX + 5, startY + 5, quadroMed - 10, quadroMed - 10);
			
			frameY = (int) (middleHeight - (quadroBig / 2f));
			graphics.setColor(Color.GREEN);
			graphics.fillRect(frameX, frameY, quadroMed, quadroBig);
			
			frameX = startX;
			frameY = startY + quadroMed + espaco;
			graphics.setColor(Color.RED);
			graphics.fillRect(frameX, frameY, quadroBig, quadroPeq);	frameX += quadroBig + espaco;
			graphics.fillRect(frameX, frameY, quadroPeq, quadroPeq);	frameX += quadroPeq + espaco;
		}
		{
			
		}
		{
			logger.debug("Desenhando quadros da esquerda");
			int startX = (int)(middleWidth - (espaco / 2f) - (quadroMed * 2) - espaco);
			int startY = (int)(middleHeight - ((quadroMed + quadroPeq + espaco) / 2f));
			
			int frameX = startX;
			int frameY = startY;
			
			graphics.setColor(Color.RED);
			graphics.fillRect(frameX, frameY, quadroBig, quadroPeq);	frameX += quadroBig + espaco;
			graphics.fillRect(frameX, frameY, quadroPeq, quadroPeq);	frameX += quadroPeq + espaco;
			
			frameX = startX - quadroMed - espaco;
			frameY = (int) (middleHeight - (quadroBig / 2f));
			graphics.setColor(Color.GREEN);
			graphics.fillRect(frameX, frameY, quadroMed, quadroBig);
			
			frameX = startX;
			frameY = startY + quadroPeq + espaco;
			graphics.setColor(Color.BLUE);
			graphics.fillRect(frameX, frameY, quadroMed, quadroMed);	frameX += quadroMed + espaco;
			graphics.fillRect(frameX, frameY, quadroMed, quadroMed);
			
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
		
		logger.debug("Drawing frame: {}", frame.getName());
		BufferedImage frameImage = ImageIO.read(frame);
		
		frameImage = BufferedImageUtil.scaleImage(frameImage, width, height);
		
		graphics.drawImage(frameImage, x, y, width, height, null);
	}

}
