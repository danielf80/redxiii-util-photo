package com.redxiii.util.photo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redxiii.util.cli.CmdLineIO;

public class CamPhotoOrganizer implements Runnable {

	private static SimpleDateFormat formatShortDate = new SimpleDateFormat("yyyy-MM");
	private static SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private static SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss");
	private static NumberFormat nFormat = new DecimalFormat("0000"); 
	
	private static int minFilesPerFolder = 10;
	private static int minDatesPerFolder = 3;
	private static boolean changePhotoName;
	private static CmdLineIO cmdLineIO = new CmdLineIO();
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new CamPhotoOrganizer().run();
	}
	
	public void run() {
//		File fDir = new File("C:/Users/Daniel/Pictures/2013/2013-11-18 - Lua de Mel/A02 - Hotel");
		File fDir = new File("/Users/df/Documents/Photos/2015/2015-09 - Reino Unido");
//		changePhotoName = cmdLineIO.getBoolean("Deseja alterar o nome dos arquivos (s/n)? ", false);
		
		try {
			changeName(fDir, "TRIP-UK");
			simpleOrganizer(fDir);
	//		subfolderOrganizer(fDir);
		} catch (Exception e) {
			logger.error("Falha ao organizar fotos", e);
		}
	}
	
	private void changeName(File fDir, String name) throws IOException {
		
		Map<String, File> mPhotos = new TreeMap<String, File>();
		Map<String, File> mPhotos2 = new TreeMap<String, File>();
		int count = 1;
		
		logger.info("Listando fotos e videos");
		for (File fPhoto : FileUtils.listFiles(fDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			if (fPhoto.isFile()) {
				String id = String.valueOf(fPhoto.lastModified()) + "." + nFormat.format(count);
				if (mPhotos.put(id, fPhoto) != null)
					logger.warn("Foto com mesmo ID: {}", fPhoto);
				
				count++;
			}
		}
		
		logger.info("Organizando {} itens por data", mPhotos.size());
		for (File fPhoto : mPhotos.values()) {
			File fParent = fPhoto.getParentFile();
			File fNewPhoto = new File(fParent, fPhoto.lastModified() + "-" + fPhoto.hashCode() + "." + FilenameUtils.getExtension(fPhoto.getName()));
			fPhoto.renameTo(fNewPhoto);
			if (mPhotos2.put(fNewPhoto.getName(), fNewPhoto) != null)
				logger.warn("Foto com mesmo ID: {}", fPhoto);
		}
		
		logger.info("Renomeando {} itens", mPhotos2.size());
		count = 1;
		for (File fPhoto : mPhotos2.values()) {
			String dttm = formatTime.format(fPhoto.lastModified());
			File fNewPhoto = new File(fDir, name + " - " + nFormat.format(count) + "-" + dttm + "." + FilenameUtils.getExtension(fPhoto.getName()));
			fPhoto.renameTo(fNewPhoto);
			count++;
		}
		
		logger.info("Organizacao concluida");
	}
	
	private void simpleOrganizer(File fDir) throws IOException {
		
		logger.info("Iniciando organizacao do diretorio {}", fDir);
		int count = 0;
		for (File fPhoto : fDir.listFiles()) {
			if (fPhoto.isFile()) {
				count++;
				String date = formatFullDate.format(new Date(fPhoto.lastModified()));
				
				File fDtDir = new File(fDir, date);
				if (!fDtDir.exists()) {
					fDtDir.mkdir();
				}
				FileUtils.moveFileToDirectory(fPhoto, fDtDir, true);
			}
		}
		logger.info("Organizacao finalizada: {} itens", count);
	}

	private void subfolderOrganizer(File fDir) throws IOException {
		
		Map<String, List<File>> map = new HashMap<String, List<File>>();
		for (File fPhoto : fDir.listFiles()) {
			String date = formatFullDate.format(new Date(fPhoto.lastModified()));
			List<File> files = map.get(date);
			if (files == null) {
				files = new ArrayList<File>();
				map.put(date, files);
			}
			files.add(fPhoto);
		}
		
		if (map.size() >= minDatesPerFolder) {
			for (String date : map.keySet()) {
				List<File> files = map.get(date);
				if (files.size() > minFilesPerFolder) {
					
					String subName = cmdLineIO.getString("Descricao p/ fotos do dia " + date + ":");
					
					File fSubDir = new File(fDir, date + (subName != null ? (" - " + subName) : ""));
					if (fSubDir.exists() || fSubDir.mkdir()) {
						for (File fPhoto : files) {
							
							if (changePhotoName) {
								String dateTime = formatDateTime.format(new Date(fPhoto.lastModified()));
								FileUtils.moveFile(fPhoto, new File(fSubDir, dateTime + "." + FilenameUtils.getExtension(fPhoto.getName()).toLowerCase()));
							} else {
								FileUtils.moveFileToDirectory(fPhoto, fSubDir, true);
							}
						}
					}
				}
			}
		} else {
			simpleOrganizer(fDir);
		}
	}
}
