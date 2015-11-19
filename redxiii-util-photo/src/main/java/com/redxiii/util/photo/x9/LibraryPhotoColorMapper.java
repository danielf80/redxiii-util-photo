package com.redxiii.util.photo.x9;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryPhotoColorMapper implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int COLORS_SAMPLES = 250;
	
	public static void main(String[] args) {
		new LibraryPhotoColorMapper().run();
	}
	
	@Override
	public void run() {
		
		File fDir = new File("/Users/df/Documents/Photos/2015");
		AbstractFileFilter filter = new SuffixFileFilter(new String[]{"jpg", "png"}, IOCase.INSENSITIVE);
		
		try {
			Writer writer = new PrintWriter("library.color.map.txt");
			
			logger.info("Listando fotos...");
			int nPhotos = 0;
			for (File fPhoto : FileUtils.listFiles(fDir, filter, TrueFileFilter.INSTANCE)) {
				
				if (fPhoto.getParentFile().getName().equals(".picasaoriginals")) 
					continue;
				
				if (fPhoto.getParentFile().getName().equals("MISSING")) 
					continue;
				
				if (fPhoto.getParentFile().getName().equals("ORIGINAL")) 
					continue;
				
				logger.debug("Lendo foto {} / {}", fPhoto.getParentFile().getName(), fPhoto.getName());
				BufferedImage image = ImageIO.read(fPhoto);
				List<Color> colors = getColors(image);
				Color color = getMainColor(colors);
				
				writer.append(fPhoto.getAbsolutePath());
				writer.append("|");
				writer.append(Integer.toHexString(color.getRGB()));
				writer.append("\r\n");
				writer.flush();
				nPhotos++;
			}
			writer.close();
			logger.info("Mapeados {} fotos", nPhotos);
		} catch (IOException e) {
			logger.error("Falha ao criar mapa", e);
		}
	}
	
	private List<Color> getColors(BufferedImage image) {
		List<Color> colors = new ArrayList<Color>();
		
		final Random random = new Random();
		
		int xMax = image.getWidth();
		int yMax = image.getHeight();
		
		for (int c = 0; c < COLORS_SAMPLES; c++) {
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

}
