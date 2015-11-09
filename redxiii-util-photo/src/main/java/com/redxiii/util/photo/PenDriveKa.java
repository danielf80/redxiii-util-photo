package com.redxiii.util.photo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PenDriveKa {

	private static Logger logger;
	private static MessageDigest md;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		logger = LoggerFactory.getLogger(PenDriveKa.class);
		md = MessageDigest.getInstance("MD5");
		
		File fSrc = new File("D:/");
		File fDest = new File("C:/Temp/Ka");
		
		copiar(fSrc, fDest);
	}

	private static void copiar(File fSrc, File fDest) throws IOException {
	
		if (fSrc.isDirectory()) {
			
			if (!fDest.exists()) {
				logger.info("Criando diretorio: {}", fDest);
				fDest.mkdir();
			}
			
			for (File fSub : fSrc.listFiles()) {
				copiar(fSub, new File(fDest, fSub.getName()));
			}
		} else if (!fSrc.isHidden()){
			logger.info("Copiando arquivo: {}", fDest);
			if (fDest.exists())
				fDest.delete();
			
			byte[] fSrcData = FileUtils.readFileToByteArray(fSrc);
			String md5Src = DigestUtils.md5Hex(fSrcData);
			
			IOUtils.copy(new ByteArrayInputStream(fSrcData), new FileOutputStream(fDest));
			
			byte[] fDestData = FileUtils.readFileToByteArray(fDest);
			String md5Dest = DigestUtils.md5Hex(fDestData);
			
			if (!md5Src.equals(md5Dest))
				throw new IllegalStateException("File copy is not identical " + md5Src + " <> " + md5Dest);
		} else {
			logger.warn("Arquivo ignorado: {}", fSrc);
		}
	}
}
