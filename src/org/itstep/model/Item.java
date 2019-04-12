package org.itstep.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Item {

	private String asin;
	private String name;
	private String itemUrl;
	private String imgUrl;
	private Integer price;
	private Integer initPrice;
	
	public Item() {
	}
	
	@Override
	public String toString() {
		
		return asin + "," 
		+ name + ","
		+ itemUrl + ","
		+ (price == null ? "" : "$" + price/100) + ","
		+ (initPrice == null ? "" : "$" + initPrice/100) + ","
		+ imgUrl;
	}
}
