package com.redxiii.util.photo.x9;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Thumbnail {

	public static void main(String[] args) throws IOException {
		
		File fSrc = new File("/Users/df/Documents/Photos/2015/2015-09 - Reino Unido/2015-09-10/TRIP-UK-1700.JPG");
		File fDest = new File("/Users/df/Downloads/TRIP-UK-1700a.JPG");
		BufferedImage image = ImageIO.read(fSrc);
		
		scaleImage2(image, fDest);
	}
	
	private static void scaleImage(BufferedImage image, File fDest) throws IOException {
		final float maxSize = 500;
		final float wRatio = maxSize / image.getWidth();
		final float hRatio = maxSize / image.getHeight();
		final float ratio = Math.min(wRatio, hRatio);
		
		int newWidth = (int) (image.getWidth() * ratio);
		int newHeight = (int) (image.getHeight() * ratio);
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics2d = newImage.createGraphics();
		graphics2d.drawImage(image, 0, 0, newWidth, newHeight, null);
		graphics2d.dispose();
		
		ImageIO.write(newImage, "JPG", fDest);
	}
	
	private static void scaleImage2(BufferedImage image, File fDest) throws IOException {
		final float maxSize = 500;
		final float wRatio = maxSize / image.getWidth();
		final float hRatio = maxSize / image.getHeight();
		final float ratio = Math.min(wRatio, hRatio);
		
		int newWidth = (int) (image.getWidth() * ratio);
		int newHeight = (int) (image.getHeight() * ratio);
		
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		BufferedImage newImage = bilinearScaleOp.filter(image, new BufferedImage(newWidth, newHeight, image.getType()));
		
		ImageIO.write(newImage, "JPG", fDest);
	}

	private static void scaleImage3(BufferedImage image, File fDest) throws IOException {
		final float maxSize = 500;
		final float wRatio = maxSize / image.getWidth();
		final float hRatio = maxSize / image.getHeight();
		final float ratio = Math.min(wRatio, hRatio);
		
		int newWidth = (int) (image.getWidth() * ratio);
		int newHeight = (int) (image.getHeight() * ratio);
		
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(ratio, ratio);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		BufferedImage newImage = bilinearScaleOp.filter(image, new BufferedImage(newWidth, newHeight, image.getType()));
		
		int xPass = (int) (newWidth / 10f);
		int yPass = (int) (newHeight / 10f);
		
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				int iX = x * xPass;
				int iY = y * yPass;
			}
		}
		
		ImageIO.write(newImage, "JPG", fDest);
	}
	
	private static void getColor(BufferedImage newImage, int xPass, int yPass, int xIndex, int yIndex) {
		int xStart = xIndex * xPass;
		int yStart = yIndex * yPass;
		
		
		for (int c = 0; c < 5; c++) {
			int clr = newImage.getRGB(xStart, yStart);
			Color color = new Color(clr);
			color.getBlue();
		}
	}
}
