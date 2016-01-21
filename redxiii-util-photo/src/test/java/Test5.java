import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redxiii.util.photo.x9.BufferedImageUtil;
import com.redxiii.util.photo.x9.Sector;

public class Test5 implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new Test5().run();
	}
	
	@Override
	public void run() {
		try {
			File fOrig = new File("/Volumes/MYLINUXLIVE/AlbumNew/TRIP-EUROPA-0001.JPG");
			File fSd = new File("/Users/df/Documents/Photos/2015/2015-09 - Reino Unido/2015-08-31/TRIP-UK-0002.JPG");
			
			String pcHex = getMd5Hex(fOrig);
			logger.debug("PC: {}", pcHex);
			String sdHex = getMd5Hex(fSd);
			logger.debug("SD: {}", sdHex);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Finalizado");
	}

	private String getMd5Hex(File fOrig)
			throws FileNotFoundException, IOException {
		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			return null;
		}
		
		BufferedImage image = ImageIO.read(fOrig);
		for (int x = 0, y = 0; x < image.getWidth() && y < image.getHeight() && x < 5; x++, y++) {
			int rgb = image.getRGB(x, y);
			logger.debug("{}: {} = {}", fOrig.getName(), x, Integer.toHexString(rgb));
			digest.update(Integer.toHexString(rgb).getBytes());
		}
		byte[] md5 = digest.digest();
		return Hex.encodeHexString(md5);
	}

}
