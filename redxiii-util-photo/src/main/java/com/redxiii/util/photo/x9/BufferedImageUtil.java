package com.redxiii.util.photo.x9;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

public final class BufferedImageUtil {

	public static List<Color> getColorSamples(final BufferedImage image, final int samples, final Sector sector) {
	
		List<Color> colors = new ArrayList<Color>();
		
		final Random random = new Random();
		
		final int xMax = sector == null ? image.getWidth() : sector.getWidth();
		final int yMax = sector == null ? image.getHeight() : sector.getHeight();
		final int xSt = sector == null ? 0 : sector.getX();
		final int ySt = sector == null ? 0 : sector.getY();
		
		for (int c = 0; c < samples; c++) {
			int x = xSt + random.nextInt(xMax);
			int y = ySt + random.nextInt(yMax);
			int rgb = image.getRGB(x, y);
			Color color = new Color(rgb);
			colors.add(color);
		}
		
		return colors;
	}
	
	public static List<Color> getColorSamples(final BufferedImage image, final int samples) {
		return getColorSamples(image, samples, null);
	}
	
	public static Color getMainColor(List<Color> colors) {
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
	
	public static BufferedImage scaleImage(BufferedImage image, float maxSize) throws IOException {
		return scaleImage(image, maxSize, true);
	}
	
	public static BufferedImage scaleImage(BufferedImage image, float maxSize, boolean keepRatio) throws IOException {
		
		float maxSizeX = maxSize;
		float maxSizeY = maxSize;

		if (keepRatio) {
			float wRatio = maxSize / image.getWidth();
			float hRatio = maxSize / image.getHeight();
			float ratio = Math.min(wRatio, hRatio);
			
			maxSizeX = image.getWidth() * ratio;
			maxSizeY = image.getHeight() * ratio;
			
		}
		
		return scaleImage(image, maxSizeX, maxSizeY);
	}
	
	public static BufferedImage scaleImage(BufferedImage image, float maxSizeX, float maxSizeY) throws IOException {
		float wRatio = maxSizeX / image.getWidth();
		float hRatio = maxSizeY / image.getHeight();
		
		int newWidth = (int) (image.getWidth() * wRatio);
		int newHeight = (int) (image.getHeight() * hRatio);
		
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(wRatio, hRatio);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		return bilinearScaleOp.filter(image, new BufferedImage(newWidth, newHeight, image.getType()));
	}
	
	
	public static List<Sector> sectorize(BufferedImage image, int xSector, int ySector) {
		int wStep = (int) (image.getWidth() / (float)xSector);
		int hStep = (int) (image.getHeight() / (float)ySector);
		
		List<Sector> sectors = new ArrayList<Sector>();
		
		Sector sector = null;
		int x = 0, y = 0;
		while (x < image.getWidth()) {
			y = 0;
			while (y < image.getHeight()) {
				sector = new Sector();
				sector.setX(x);
				sector.setY(y);
				
				sector.setXEnd(Math.min(sector.getX() + wStep, image.getWidth()));
				sector.setYEnd(Math.min(sector.getY() + hStep, image.getHeight()));
				sectors.add(sector);
				
				y = sector.getYEnd();
			}
			x = sector.getXEnd();
		}
		return sectors;
	}
	
	public static BufferedImage toGray(BufferedImage imgSrc) {
		BufferedImage imgDest = new BufferedImage(imgSrc.getWidth(), imgSrc.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		
		Graphics graphics = imgDest.getGraphics();
		graphics.drawImage(imgSrc, 0, 0, null);
		graphics.dispose();
		
		return imgDest;
	}
}
