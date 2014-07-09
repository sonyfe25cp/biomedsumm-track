package utils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Chen Jie
 *
 * Jul 9, 2014
 */
public class LanguageUtils {

	
	/**
	 * Simple cut algorithm by .?!
	 * @param text
	 * @return
	 */
	public static List<String> cutEnglishTextIntoSentences(String text){
		String[] split = text.split("[\\.?!]");
		List<String> results = new ArrayList<>(split.length);
		for(String tmp : split){
			results.add(tmp.trim());
		}
		return results;
	}
	
	public static void main(String[] args) {
		String text = "Query expansion is one1. of query optimization and plays an important role in proving the performance. The hello world? nono!";
		List<String> cutEnglishTextIntoSentences = LanguageUtils.cutEnglishTextIntoSentences(text);
		for(String tmp : cutEnglishTextIntoSentences){
			System.out.println(tmp);
		}
	}
	
}
