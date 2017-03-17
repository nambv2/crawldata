package at.demo.crawldata;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App 
{
	public static Element getDataFromUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return doc.body();
	}
	
	public static void createFolder(String path) {
		File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println(".......Folder is created!");
            } else {
                System.out.println("Error");
            }
        } 
	}
	
	public static void readToExcel(String path, List<ResultObj> cons) {
		
		/*try {
			FileInputStream file = new FileInputStream(new File(path));
			Workbook workBook = WorkbookFactory.create(file);
			Sheet sheet = workBook.getSheetAt(0);
			int rownum = 1;
			int count = 0;
			for (ResultObj con : cons) {
				count ++;
				Row row = sheet.createRow(rownum++);
				int id = con.getId();
				String title = con.getTitle().toString().trim();
				String content = con.getContent().toString().trim();
				String category = con.getCategories().get(0);
				String image = con.getImage();
				String type = con.getTypes().get(0);
				String tag = con.getTags();
				String status = con.getStatus();
				int blogNumber = con.getNumberBlog();
				if (row.getRowNum() == 0)
					continue;
				if (row.getCell(0) == null) {
					row.createCell(0);
				}
				row.getCell(0).setCellValue(count);
				//
				if (row.getCell(1) == null) {
					row.createCell(1);
				}
				row.getCell(1).setCellValue(title);
				//
				if (row.getCell(2) == null) {
					row.createCell(2);
				}
				row.getCell(2).setCellValue(content);
				//
				if (row.getCell(3) == null) {
					row.createCell(3);
				}
				row.getCell(3).setCellValue(category);
				//
				if (row.getCell(4) == null) {
					row.createCell(4);
				}
				row.getCell(4).setCellValue(image);
				//
				//
				if (row.getCell(6) == null) {
					row.createCell(6);
				}
				row.getCell(6).setCellValue(type);
				//
				if (row.getCell(7) == null) {
					row.createCell(7);
				}
				row.getCell(7).setCellValue(title);
				//
				//
				if (row.getCell(8) == null) {
					row.createCell(8);
				}
				row.getCell(8).setCellValue(tag);
				//
				if (row.getCell(9) == null) {
					row.createCell(9);
				}
				row.getCell(9).setCellValue(status);
				//
				if (row.getCell(10) == null) {
					row.createCell(10);
				}
				row.getCell(10).setCellValue(blogNumber);
			}
			file.close();
			FileOutputStream output_file = new FileOutputStream(new File(path));
			// write changes
			workBook.write(output_file);
			// close the stream
			output_file.close();
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("_________________CHU Y, CO LOI ROI, DUOI VO RA NGOAI DI______________"
					+ "\n**khong thi Tat FILE excel di**");
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void downloadSource(String path,String menu) throws Exception {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		
		String arrSource [] = path.split("/");
		String nameSource = arrSource[arrSource.length - 1];
		
		String dateString = sf.format(now).replace("-", "");
		createFolder("C:\\upload"+dateString);
		createFolder("C:\\upload"+dateString+"\\"+menu);
		String folderPath = "C:\\upload"+dateString+"\\"+menu;
		System.out.println("----->>>>>I'm downloading ("+nameSource+") to "+folderPath);
		URL url = new URL(path);
		InputStream in = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buf))) {
			out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();

		FileOutputStream fos = new FileOutputStream(folderPath+"\\"+nameSource);
		fos.write(response);
		fos.close();
	}
	
	public static void parseWebsite(ObjWeb input) throws Exception {
		Element body = input.getBody();
		ResultObj obj;
		List<ResultObj> listObj = new ArrayList<ResultObj>();
		int count = 1;
		if (body.select("#game>#game_embed") == null || body.select("#game>#game_embed").size() == 0) {
			return;
		}
		Element gameEmbed = body.select("#game>#game_embed").get(0);
		String linkDataSource = gameEmbed.attr("data-src").toString();
		String arrNameDataSource [] = linkDataSource.split("/");
		String nameGame = arrNameDataSource[arrNameDataSource.length - 1];
		if(nameGame.indexOf(".swf") != -1) {
			//downloadSource(linkDataSource, input.getMenu());
			downloadSource(input.getImg(), input.getMenu());
			obj = new ResultObj();
			obj.setId(count);
			obj.setSwf(nameGame);
			obj.setImg(input.getImg());
			obj.setTitle(input.getTitle());
			listObj.add(obj);
		}
	}
	
	public static Element httpClient(String url) {
		HttpClient client = HttpClientBuilder.create().build();
    	HttpGet request = new HttpGet(url);
    	HttpResponse response;
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return Jsoup.parse(result.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getDataByMenu(String menu) throws Exception {
		String homeUrl = "http://www.silvergames.com";
		String menuUrl = homeUrl+"/"+menu;
		ObjWeb obj;
        Element body = httpClient(menuUrl);
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Sony Vaio EA21FX\\Desktop\\action\\action.html");
        byte[] content = body.html().getBytes();
        fos.write(content);
        Element catContent = body.getElementById("cat_content");
        Element listGames = catContent.child(0);
        Elements games = listGames.select("div>ul");
        for(int i = 0; i < games.size(); i++) {
        	obj = new ObjWeb();
        	String menuGame = games.get(i).select("a").get(0).attr("href");
        	String titleGame = games.get(i).select("a").get(1).text();
        	String imgGameLink = games.get(i).select("a").get(0).select("img").attr("src");
        	String gameUrl = homeUrl+menuGame;
        	Element bodyMenuGame = httpClient(gameUrl);
        	obj.setBody(bodyMenuGame);
        	obj.setTitle(titleGame);
        	obj.setImg(imgGameLink);
        	obj.setMenu(menu);
        	parseWebsite(obj);
        }
	}
	
	
    public static void main( String[] args )
    {
        System.out.println( "*****Begin get games*****" );
        String [] menu = {"action","racing","shooting","sports","strategy","puzzle","iogames","mmo"};
		try {
			for(int i = 0; i < menu.length; i++) {
				getDataByMenu(menu[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println("--------------end------------");
        	
    }
}
