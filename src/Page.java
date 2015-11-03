import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	public ArrayList<String> links=new ArrayList<String>();
	public String name;
	public String content="";
	public Page(String name,Document doc){
		this.name=name;
		Elements links = doc.select("link");
		for(Element link: links){
			this.links.add(link.attr("href"));
		}
		Elements anchors = doc.select("a");
		for(Element anchor: anchors){
			this.links.add(anchor.attr("href"));
		}
		Elements ps = doc.select("p,li,h1,h2,h3,h4");
		for(Element p: ps){
			this.content += p.text();
		}
		Elements divs = doc.select("div:not(:has(div))");
		for(Element div: divs){
			String text = div.text();
			this.content+=div.text().trim();
		}
	}
}
