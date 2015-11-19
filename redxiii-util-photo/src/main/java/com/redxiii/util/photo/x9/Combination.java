package com.redxiii.util.photo.x9;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Combination implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run() {
		try {
			List<String> photos = IOUtils.readLines(new FileReader("library.color.map.txt"));
			List<String> areas = IOUtils.readLines(new FileReader("photo.map.txt"));
			
			logger.info("Lendo arquivo de biblioteca de photos");
			Map<String, Color> photosColor = new HashMap<String, Color>();
			for (String line : photos) {
				String[] parts = StringUtils.split(line, '|');
				photosColor.put(parts[0], new Color((int)Long.parseLong(parts[1].toUpperCase(), 16)));
			}
			
			logger.info("Encontrando melhor foto");
			PrintWriter writer = new PrintWriter("mosaiclly.txt");
			for (String line : areas) {
				String[] parts = StringUtils.split(line, '|');
				Color color = new Color((int)Long.parseLong(parts[4], 16));
				
				String bestFile = null;
				int bestMatch = 999999;
				logger.debug("Procurando foto para a cor {}", Integer.toHexString(color.getRGB()));
				for (Entry<String, Color> entry : photosColor.entrySet()) {
					Color pColor = entry.getValue();
					int match = Math.abs(pColor.getRed() - color.getRed()) +
							Math.abs(pColor.getGreen() - color.getGreen()) +
							Math.abs(pColor.getBlue() - color.getBlue());
					if (match < bestMatch) {
						bestMatch = match;
						bestFile = entry.getKey();
					}
				}
				writer.append(parts[0]);
				writer.append("|");
				writer.append(parts[1]);
				writer.append("|");
				writer.append(parts[2]);
				writer.append("|");
				writer.append(parts[3]);
				writer.append("|");
				writer.append(Integer.toHexString(color.getRGB()));
				writer.append("|");
				writer.append(bestFile);
				writer.append("\r\n");
				writer.flush();
			}
			writer.close();
			logger.info("Programa finalizado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Combination().run();
	}

}
