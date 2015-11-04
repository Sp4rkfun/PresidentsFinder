import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PageFinder {
	ArrayList<Page> pages = new ArrayList<Page>();
	public double avgDocLength;
	private HashMap<String, MutableInt> numOccurrences;
	public PageFinder() {
		// StanfordCoreNLP pipeline = new StanfordCoreNLP();
		File folder = new File("docs");
		File[] files = folder.listFiles();
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		try {
			int cumDocLength = 0;
			for (File file : files) {
				Document doc = Jsoup.parse(file, "ISO-8859-1");
				Element title = doc.select("title").first();
				String name = title.text();
				Pattern p = Pattern.compile(" -");
				Matcher m = p.matcher(name);

				if (m.find()) {
					Page page = new Page(name.substring(0, m.start()), doc, tagger);
					pages.add(page);
					cumDocLength+=page.documentSize;
				}
				/*
				 * Elements divs = doc.select("div"); for(Element e:divs){
				 * System.out.println(e.text()); }
				 */
			}
			avgDocLength=(double)cumDocLength/files.length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Page> getHits(String[] words) {
		ArrayList<Page> hits = new ArrayList<Page>();
		this.numOccurrences = new HashMap<String, MutableInt>();
		for (Page page : pages) {
			boolean passed = true;
			for (String word : words) {
				if (!page.getHitList().containsKey(word)) {
					passed = false;
					break;
				} else {
					if(this.numOccurrences.containsKey(word)) {
						this.numOccurrences.get(word).incrementVal();
					} else {
						this.numOccurrences.put(word, new MutableInt(1));
					}
				}
			}
			if (passed)
				hits.add(page);
		}
		return hits;
	}
	
	public PriorityQueue<String> find(String query) {
		PriorityQueue<String> scoredPages = new PriorityQueue<String>();
		ArrayList<Page> relevantPages = getHits(query.split("\\s+"));
		return null;
	}

	public static double idf(int docs, int contains) {
		return Math.log((docs - contains + 0.5) / (contains + 0.5));
	}

	public static double score(int docLength, int occurances, int avdocLength) {
		double k1 = 1.2;
		double b = 0.75;
		double b1 = 0.25;
		return occurances / (occurances + k1 * (b1 + b * (docLength / (double) avdocLength)));
	}
}
