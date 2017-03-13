package at.demo.crawldata;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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
	
	public static void parseWebsite(Element body) {
		if (body.select("#game>#game_embed") == null || body.select("#game>#game_embed").size() == 0) {
			return;
		}
		Element gameEmbed = body.select("#game>#game_embed").get(0);
		String swfName = gameEmbed.attr("data-src").toString();
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
	
	public static void getDataByMenu(String menu) throws IOException {
		String homeUrl = "http://www.silvergames.com";
		String menuUrl = homeUrl+"/"+menu;
        Element body = httpClient(menuUrl);
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Sony Vaio EA21FX\\Desktop\\action\\action.html");
        byte[] content = body.html().getBytes();
        fos.write(content);
        Element catContent = body.getElementById("cat_content");
        Element listGames = catContent.child(0);
        Elements games = listGames.select("div>ul");
        for(int i = 0; i < games.size(); i++) {
        	String menuGame = games.get(i).select("a").get(0).attr("href");
        	String gameUrl = homeUrl+menuGame;
        	Element bodyMenuGame = httpClient(gameUrl);
        	parseWebsite(bodyMenuGame);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("--------------end------------");
        	
    }
}
