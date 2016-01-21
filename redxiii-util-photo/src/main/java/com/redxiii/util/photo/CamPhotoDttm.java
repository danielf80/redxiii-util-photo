package com.redxiii.util.photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.GenericImageMetadata.GenericImageMetadataItem;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamPhotoDttm implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		new CamPhotoDttm().run();
	}
	
	public void run() {
		File fDir = new File("/Users/df/Documents/dev/redxiii/redxiii-util-photo/2015-08-31");
		
		try {
//			changeDttm(fDir);
			verifyImage(fDir);
		} catch (Exception e) {
			logger.error("Falha ao organizar fotos", e);
		}
		logger.debug("Processo concluido");
	}
	
	private void changeDttm(File fDir) throws IOException {
		
		logger.info("Listando fotos e videos");
		for (File fPhoto : FileUtils.listFiles(fDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			if (fPhoto.isFile()) {

				FileInputStream stream = new FileInputStream(fPhoto);
				byte[] data = IOUtils.toByteArray(stream);
				IOUtils.closeQuietly(stream);
				
				String s = new String(data);
				if (s.contains("Apple")) {
					logger.debug("Foto tirada do iPhone {}", fPhoto);
				} else if (s.contains("Canon")) {
					logger.debug("Foto tirada da Canon {}", fPhoto);
				} else {
					logger.debug("Foto tirada da Outro {}", fPhoto);
				}
			}
		}
		
		logger.info("Organizacao concluida");
	}
	
	private void verifyImage(File fDir) throws IOException, ImageReadException {
		
		logger.info("Listando fotos e videos");
		for (File fPhoto : FileUtils.listFiles(fDir, new SuffixFileFilter(new String[]{"jpg", "png"}, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE)) {
			
			if (fPhoto.isDirectory())
				continue;
			
			File fDirParent = fPhoto.getParentFile().getParentFile();
			if (!fDirParent.equals(fDir))
				continue;
			
			Map<String, String> metadata = getMetadataAsMap(Imaging.getMetadata(fPhoto));
			
			String dttm = metadata.get("DateTime");
			String dttmOri = metadata.get("DateTimeOriginal");
			String dttmDig = metadata.get("DateTimeDigitized");
			
			if (!StringUtils.equals(dttm, dttmDig) || !StringUtils.equals(dttmDig, dttmOri)) {
				logger.debug("Datas diferentes na foto: {}", fPhoto);
			}
			
			break;
		}
		
		logger.info("Organizacao concluida");
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
