package com.redxiii.util.photo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.GenericImageMetadata.GenericImageMetadataItem;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoOrganizer implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final DateTimeFormatter mdDttmFormatter = DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
	private final DateTimeFormatter sortDttmFormatter = DateTimeFormat.forPattern("yyyy.MM.dd-HH.mm.ss");
	private final DateTimeFormatter formatFullDate = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final NumberFormat nFormat = new DecimalFormat("0000"); 
	private final String IMG_NAME = "TRIP-EUROPA";
	private final String ORI_DIR = "ORIGINAL";
	private final String MOV_DIR = "Videos";
	
	private final boolean PRETEND = false;
	private final boolean MOVE_VIDEOS = true;
	private final boolean CREATE_DATED_FOLTERS = false;
	private final boolean MOVE_SUBFOLDERS_IMGS = true;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new PhotoOrganizer().run();
	}
	
	public void run() {
		File fDir = new File("/Volumes/MYLINUXLIVE/Album");
//		File fDir = new File("/Users/df/Documents/Photos/2014/2014-09 - Europa");
		try {
			verifyImage(fDir);
		} catch (Exception e) {
			logger.error("Falha ao organizar fotos", e);
		}
		logger.debug("Processo concluido");
	}
	
	@SuppressWarnings("unused")
	private void verifyImage(File fDir) throws IOException, ImageReadException {
		
		Map<String, File> mPhotos = new TreeMap<String, File>();
		Map<String, DateTime> mDates = new TreeMap<String, DateTime>();
		
		final File dVideos = new File(fDir, MOV_DIR);
		final File dOriginal = new File (fDir, ORI_DIR);
		
		if (MOVE_VIDEOS) {
			moveVideos(fDir, dVideos);
		}
		
		int index = 0;
		int count = 0;
		
		logger.info("Listando fotos...");
		Collection<File> fPhotos = FileUtils.listFiles(fDir, new SuffixFileFilter(new String[]{"jpg", "png"}, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
		logger.info("Encontradas {} arquivos. Organizando", fPhotos.size());
		for (File fPhoto : fPhotos) {
			
			if (fPhoto.isDirectory())
				continue;
			
			if (fPhoto.getParentFile().equals(dVideos) || fPhoto.getParentFile().equals(dOriginal)) 
				continue;
			
			if (fPhoto.getParentFile().equals(fDir)) {
				// Photo at current directory
			} else if (fPhoto.getParentFile().getName().equals(".picasaoriginals")) {
				if (PRETEND && MOVE_SUBFOLDERS_IMGS) {
					logger.debug("Photo {} not at right folder", fPhoto);
					continue;
				} else if (MOVE_SUBFOLDERS_IMGS) {
					FileUtils.moveFileToDirectory(fPhoto, dOriginal, true);
					continue;
				}
			} else {
				File fDirParent = fPhoto.getParentFile().getParentFile();
				if (fDirParent.equals(fDir)) {
					// Photo at sub-folder, probably at dated folder
				} else {
					if (PRETEND && MOVE_SUBFOLDERS_IMGS) {
						logger.debug("Photo {} not at right folder", fPhoto);
						continue;
					} else if (MOVE_SUBFOLDERS_IMGS) {
						FileUtils.moveFileToDirectory(fPhoto, dOriginal, true);
						continue;
					}
				}
			}
			// From this point the Photo must be organized and renamed
			
			count++;
			DateTime photoDttm = getPhotoDttm(fPhoto);
			if (photoDttm == null)
				continue;

			index++;
			String id = sortDttmFormatter.print(photoDttm) + "-" + nFormat.format(index);
			if (mPhotos.containsKey(id)) 
				logger.warn("Falha ao mapear os arquivos: {}", id);
			
			mPhotos.put(id, fPhoto);
			mDates.put(id, photoDttm);
		}
		logger.info("Encontradas {} photos", count);
		
		renomear(fDir, mPhotos, mDates);
		
		logger.info("Organizacao concluida");
	}

	private DateTime getPhotoDttm(File fPhoto) {
		
		Map<String, String> metadata = null;
		try {
			try {
				metadata = getMetadataAsMap(Imaging.getMetadata(fPhoto));
			} catch (Exception e) {
				logger.error("Falha ao ler imagem: {}", fPhoto);
				throw new IllegalArgumentException();
			}
			
	//		String dttm = metadata.get("DateTime");
			String dttmOri = metadata.get("DateTimeOriginal");
			String dttmDig = metadata.get("DateTimeDigitized");
			
			if (StringUtils.isBlank(dttmDig)) {
				logger.debug("Sem data de digitalizacao: {}", fPhoto);
				throw new IllegalArgumentException();
			} 
			
			String origin = metadata.get("Make");
			if (StringUtils.isBlank(origin)) {
				logger.debug("Sem origem: {}", fPhoto);
				throw new IllegalArgumentException();
			}
			
			if (!StringUtils.equals(dttmDig, dttmOri)) {
				logger.debug("Datas diferentes na foto: {}", fPhoto);
				throw new IllegalArgumentException();
			}
			
			DateTime photoDttm = mdDttmFormatter.parseDateTime(StringUtils.remove(dttmDig, "'"));
			if (StringUtils.equals(origin, "'Canon'")) {
				photoDttm = photoDttm.plusHours(5);
			}
			
			return photoDttm;
		} catch (IllegalArgumentException e) {
			if (metadata != null) {
//				MapUtils.verbosePrint(System.out, "Photo", metadata);
			}
		}
		
		logger.debug("Obtido data de modificacao {}", fPhoto);
		return new DateTime(fPhoto.lastModified());
	}
	
	private void renomear(File fDir, Map<String, File> mPhotos, Map<String, DateTime> mDates) throws IOException {
		
		int index = 0, repeated = 0;
		
		logger.info("Renomeando {} photos", mPhotos.size());
		File fDtDir = fDir;
		for (Entry<String, File> entry : mPhotos.entrySet()) {
			index++;
			File fPhoto = entry.getValue();
			
			if (CREATE_DATED_FOLTERS) {
				String date = formatFullDate.print( mDates.get(entry.getKey()) );
				
				fDtDir = new File(fDir, date);
				if (!fPhoto.getParentFile().equals(fDtDir))
					FileUtils.moveFileToDirectory(fPhoto, fDtDir, true);
			} else {
				// Keep original directory
				fDtDir = fPhoto.getParentFile();
			}
			File fNewPhoto = new File(fDtDir, IMG_NAME + "-" + nFormat.format(index) + "." + FilenameUtils.getExtension(fPhoto.getName()));
			
			if (fNewPhoto.getName().equals(fPhoto.getName())) {
				logger.debug("Photo com o nome correto: {}", fPhoto);
				continue;
			}
			
			if (fNewPhoto.exists()) {
				logger.warn("Photo com o nome repetido: {}", fPhoto);
				repeated++;
				continue;
			}
			if (!PRETEND) {
				fPhoto.renameTo(fNewPhoto);
			}
			logger.debug("Photo {} renomeada para {}", fPhoto.getParentFile().getName() + "/" + fPhoto.getName(), fNewPhoto.getParentFile().getName() + "/" + fNewPhoto.getName());
		}
		logger.error("Encontradas {} photo repetidas", repeated);
	}

	private void moveVideos(File fDir, final File dVideos) throws IOException {
		logger.info("Listando fotos...");
		for (File fMov : FileUtils.listFiles(fDir, new SuffixFileFilter(new String[]{"mov", "mp4"}, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE)) {
			if (fMov.isDirectory())
				continue;
			
			if (fMov.getParentFile().equals(dVideos)) 
				continue;
			
			FileUtils.moveFileToDirectory(fMov, dVideos, true);
			logger.debug("Movendo video...");
		}
	}
	
	
	private Map<String, String> getMetadataAsMap(ImageMetadata metadata) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if (metadata instanceof JpegImageMetadata) {
			final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			
			final List<ImageMetadataItem> items = jpegMetadata.getItems();
			
            for (int i = 0; i < items.size(); i++) {
            	final ImageMetadataItem item = items.get(i);
                if (item instanceof GenericImageMetadataItem) {
                	GenericImageMetadataItem gItem = (GenericImageMetadataItem)item;
                	
                	map.put(gItem.getKeyword(), gItem.getText());
                }
            }
		}
		return map;
	}
}
