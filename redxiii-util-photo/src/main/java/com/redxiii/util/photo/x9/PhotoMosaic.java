package com.redxiii.util.photo.x9;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoMosaic implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int COLORS_SAMPLES = 15;
	private final int IMG_DECOMPOSITION = 25;	// = 10 x 10
	
	public static void main(String[] args) {
		new PhotoMosaic().run();
	}
	
	private static class Area {
		int xStart, yStart;
		int xEnd, yEnd;
		
		int getWidth() {
			return xEnd - xStart;
		}
		int getHeight() {
			return yEnd - yStart;
		}
		@Override
		public String toString() {
			return xStart + "|" + yStart + "|" + xEnd + "|" + yEnd;
		}
	}
	
	
	
	@Override
	public void run() {
		File fOrig = new File("/Users/df/Documents/Photos/2013/2013-11-18 - Lua de Mel/A06 - Chichen Itza/Chichen Itza - 0032.JPG");
		File fDest = new File("/Users/df/Downloads/Chichen Itza - 0032.JPG");
		BufferedImage image;
		try {
			logger.info("Lendo imagem");
			image = ImageIO.read(fOrig);
		} catch (IOException e) {
			logger.info("Falha lendo imagem", e);
			return;
		}
		
		Random r = new Random();
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		AlphaComposite nonAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		
		logger.info("Obtendo areas...");
		List<Area> areas = getAreas(image);
		logger.info("Criando Graphics2D");
		Graphics2D g = image.createGraphics();
		
		try {
			Writer writer = new PrintWriter("photo.map.txt");
			for (Area area : areas) {
				logger.debug("Obtendo cores da area {}", area);
				List<Color> colors = getColors(image, area);
				
				logger.debug("Calculando cor predominante");
				Color color = getMainColor(colors);
				
				writer.append(area.toString());
				writer.append("|");
				writer.append(Integer.toHexString(color.getRGB()));
				writer.append("\r\n");
				writer.flush();
				
//			if (r.nextInt(5) == 1) {
//				g.setComposite(alpha);
//				g.drawImage(image, 
//						area.xStart, area.yStart, area.xEnd, area.yEnd, 
//						area.xStart, area.yStart, area.xEnd, area.yEnd, null);
//			} else {
					logger.debug("Desenhando area com a cor {}", Integer.toHexString(color.getRGB()));
					g.setComposite(r.nextInt(5) == 1 ? alpha : nonAlpha);
					g.setColor(color);
					g.fill(new Rectangle(area.xStart, area.yStart, area.getWidth(), area.getHeight()));
//			}
//				logger.debug("Area desenhada");
			}
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		logger.debug("Finalizando desenho");
		g.dispose();
		try {
			logger.info("Salvando imagem");
			ImageIO.write(image, "JPG", fDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Programa finalizado");
	}
	
	private List<Area> getAreas(BufferedImage image) {
		final int parts = IMG_DECOMPOSITION;
		int wStep = (int) (image.getWidth() / (float)parts);
		int hStep = (int) (image.getHeight() / (float)parts);
		
		List<Area> areas = new ArrayList<PhotoMosaic.Area>();
		
		Area area = null;
		int x = 0, y = 0;
		while (x < image.getWidth()) {
			y = 0;
			while (y < image.getHeight()) {
				area = new Area();
				area.xStart = x;
				area.yStart = y;
				
				area.xEnd = Math.min(area.xStart + wStep, image.getWidth());
				area.yEnd = Math.min(area.yStart + hStep, image.getHeight());
				areas.add(area);
				
				y = area.yEnd;
			}
			x = area.xEnd;
		}
		
		/*for (int x = 0; x < parts; x++) {
			
			for (int y = 0; y < parts; y++) {
				area = new Area();
				area.xStart = (x * wStep);
				area.yStart = (y * hStep);
				
				area.xEnd = Math.min(area.xStart + wStep, image.getWidth());
				area.yEnd = Math.min(area.yStart + hStep, image.getHeight());
				areas.add(area);
				
				if (x == parts)	// Last X area
					area.xEnd = image.getWidth();
			}
			// Last Y area
//			area.yEnd = image.getHeight();
		}*/
		
		return areas;
	}
	
	private List<Color> getColors(BufferedImage image, Area area) {
		List<Color> colors = new ArrayList<Color>();
		
		final Random random = new Random();
		
		int xMax = area.getWidth();
		int yMax = area.getHeight();
		
		for (int c = 0; c < COLORS_SAMPLES; c++) {
			int rgb = image.getRGB(area.xStart + random.nextInt(xMax), area.yStart + random.nextInt(yMax));
			Color color = new Color(rgb);
			colors.add(color);
//			System.out.println(color.getRed() + "\t" + color.getGreen() + "\t" + color.getBlue());
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

}
