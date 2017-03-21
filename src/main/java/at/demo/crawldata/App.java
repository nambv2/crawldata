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
import java.io.OutputStream;
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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
	private static String configPath = "/Users/nambv/Desktop/" ;
	//private static String configPath = "C:/Users/Sony Vaio EA21FX/Desktop";
	public static Element getDataFromUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return doc.body();
	}
	
	public static void createFolder(String path) {
		File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println(".......Folder is created at "+path);
            } else {
                System.out.println("Error");
            }
        } 
	}
	
	public static void readToExcel(String folder, List<ResultObj> cons) {
		try {
			String path = folder + "/content.xls";
			File fileExcel = new File(path);
			fileExcel.createNewFile();
			System.out.println("=============>>Write out put file execel at "+path);
			FileInputStream file = new FileInputStream(new File(path));
			Workbook workBook = WorkbookFactory.create(file);
			Sheet sheet = workBook.getSheetAt(0);
			int rownum = 1;
			int count = 0;
			for (ResultObj con : cons) {
				count ++;
				Row row = sheet.createRow(rownum++);
				String id = con.getId();
				String title = con.getTitle().toString().trim();
				String image = con.getImg();
				String swf = con.getSwf();
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
				row.getCell(2).setCellValue(image);
				//
				if (row.getCell(3) == null) {
					row.createCell(3);
				}
				row.getCell(3).setCellValue(swf);
				
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
		}
	}
	
	private static void saveFileFromUrl(List<ResultObj> listObj, String folderPath) {
		for(ResultObj obj : listObj) {
			String url = obj.getLinkSwf();
			InputStream inputStream = null;
			OutputStream outputStream = null;
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet getFile = new HttpGet(url);
			HttpResponse response;
			String arrSource [] = url.split("/");
			String nameSource = arrSource[arrSource.length - 1];
			System.out.println("swf----->>>>>I'm downloading ("+nameSource+") to "+folderPath);
			try {
				response = client.execute(getFile);
				if(response.getEntity().getContent() != null) 
					inputStream = response.getEntity().getContent();
				outputStream = new FileOutputStream(new File(folderPath+"/"+nameSource));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				System.out.println("Finish download file success.");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
		
	}
	
	public static void downloadSource(List<ResultObj> listObj,String folderPath) throws Exception {
		for(ResultObj obj : listObj) {
			String path = obj.getLinkImg();
			String arrSource [] = path.split("/");
			String nameSource = arrSource[arrSource.length - 1];
			
			System.out.println("img----->>>>>I'm downloading ("+nameSource+") to "+folderPath);
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

			FileOutputStream fos = new FileOutputStream(folderPath+"/"+nameSource);
			fos.write(response);
			fos.close();
		}
	}
	
	public static void parseWebsite(List<ObjWeb> listObjWeb) throws Exception {
		ResultObj obj;
		List<ResultObj> listObj = new ArrayList<ResultObj>();
		int count = 1;
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = sf.format(now).replace("-", "");
		String menu = listObjWeb.get(0) != null ? listObjWeb.get(0).getMenu() : "fuck";
		createFolder(configPath+dateString);
		createFolder(configPath+dateString+"/"+menu);
		for(ObjWeb input : listObjWeb) {
			Element body = input.getBody();
			if (body.select("#game>#game_embed") == null || body.select("#game>#game_embed").size() == 0) {
				return;
			}
			Element gameEmbed = body.select("#game>#game_embed").get(0);
			String linkDataSource = gameEmbed.attr("data-src").toString();
			String linkImgSource = input.getImg();
			String arrNameDataSource [] = linkDataSource.split("/");
			String nameDataGame = arrNameDataSource[arrNameDataSource.length - 1];
			String arrSource [] = linkImgSource.split("/");
			String nameImgSource = arrSource[arrSource.length - 1];
			if(nameDataGame.indexOf(".swf") != -1) {
				obj = new ResultObj();
				obj.setLinkSwf(linkDataSource);
				obj.setLinkImg(linkImgSource);
				obj.setId(String.valueOf(count));
				obj.setSwf(nameDataGame);
				obj.setImg(nameImgSource);
				obj.setTitle(input.getTitle());
				listObj.add(obj);
			}
		}
		System.out.println("===============>Total "+listObj.size()+"files in "+menu.toUpperCase()+" was found<===============");
		System.out.println("\n");
		System.out.println("*****************************************************************");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Downloading file ...");
		readToExcel(configPath+dateString+"/"+menu, listObj);
		//saveFileFromUrl(listObj, configPath+dateString+"/"+listObjWeb.get(0).getMenu());
		//downloadSource(listObj, configPath+dateString+"/"+listObjWeb.get(0).getMenu());
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
		List<ObjWeb> listObjectWeb = new ArrayList<ObjWeb>();
        Element body = httpClient(menuUrl);
        /*FileOutputStream fos = new FileOutputStream(configPathMac+"/action/action.html");
        byte[] content = body.html().getBytes();
        fos.write(content);*/
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
        	listObjectWeb.add(obj);
        }
      parseWebsite(listObjectWeb);
	}
	
	
    public static void main( String[] args )
    {
        System.out.println( "*****Begin find games*****" );
        System.out.println("...running..");
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
