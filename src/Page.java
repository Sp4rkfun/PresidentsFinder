import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Page {
	private ArrayList<String> links=new ArrayList<String>();
	private String name;
	public String getName() {
		return name;
	}
	
	private ArrayList<String> tags = new ArrayList<String>();
	private String nonTags = ", . .$$. `` '' : $ # -LRB- -RRB-";
	private HashMap<String, MutableInt> hitList = new HashMap<String, MutableInt>();
	
	public Page(String name,Document doc){
		this.name=name;
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		int index = 0;
		while(index < 46) {
			String tag = tagger.getTag(index);
			if(!nonTags.contains(tag)) {
				tags.add(tagger.getTag(index));
			}
			index++;
		}
		
		Elements links = doc.select("link, a");
		for(Element link: links){
			this.links.add(link.attr("href"));
		}
		Elements ps = doc.select("p,li,h1,h2,h3,h4");
		for(Element p: ps){
			String sentence = p.text().trim();
			String tagged = tagger.tagString(sentence);
			String[] taggedWords = tagged.split("\\s+");
			for(int i = 0; i < taggedWords.length; i++) {
				String word = taggedWords[i];
				String nextWord = null;
				if(i < taggedWords.length-1) {
					nextWord = taggedWords[i+1];
				} else {
					nextWord = "";
				}
				if(word.length() < 2) continue;
				String[] wordAndTag = word.split("_");
				if(this.tags.contains(wordAndTag[1])) {
					if(nextWord.contains("POS")) {
						addHit(wordAndTag[0].trim() + nextWord.split("_")[0]);
					} else {
						if(!wordAndTag[0].contains("?")) {
							addHit(wordAndTag[0]);
						}
					}
				} 
			}
		}
	}
	
	
	private void addHit(String word) {
		if(this.hitList.containsKey(word)) {
			this.hitList.get(word).incrementVal();
		} else {
			this.hitList.put(word, new MutableInt(1));
		}
	}
	
	public HashMap<String, MutableInt> getHitList() {
		return this.hitList;
	}
}

class MutableInt {
	private int value;
	
	public MutableInt(int input) {
		this.value = input;
	}
	
	public void incrementVal() {
		this.value++;
	}
	
	public int getVal() {
		return this.value;
	}
}





