import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
	private double avgDocLength;
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
	
	public PriorityQueue<Page> find(String query) {
		Comparator<Page> comparator = new Comparator<Page>() {

			@Override
			public int compare(Page o1, Page o2) {
				if(o1.score > o2.score) {
					return -1;
				} else if (o1.score < o2.score) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		PriorityQueue<Page> scoredPages = new PriorityQueue<Page>(comparator);
		String[] searchTerms = query.split("\\s+");
		ArrayList<Page> relevantPages = getHits(searchTerms);
		for(Page page : relevantPages) {
			double score = score(page, searchTerms);
			page.score = score;
			scoredPages.add(page);
		}
		return scoredPages;
	}

	public double idf(String term) {
		int N = this.pages.size();
		int numDocsContains = this.numOccurrences.get(term).getVal();
		
		return Math.log((N - numDocsContains + 0.5) / (numDocsContains + 0.5));
	}

	public double score(Page page, String[] terms) {
		double k1 = 1.2;
		double b = 0.75;
		double b1 = 0.25;
		HashMap<String, MutableInt> hitList = page.getHitList();
		double totalScore = 0;
		for (String term : terms) {
			double idfVal = idf(term);
			if(idfVal < 0.5) {
				idfVal = 0.5;
			}
			double frequency = hitList.get(term).getVal();
			double docPercent = page.documentSize / this.avgDocLength;
			totalScore += idfVal * (frequency / (frequency + (k1 * (b1 + b * docPercent))));
		}
		totalScore *= (1 + k1);
		
		return totalScore;
	}
}
