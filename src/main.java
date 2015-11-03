import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//StanfordCoreNLP pipeline = new StanfordCoreNLP();
		File folder = new File("docs");
		File[] files = folder.listFiles();
		try {
		for(File file:files){
			Document doc = Jsoup.parse(file,"ASCII");
			Element title = doc.select("title").first();
			String name = title.text();
			Pattern p = Pattern.compile(" -");
			Matcher m = p.matcher(name);
			if(m.find())new Page(name.substring(0,m.start()),doc);
	/*		Elements divs = doc.select("div");
			for(Element e:divs){
				System.out.println(e.text());
			}*/
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
