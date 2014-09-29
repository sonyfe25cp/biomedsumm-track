package utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.process.DocumentPreprocessor;


/**
 * @author Chen Jie
 *
 * Jul 9, 2014
 */
public class LanguageUtils {
	static Logger logger = LoggerFactory.getLogger(LanguageUtils.class);
	
	/**
	 * Simple cut algorithm by .?!
	 * @param text
	 * @return
	 */
//	public static List<String> simpleCutEnglishTextIntoSentences(String text){
//		String[] split = text.split("[\\.?!]");
//		List<String> results = new ArrayList<>(split.length);
//		for(String tmp : split){
//			results.add(tmp.trim());
//		}
//		return results;
//	}
	
	/**
	 * @param text
	 * @return
	 */
	public static List<String> cutEnglishTextIntoSentences(String text){
		String[] lines = text.split(System.getProperty("line.separator"));
		List<String> sentences = new ArrayList<>();
		
		for(String line : lines){
			if(line.trim().length() == 0){
				continue;
			}
			if(!line.endsWith(".")){
				line = line + ".";
			}
			line = line.replaceAll("\\.\\.\\.", ".");
			List<String> sub = cut(line);
			sentences.addAll(sub);
		}
		return sentences;
	}
	
	public static List<String> cutTextSentences(String text){
	    List<String> cutEnglishTextIntoSentences = cutEnglishTextIntoSentences(text);
	    List<String> refined = new ArrayList<>();
	    for(String str : cutEnglishTextIntoSentences){
	        if(str.length() == 1 && str.equals("References")){
	            logger.info("find the references~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	            break;
	        }
	        refined.add(str);
	    }
	    return refined;
	}
	
	
	
	public static List<String> cut(String text){
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(text));
		List<String> sentences = new ArrayList<>();
	    for (List sentence : dp) {
	       String newS = sentence.toString().replaceAll(",", "");
	       sentences.add(newS);
	    }
	    return sentences;
	}
	
	
	public static boolean isSameSentence(String str, String aim){
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(str));
		DocumentPreprocessor dpAim = new DocumentPreprocessor(new StringReader(aim));
		
		Map<String, Integer> map = new HashMap<>();
		
		for(List subdp1 : dp){
			for(Object obj : subdp1){
				Integer count = map.get(obj.toString());
				if(count == null)
					count = 0;
				count ++;
				map.put(obj.toString(), count);
			}
		}
		for(List subdp1 : dpAim){
			for(Object obj : subdp1){
				Integer count = map.get(obj.toString());
				if(count == null)
					count = 0;
				count ++;
				map.put(obj.toString(), count);
			}
		}
		int cross = 0;
		int union = map.size();
		for(Entry<String, Integer> entry : map.entrySet()){
			int value = entry.getValue();
			if(value >= 2){
				cross ++;
			}
		}
		
		double  ratio = (double)(cross+2) / union;
//		logger.info("cross : {}, union : {}, ratio : {}", new String[]{cross+"", union+"", ratio+""});
		
//		logger.info(ratio+"");
//		logger.info("{} vs {}", aim, "");
		if(ratio > 0.9){
			return true;
		}else{
			return false;
		}
	}
	
	
	public static void main(String[] args) {
		String text = "Query expansion is one1. of query optimization and plays an important role in proving the performance. The hello world? nono!";
		List<String> cutEnglishTextIntoSentences = LanguageUtils.cutEnglishTextIntoSentences(text);
		for(String tmp : cutEnglishTextIntoSentences){
			System.out.println(tmp);
		}
	}

	public static String removeCitantionNoise(String cp) {
		
		return cp.replaceAll("\\(.*?\\)|\\[.*?\\]", "");
	}
	
}
