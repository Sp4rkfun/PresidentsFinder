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
	private MaxentTagger tagger;
	public int documentSize = 0;
	
	public Page(String name,Document doc, MaxentTagger inputTagger){
		this.name=name;
		this.tagger = inputTagger;
		int index = 0;
		while(index < 46) {
			String tag = this.tagger.getTag(index);
			if(!nonTags.contains(tag)) {
				tags.add(this.tagger.getTag(index));
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
			String tagged = this.tagger.tagString(sentence);
			String[] taggedWords = tagged.split("\\s+");
			for(int i = 0; i < taggedWords.length; i++) {
				String taggedWord = taggedWords[i];
				String nextWord = null;
				if(i < taggedWords.length-1) {
					nextWord = taggedWords[i+1];
				} else {
					nextWord = "";
				}
				if(taggedWord.length() < 2) continue;
				String[] wordAndTag = taggedWord.split("_");
				String word = wordAndTag[0].trim();
				if(this.tags.contains(wordAndTag[1])) {
					if(nextWord.contains("POS")) {
						addHit(word + nextWord.split("_")[0]);
					} else {
						if(!word.contains("?")) {
							addHit(word);
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
		this.documentSize++;
	}
	
	public HashMap<String, MutableInt> getHitList() {
		return this.hitList;
	}
	public boolean hasWord(String word){
		return hitList.containsKey(word);
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





