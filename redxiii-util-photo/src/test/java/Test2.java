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

import com.redxiii.util.photo.x9.BufferedImageUtil;
import com.redxiii.util.photo.x9.Sector;

public class Test2 implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new Test2().run();
	}
	
	public Test2() {
		try {
			File fOrig = new File("C:/Users/dfilgueiras/Downloads/storr.jpg");
			File fDest = new File("C:/Users/dfilgueiras/Downloads/framed.jpg");
			BufferedImage imgOrig;
			try {
				logger.info("Lendo imagem original");
				imgOrig = ImageIO.read(fOrig);
				logger.info("Dimensao da imagem {} / {}", imgOrig.getWidth(), imgOrig.getHeight());
			} catch (IOException e) {
				logger.info("Falha lendo imagem", e);
				return;
			}
			
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			AlphaComposite nonAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			
			logger.info("Criando nova imagem");
			BufferedImage imgDest = new BufferedImage(imgOrig.getWidth(), imgOrig.getHeight(), imgOrig.getType());
			Graphics2D graphics = imgDest.createGraphics();
			
			logger.info("Setorizando");
			List<Sector> sectors = BufferedImageUtil.sectorize(imgOrig, 100, 54);
			
			logger.info("Desenhando fundo");
			graphics.drawImage(imgOrig, 
					0, 0, imgOrig.getWidth(), imgOrig.getHeight(), 
					0, 0, imgOrig.getWidth(), imgOrig.getHeight(), null);
			
			logger.info("Desenhando {} setores", sectors.size());
			graphics.setComposite(alpha);
			for (Sector sector : sectors) {
				Color cMain = BufferedImageUtil.getMainColor(BufferedImageUtil.getColorSamples(imgOrig, 100, sector));
				graphics.setColor(cMain);
				graphics.fill(new Rectangle(sector.getX(), sector.getY(), sector.getWidth(), sector.getHeight()));
			}
			
			graphics.dispose();
			
			logger.info("Salvando nova imagem");
			ImageIO.write(imgDest, "JPG", fDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Finalizado");
	}
		
	@Override
	public void run() {
		
	}

}
