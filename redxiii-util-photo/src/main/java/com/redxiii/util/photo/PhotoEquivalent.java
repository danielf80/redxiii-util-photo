package com.redxiii.util.photo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redxiii.util.photo.x9.Sector;

public class PhotoEquivalent implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new PhotoEquivalent().run();
	}
	
	@Override
	public void run() {
		try {
			// TODO Auto-generated method stub
			File fDir = new File("/Users/df/Documents/Photos/2014/2014-09 - Europa");
			File fOriginal = new File("/Users/df/Documents/Photos/2014/2014-09 - Europa/2014-09-05/TRIP-EUROPA-0739.JPG");
			
			
			Map<Dimension,Color> identity = getColorSamples(ImageIO.read(fOriginal), 250, null);
			
			File fBestPhoto = null;
			int bestScore = 0;
			
			int count = 0;
			logger.info("Listando fotos...");
			for (File fPhoto : FileUtils.listFiles(fDir, new SuffixFileFilter(new String[]{"jpg", "png"}, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE)) {
				
				if (fPhoto.isDirectory())
					continue;
				
				if (fPhoto.equals(fOriginal))
					continue;
				
				if (count % 100 == 0)
					logger.debug("Avaliados {} fotos", count);
				
				count++;
				BufferedImage image = ImageIO.read(fPhoto);
				int score = 0;
				int index = identity.size();
				for (Entry<Dimension, Color> entry : identity.entrySet()) {
					
					Dimension dim = entry.getKey();
					Color oColor = entry.getValue();

					if ((int)dim.getWidth() >= image.getWidth())
						continue;
					if ((int)dim.getHeight() >= image.getHeight())
						continue;
					if (index + score < bestScore)
						continue;
					
					Color fColor = new Color( image.getRGB((int)dim.getWidth(), (int)dim.getHeight()) );
					int diff = Math.abs(oColor.getRed() - fColor.getRed()) +
							Math.abs(oColor.getGreen() - fColor.getGreen()) +
							Math.abs(oColor.getBlue() - fColor.getBlue());
					
					if (diff <= 100)
						score++;
						
					index--;
				}
				if (score > bestScore) {
					bestScore = score;
					fBestPhoto = fPhoto;
					logger.debug("Melhor foto ({}): {}", bestScore, fBestPhoto);
				}
			}
			logger.info("Melhor comparacao igual a {}", bestScore);
			logger.info("Foto mais parecido igual {}", fBestPhoto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static Map<Dimension,Color> getColorSamples(final BufferedImage image, final int samples, final Sector sector) {
		
		Map<Dimension,Color> colors = new LinkedHashMap<Dimension,Color>();
		
		final Random random = new Random();
		
		final int xMax = sector == null ? image.getWidth() : sector.getWidth();
		final int yMax = sector == null ? image.getHeight() : sector.getHeight();
		final int xSt = sector == null ? 0 : sector.getX();
		final int ySt = sector == null ? 0 : sector.getY();
		
		for (int c = 0; c < samples; c++) {
			int x = xSt + random.nextInt(xMax);
			int y = ySt + random.nextInt(yMax);
			int rgb = image.getRGB(x, y);
			Dimension dimension = new Dimension(x, y);
			Color color = new Color(rgb);
			colors.put(dimension, color);
		}
		
		return colors;
	}

}
