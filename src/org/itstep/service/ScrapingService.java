package org.itstep.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.itstep.model.Item;
import org.itstep.util.Timer;
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
			Document document = Jsoup.connect(url).get();

			FileService.writeHTML(document.html());

			Element searchDiv = document.getElementsByClass("s-result-list sg-row").first();
			Elements itemSearchElements = searchDiv.getElementsByAttribute("data-asin");
			for (Element element : itemSearchElements) {
				String asin = element.attr("data-asin");
				String name = getName(element);
				String itemUrl = getItemUrl(element);
				String imgUrl = getImgUrl(element);
				Integer price = getPrice(element);
				Integer initPrice = getInitPrice(element);

				Item item = new Item(asin, name, itemUrl, imgUrl, price, initPrice);
				items.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return items;
	}

	private static Integer getInitPrice(Element element) {
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

	private static Integer getPrice(Element element) {
		Element priceElement = element.getElementsByClass("a-offscreen").first();
		String textPrice = priceElement.text();
		textPrice = textPrice.replaceAll("\\D", "");
		if (textPrice.isEmpty()) {
			return null;
		}
		return Integer.parseInt(textPrice);
	}

	private static String getImgUrl(Element element) {
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

	private static String getItemUrl(Element element) {
		Element linkElement = element.getElementsByClass("a-link-normal a-text-normal").first();
		String link = linkElement.attr("href");
		if (!link.startsWith("https://www.amazon.com")) {
			link = "https://www.amazon.com" + link;
		}
		return link;
	}

	private static String getName(Element element) {
		Element linkElement = element.getElementsByClass("a-link-normal a-text-normal").first();
		return linkElement.text();
	}

}
