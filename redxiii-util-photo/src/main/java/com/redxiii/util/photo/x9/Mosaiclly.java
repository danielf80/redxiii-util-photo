package com.redxiii.util.photo.x9;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mosaiclly implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int SECTORS = 25;
	private final int MAX_WIDTH = 4000, MAX_HEIGHT = 3000;
	private final int WIDTH_SECTOR = MAX_WIDTH / SECTORS;
	private final int HEIGHT_SECTOR = MAX_HEIGHT / SECTORS;
	
	public static void main(String[] args) {
		new Mosaiclly().run();
	}
	
	public void run()  {
		try {
			File fOrig = new File("/Users/df/Documents/Photos/2015/2015-09 - Reino Unido/2015-08-31/TRIP-UK-0040.JPG");
			BufferedImage origImage = ImageIO.read(fOrig);
			
			logger.info("Lendo arquivo de mapeamento");
			List<String> lines = IOUtils.readLines(new FileReader("mosaiclly.txt"));
			
			logger.info("Criando foto");
			BufferedImage image = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = image.createGraphics();
			
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			AlphaComposite nonAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			
			int xIndex = 0, yIndex = 0;
			int c = 0;
			
			for (String line : lines) {
				String[] parts = StringUtils.split(line, '|');
				Color color = new Color((int)Long.parseLong(parts[4], 16));
				String fileName = parts[5];
				
				logger.debug("Lendo e redimensionando imagem {}", fileName);
				BufferedImage imgSector = getResizedImage(fileName);
				
				logger.debug("Gravando na imagem final");
//				g.setComposite(nonAlpha);
				g.drawImage(imgSector, 
						xIndex, yIndex, xIndex + WIDTH_SECTOR, yIndex + HEIGHT_SECTOR,
						0, 0, imgSector.getWidth(), imgSector.getHeight(), null);
				
//				g.setComposite(alpha);
//				g.setColor(color);
//				g.draw(new Rectangle(xIndex, yIndex, WIDTH_SECTOR, HEIGHT_SECTOR));
				
				xIndex += WIDTH_SECTOR;
				
				c++;
				if (c >= SECTORS) {
					xIndex = 0;
					yIndex += HEIGHT_SECTOR;
					c = 0;
				}
			}
			g.setComposite(alpha);
			g.drawImage(origImage, 0, 0, MAX_WIDTH, MAX_HEIGHT, null);
			
			g.dispose();
			
			ImageIO.write(image, "JPG", new File("/Users/df/Downloads/MOSAICLLY.JPG"));
			logger.info("Imagem gerada");
		} catch (IOException e) {
			logger.error("Falha ao gerar imagem", e);
		}
	}
	
	private BufferedImage getResizedImage(String imageName) throws IOException {
		BufferedImage image = ImageIO.read(new File(imageName));
		
		double sx = (double) WIDTH_SECTOR / (double)image.getWidth();
		double sy = (double) HEIGHT_SECTOR / (double)image.getHeight();
	
		
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(sx, sy);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		return bilinearScaleOp.filter(image, new BufferedImage(WIDTH_SECTOR, HEIGHT_SECTOR, image.getType()));
	}

}
