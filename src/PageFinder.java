import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PageFinder {
	public PageFinder() {
		// StanfordCoreNLP pipeline = new StanfordCoreNLP();
		File folder = new File("docs");
		File[] files = folder.listFiles();
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		try {
			for (File file : files) {
				Document doc = Jsoup.parse(file, "ISO-8859-1");
				Element title = doc.select("title").first();
				String name = title.text();
				Pattern p = Pattern.compile(" -");
				Matcher m = p.matcher(name);
				if (m.find()) {
					Page page = new Page(name.substring(0, m.start()), doc, tagger);
					HashMap<String, MutableInt> hitList = page.getHitList();
				}

				/*
				 * Elements divs = doc.select("div"); for(Element e:divs){
				 * System.out.println(e.text()); }
				 */
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
