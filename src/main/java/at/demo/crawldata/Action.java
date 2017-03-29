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

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author nambv
 *
 * Mar 21, 2017
 */

public class Action {
	
	private static String configPath = "/Users/nambv/Desktop/" ;
	
	private static void createFolder(String path) {
		File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println(".......Folder is created at "+path);
            } else {
                System.out.println("Error");
            }
        } 
	}
	
	
	private static Element httpClient(String url) {
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
	
	private static void saveFileFromUrl(ResultObj obj, String folder) {
		String url = obj.getLinkSwf();
		InputStream inputStream = null;
		OutputStream outputStream = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet getFile = new HttpGet(url);
		HttpResponse response;
		String arrSource [] = url.split("/");
		String nameSource = arrSource[arrSource.length - 1];
		System.out.println("swf----->>>>>I'm downloading ("+nameSource+") to "+folder);
		try {
			response = client.execute(getFile);
			if(response.getEntity().getContent() != null) 
				inputStream = response.getEntity().getContent();
			System.out.println(inputStream);
			outputStream = new FileOutputStream(new File(folder+"/"+nameSource));

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
	
	private static void downloadSource(ResultObj obj,String folderPath) {
		String path = obj.getLinkImg();
		String arrSource [] = path.split("/");
		String nameSource = arrSource[arrSource.length - 1];
		
		System.out.println("img----->>>>>I'm downloading ("+nameSource+") to "+folderPath);
		URL url;
		try {
			url = new URL(path);
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readToExcel(ResultObj con,String folder) {
		try {
			String path = folder + "/content.xlsx";
			System.out.println("=============>>Write out put file execel at "+path);
			FileInputStream file = new FileInputStream(new File(path));
			XSSFWorkbook workBook = new XSSFWorkbook(file);
			XSSFSheet sheet = workBook.getSheetAt(0);
			int rownum = sheet.getPhysicalNumberOfRows();
			Row row = sheet.createRow(rownum++);
			String id = con.getId();
			String title = con.getTitle().toString().trim();
			String image = con.getImg();
			String swf = con.getSwf();
			if (row.getCell(0) == null) {
				row.createCell(0);
			}
			row.getCell(0).setCellValue(rownum-1);
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
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		}
	}
	
	private static void parseWebsite(ObjWeb input,String folder) {
		ResultObj obj;
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
			obj.setId(input.getId());
			obj.setSwf(nameDataGame);
			obj.setImg(nameImgSource);
			obj.setTitle(input.getTitle());
			System.out.println(linkDataSource);
			saveFileFromUrl(obj, folder);
			downloadSource(obj, folder);
			readToExcel(obj,folder);
			//listObj.add(obj);
		}
	}
	
	private static void getDataByMenu(String menu, int pageSize, String dateString) throws Exception {
		String homeUrl = "http://www.silvergames.com";
		String folder = configPath+dateString+"/"+menu;
		
		for(int j = 0; j <= pageSize;j++) {
			ObjWeb obj;
			String pageNumber = j == 0 ? "" : "/"+String.valueOf(j);
			String folderName = folder+"-page"+j;
			createFolder(folderName);
			String path = folderName+ "/content.xlsx";
			initExcelFile(path);
			String menuUrl = homeUrl+"/"+menu+pageNumber;
	        Element body = httpClient(menuUrl);
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
	        	obj.setId(String.valueOf(i));
	        	obj.setBody(bodyMenuGame);
	        	obj.setTitle(titleGame);
	        	obj.setImg(imgGameLink);
	        	obj.setMenu(menu);
	        	parseWebsite(obj,folderName);
	        	//listObjectWeb.add(obj);
	        }
		}
      //parseWebsite(listObjectWeb);
	}
	
	private static void initExcelFile(String path) {
		File fileExcel = new File(path);
		if(!fileExcel.exists())
			try {
				fileExcel.createNewFile();
				XSSFWorkbook workbook = new XSSFWorkbook();
		        XSSFSheet sheet = workbook.createSheet("Games");
		        Object[][] datatypes = {
		                {"id", "title", "img","swf"}
		        };
		        int rowNum = 0;
		        System.out.println("Creating excel for first time");

		        for (Object[] datatype : datatypes) {
		            Row row = sheet.createRow(rowNum++);
		            int colNum = 0;
		            for (Object field : datatype) {
		                Cell cell = row.createCell(colNum++);
		                if (field instanceof String) {
		                    cell.setCellValue((String) field);
		                } else if (field instanceof Integer) {
		                    cell.setCellValue((Integer) field);
		                }
		            }
		        }

		        try {
		            FileOutputStream outputStream = new FileOutputStream(path);
		            workbook.write(outputStream);
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }

		        System.out.println("Done");

			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static String getContentBodyAsString(HttpResponse res) throws IOException 
	{
		InputStream is = res.getEntity().getContent();
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		for(int l = bis.read(buff); l != -1; l = bis.read(buff))
		{
			baos.write(buff, 0, buff.length);
			buff = new byte[1024];
		}
		return new String(baos.toByteArray());
	}
	
	public static void main(String args[]) throws ClientProtocolException, IOException {
		System.out.println( "*****Begin find games*****" );
        System.out.println("...running..");
        String [] menu = {"action","racing","shooting","sports","strategy","puzzle","iogames","mmo"};
        String pageSize = "5";
        Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = sf.format(now).replace("-", "");
		createFolder(configPath+dateString);
		try {
			for(int i = 0; i < menu.length; i++) {
				getDataByMenu(menu[i],Integer.parseInt(pageSize),dateString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println("--------------end------------");
		
	}

}
