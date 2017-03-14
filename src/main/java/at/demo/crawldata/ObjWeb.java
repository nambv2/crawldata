/**
 * 
 */
package at.demo.crawldata;

import org.jsoup.nodes.Element;

/**
 * @author nambv
 *
 * Mar 14, 2017
 */
public class ObjWeb {
	private String title;
	private String img;
	private Element body;
	private String menu;
	
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Element getBody() {
		return body;
	}
	public void setBody(Element body) {
		this.body = body;
	}
}
