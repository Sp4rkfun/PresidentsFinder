import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	public ArrayList<String> links=new ArrayList<String>();
	public String name;
	public Page(String name,Document doc){
		this.name=name;
		Elements links = doc.select("link");
		for(Element link: links){
			System.out.println(link.attr("href"));
		}
		Elements anchors = doc.select("a");
		for(Element anchor: anchors){
			System.out.println(anchor.attr("href"));
		}
	}
}
