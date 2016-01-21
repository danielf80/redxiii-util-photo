package com.redxiii.util.photo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoCompareReplicate implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new PhotoCompareReplicate().run();
	}
	
	@Override
	public void run() {
		
		File fDirPc = new File("/Users/df/Documents/Photos/2015/2015-09 - Reino Unido");
		File fDirSd = 		new File("/Volumes/MYLINUXLIVE/Album");
		File fDirSdRep = 	new File("/Volumes/MYLINUXLIVE/AlbumRep");
		File fDirOrd = 		new File("/Volumes/MYLINUXLIVE/AlbumOrd");
		File fDirNew = 		new File("/Volumes/MYLINUXLIVE/AlbumNew");
		
		logger.debug("Lendo photos do SD...");
//		Map<String, File> photosSd = getApagaRepetidas(fDirSd, fDirSdRep);
		Map<String, File> photosSd = getMap(fDirSd);
		
		logger.debug("Lendo photos do PC...");
		Map<String, File> photosPc = getMap(fDirPc);
		
		
		
		logger.debug("Photos Pc {} vs {} Photos SD", photosPc.size(), photosSd.size());
		
		for (Entry<String, File> entry : photosSd.entrySet()) {
			try {
				if (photosPc.containsKey(entry.getKey())) {
//					File fPhotoPc = photosPc.get(entry.getKey());
//					FileUtils.copyFileToDirectory(fPhotoPc, fDirOrd);
				} else {
					logger.debug("Foto nao encontrada no PC: {}", entry.getValue());
					FileUtils.copyFileToDirectory(entry.getValue(), fDirNew);
				}
			} catch (Exception e) {
				logger.error("Falha ao mover photo");
			}
		}
			
	}
	
	private Map<String, File> getApagaRepetidas(File fDir, File fDirRepeat) {
		
		Map<String, File> photosMap = new HashMap<String, File>();
		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			return null;
		}
		
		int count = 0;
		int repetidas = 0;
		logger.info("Listando fotos e videos");
		for (File fPhoto : FileUtils.listFiles(fDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			if (fPhoto.isFile() && fPhoto.getName().endsWith("JPG")) {
				try {
					Reader reader = new FileReader(fPhoto);
					byte[] data = IOUtils.toByteArray(reader);
					
					digest.reset();
					digest.update(data);
					byte[] md5 = digest.digest();
					String md5Hex = Hex.encodeHexString(md5);
					
					if (photosMap.containsKey(md5Hex)) {
						// Repetida
						FileUtils.moveFileToDirectory(fPhoto, fDirRepeat, true);
						repetidas++;
					} else {
						photosMap.put(md5Hex, fPhoto);
					}
					
					
					count++;
					if (count % 100 == 0)
						logger.debug("... {} photos mapped", count);
				} catch (IOException e) {
					logger.error("Falha ao ler arquivo", e);
				}
			}
		}
		logger.debug("Photos repetidas: {}", repetidas);
		
		return photosMap;
	}
	
	private Map<String, File> getMap(File fDir) {
		
		Map<String, File> photosMap = new LinkedHashMap<String, File>();
		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			return null;
		}
		
		int count = 0;
		logger.info("Listando fotos e videos");
		for (File fPhoto : FileUtils.listFiles(fDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			if (fPhoto.isFile() && fPhoto.getName().endsWith("JPG")) {
				try {
					String md5Hex = getMd5Hex(fPhoto);
					
					photosMap.put(md5Hex, fPhoto);
					
					count++;
					if (count % 100 == 0)
						logger.debug("... {} photos mapped", count);
				} catch (IOException e) {
					logger.error("Falha ao ler arquivo", e);
				}
			}
		}
		
		return photosMap;
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
		for (int x = 0, y = 0; x < image.getWidth() && y < image.getHeight(); x++, y++) {
			int rgb = image.getRGB(x, y);
//			logger.debug("{}: {} = {}", fOrig.getName(), x, Integer.toHexString(rgb));
			digest.update(Integer.toHexString(rgb).getBytes());
		}
		byte[] md5 = digest.digest();
		return Hex.encodeHexString(md5);
	}

}
