package org.itstep;

import java.util.List;
import java.util.logging.Logger;

import org.itstep.model.Item;
import org.itstep.service.FileService;
import org.itstep.service.ScrapingService;

public class ApplicationRunner {

	private static Logger logger = Logger.getLogger(ApplicationRunner.class.getName());

	public static void main(String[] args) {
		
		if(args.length == 0) {
			logger.info("No keyword to find items!");
			return;
		}
		String keyword = "";
		for (int i = 0; i < args.length; i++) {
			keyword += args[i] + " ";
		}
		keyword = keyword.trim();
		keyword = keyword.replaceAll(" ", "+");
		
//		List<Item> items = ScrapingService.getItemsByJsoup(keyword);
		List<Item> items = ScrapingService.getItemsBySelenium(keyword);
		
		FileService.writeListToFile(items);
	}
}
