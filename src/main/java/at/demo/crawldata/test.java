/**
 * 
 */
package at.demo.crawldata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @author nambv
 *
 * Mar 14, 2017
 */
public class test {
	public static void main(String args[]) {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = sf.format(now).replace("-", "");
		System.out.println(dateString);
	}
}
