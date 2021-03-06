package org.itstep.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.itstep.model.Item;
import org.itstep.util.Timer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScrapingService {

	private static final String CHROME_DRIVER_PATH = System.getProperty("user.dir") + 
			System.getProperty("file.separator") + 
			"lib" +
			System.getProperty("file.separator") +
			"chromedriver.exe";
	
	public static List<Item> getItemsBySelenium(String keyword) {

		List<Item> items = new ArrayList<Item>();

		String url = String.format("https://www.amazon.com/s?k=%s&ref=nb_sb_noss_2", keyword);
		
		WebDriver driver = getChromeDriver();
		driver.get(url);

		Timer.waitSeconds(3);
		
		
		driver.quit();
		return items;
	}

	private static WebDriver getChromeDriver() {

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
		
		
		
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		Timer.waitSeconds(2);
		
		return driver;
	}
	
	
	
	
	
	
	

	public static List<Item> getItemsByJsoup(String keyword) {

		List<Item> items = new ArrayList<Item>();

		String url = String.format("https://www.amazon.com/s?k=%s&ref=nb_sb_noss_2", keyword);

		try {
			Connection connection = Jsoup.connect(url);
			connection.request().addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");			
			connection.request().addHeader("accept-encoding", "gzip, deflate, br");			
			connection.request().addHeader("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,uk;q=0.6,de;q=0.5");			
			connection.request().addHeader("cache-control", "no-cache");			
			connection.request().addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");			
			
			Document document = connection.get();

			FileService.writeHTML(document.html());

			Element searchDiv = document.getElementsByClass("s-result-list sg-row").first();
			Elements itemSearchElements = searchDiv.getElementsByAttribute("data-asin");
			for (Element element : itemSearchElements) {
				String asin = element.attr("data-asin");
				String name = getNameByJsoup(element);
				String itemUrl = getItemUrlByJsoup(element);
				String imgUrl = getImgUrlByJsoup(element);
				Integer price = getPriceByJsoup(element);
				Integer initPrice = getInitPriceByJsoup(element);

				Item item = new Item(asin, name, itemUrl, imgUrl, price, initPrice);
				items.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return items;
	}

	private static Integer getInitPriceByJsoup(Element element) {
		Elements priceElements = element.getElementsByClass("a-offscreen");
		if (priceElements.size() > 1) {
			Element priceElement = priceElements.get(1);
			String textPrice = priceElement.text();
			textPrice = textPrice.replaceAll("\\D", "");
			if (!textPrice.isEmpty()) {
				return Integer.parseInt(textPrice);
			}
		}
		return null;
	}

	private static Integer getPriceByJsoup(Element element) {
		Element priceElement = element.getElementsByClass("a-offscreen").first();
		String textPrice = priceElement.text();
		textPrice = textPrice.replaceAll("\\D", "");
		if (textPrice.isEmpty()) {
			return null;
		}
		return Integer.parseInt(textPrice);
	}

	private static String getImgUrlByJsoup(Element element) {
		Element imgLinkElement = element.getElementsByAttribute("data-image-latency").first();
		if (imgLinkElement.hasAttr("src")) {
			String imgLink = imgLinkElement.attr("src");
			if (!imgLink.startsWith("https://m.media-amazon.com")) {
				imgLink = "https://m.media-amazon.com" + imgLink;
			}
			return imgLink;
		}
		return "";
	}

	private static String getItemUrlByJsoup(Element element) {
		Element linkElement = element.getElementsByClass("a-link-normal a-text-normal").first();
		String link = linkElement.attr("href");
		if (!link.startsWith("https://www.amazon.com")) {
			link = "https://www.amazon.com" + link;
		}
		return link;
	}

	private static String getNameByJsoup(Element element) {
		Element linkElement = element.getElementsByClass("a-link-normal a-text-normal").first();
		return linkElement.text();
	}

}
