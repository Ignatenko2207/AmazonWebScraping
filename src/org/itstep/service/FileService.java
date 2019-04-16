package org.itstep.service;

import java.io.FileWriter;
import java.util.List;

import org.itstep.model.Item;

public class FileService {

	private static final String FILE_PATH = System.getProperty("user.dir") + 
											System.getProperty("file.separator") + 
											"files" +
											System.getProperty("file.separator") +
											"items.csv";
	
	private static final String TEMP_FILE_PATH = System.getProperty("user.dir") + 
			System.getProperty("file.separator") + 
			"files" +
			System.getProperty("file.separator") +
			"temp.html";
	
	public static void writeListToFile(List<Item> items) {
		try (FileWriter writer = new FileWriter(FILE_PATH, true)){
			for (Item item : items) {
				writer.write(item.toString() + ";");
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeHTML(String html) {
		try (FileWriter writer = new FileWriter(TEMP_FILE_PATH)){
			
				writer.write(html);
				writer.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
