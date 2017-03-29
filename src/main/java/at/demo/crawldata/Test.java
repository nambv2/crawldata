/**
 * 
 */
package at.demo.crawldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author nambv
 *
 * Mar 14, 2017
 */
public class Test {
	public static void main(String[] args) 
    {
		long a = 26859787;
		System.out.println(TimeUnit.DAYS.convert(a, TimeUnit.MILLISECONDS));
    }
}
