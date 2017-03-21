/**
 * 
 */
package at.demo.crawldata;

/**
 * @author nambv
 *
 * Mar 13, 2017
 */
public class ResultObj {
	private String title;
	private String id;
	private String img;
	private String swf;
	private String linkSwf;
	private String linkImg;
	
	public String getLinkSwf() {
		return linkSwf;
	}
	public void setLinkSwf(String linkSwf) {
		this.linkSwf = linkSwf;
	}
	public String getLinkImg() {
		return linkImg;
	}
	public void setLinkImg(String linkImg) {
		this.linkImg = linkImg;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getSwf() {
		return swf;
	}
	public void setSwf(String swf) {
		this.swf = swf;
	}
	
}
