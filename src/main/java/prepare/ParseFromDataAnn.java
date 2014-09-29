package prepare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.Citantion;
import model.Instance;
import model.Paper;
import model.PaperInstance;
import model.SummaryInstance;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Utils;

/**
 * @author Chen Jie
 *
 * Jul 9, 2014
 */
public class ParseFromDataAnn {
	static Logger logger = LoggerFactory.getLogger(ParseFromDataAnn.class);

	public static void main(String[] args) {
		ParseFromDataAnn pfda = new ParseFromDataAnn();
		String rootPath = Utils.loadPath();
		pfda.batchParse(rootPath);
	}
	
	public static List<Instance> loadDataSet(){
		String path = Utils.loadPath();
		List<Instance> batchParse = batchParse(path);
		return batchParse;
	}
	
	
	/**
	 * 输入根路径（data）
	 * @param folder
	 * @return
	 */
	public static List<Instance> batchParse(String dataPath){//data
		List<Instance> instances = new ArrayList<>();
		File rootFolder = new File(dataPath);
		for(File subFolder : rootFolder.listFiles()){//tran1
			if(subFolder.isFile()){
				continue;
			}
			List<List<Citantion>> newGroups = new ArrayList<>();
			Map<String, String> summaries = null;
			Map<String, Paper> papers = null;
			List<List<Citantion>> originGroups = new ArrayList<>();
			String topicId = subFolder.getName();//任务号
			for(File diffFolder : subFolder.listFiles()){//summary,pdf,ann,
				String folderName = diffFolder.getName();
				logger.info("begin to parse {} -- {}", topicId, folderName);
				switch(folderName){
				case "Annotation":
					for(File differentPersonAnn : diffFolder.listFiles()){
					    if(differentPersonAnn.getName().equals(".DS_Store")){
					        continue;
					    }
						List<Citantion> onePerson = parseCitantionFile(differentPersonAnn);
						if(onePerson.size() == 0){
						    logger.error("{} error ", differentPersonAnn.getName());
						}
						originGroups.add(onePerson);
					}
					logger.info("originGroup.size: {}", originGroups.size());
					break;
				case "Documents_PDF":
					break;
				case "Documents_Text":
					papers = batchReadOriginFile(diffFolder);
					break;
				case "Summary":
					summaries = batchReadSummary(diffFolder);
					break;
				default :
				    break;
				}
			}
			//regroup citantion
			Map<String, List<Citantion>> eachCPCitantions = new HashMap<>();
			for(List<Citantion> citantions : originGroups){
				for(Citantion citantion : citantions){
					String key = citantion.getCP().fileName+"-"+citantion.getRP().fileName+"-"+citantion.citationMarker;
					List<Citantion> current = eachCPCitantions.get(key);
					if(current == null){
						current = new ArrayList<>();
					}
					current.add(citantion);
					eachCPCitantions.put(key, current);//按照cp-rp-num 分组，把不同人的放到list中
				}
			}
			
			Instance instance = new Instance();
			List<PaperInstance> paperInstances = new ArrayList<>();
			for(Entry<String, List<Citantion>> entry :eachCPCitantions.entrySet()){
				String key = entry.getKey();
				List<Citantion> citantions = entry.getValue();
				logger.info("key : {}, citantions size : {}", key, citantions.size());

				PaperInstance paperInstance = new PaperInstance();
				paperInstance.citantions = citantions;
				paperInstance.citantionMarker = citantions.get(0).citationMarker;
				String rpFile = citantions.get(0).RP.fileName;
				Paper rp = papers.get(rpFile);
//				rp.shortText = citantions.get(0).referenceText;
				Set<String> referenceSet = new HashSet<>();
				for(Citantion citantion : citantions){
				    Map<String, String> referenceMap = citantion.getReferenceMap();
				    if(referenceMap != null){
        				    for(Entry<String, String> tmpEntry : referenceMap.entrySet()){
        				        String referText = tmpEntry.getValue();
        				        referenceSet.add(referText.trim());
        				    }
				    }
				    citantion.RP = rp;
				}
				Set<String> finedSet = pureSet(referenceSet);
				rp.shortTexts = finedSet;
				paperInstance.RP = rp;
				paperInstances.add(paperInstance);
				newGroups.add(citantions);
			}
			instance.paperInstances = paperInstances;
			System.err.println(newGroups.size());
			
			List<SummaryInstance> summaryInstances = new ArrayList<>();
			for(List<Citantion> citantions : originGroups){
//			    logger.info("citantions .size {}", citantions.size());
				SummaryInstance summaryInstance = new SummaryInstance();
				summaryInstance.annotator = citantions.get(0).annotator;
				summaryInstance.citantions = citantions;
				String rpName = citantions.get(0).RP.fileName;
				summaryInstance.RP = papers.get(rpName);
				List<Paper> cps = new ArrayList<>();
				for(Citantion ci : citantions){
					String cpFile = ci.CP.fileName;
					Paper cp = papers.get(cpFile);
					cps.add(cp);
				}
				summaryInstance.CPs = cps;
				String summaryKey = generateSummaryKey(topicId, summaryInstance.annotator);
				if(summaries != null){
        				String summary = summaries.get(summaryKey);
        				summaryInstance.summary = summary;
        				summaryInstances.add(summaryInstance);
				}
			}
			
			instance.summaryInstances = summaryInstances;
			instances.add(instance);
		}
		return instances;
	}
	
	static Set<String> pureSet(Set<String> origin){
	    Set<String> result = new HashSet<>();
	    List<String> array = new ArrayList<>(origin);
	    Collections.sort(array, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.length() >= o2.length()){
                    return -1;
                }else{
                    return 1;
                }
            }
	    });
	    
	    for(int i = 0; i < array.size(); i++){
	        String first = array.get(i);
//	        logger.info("first ====== : {}", first);
	        boolean flag = true;
	        for(int j = i+1; j < array.size(); j++){
	            String second = array.get(j);
	            if(first.contains(second)){
	                flag = false;
	            }
	        }
	        if(flag){
	            result.add(first);
	        }
	    }
	    
//	    for(String tmp : result){
//	        logger.info("re ---------- {}", tmp);
//	    }
//	    if(result.size() != array.size()){
//	        logger.error("000000000000000000000000000000000000");
//	    }
	    
	    
	    return result;
	}
	
	
	/**
	 * key : topicId - annotator
	 * value : summary
	 * @param folder
	 * @return
	 */
	static  Map<String, String> batchReadSummary(File folder){
		Map<String, String> summaries = new HashMap<>();
		for(File file : folder.listFiles()){
			try {
				String name = file.getName();
				//D1401_TRAIN.A.ann
				String[] tmps = name.split("\\.");
				String topicID = tmps[0];
				String annotator = tmps[1];
				String readFileToString = FileUtils.readFileToString(file);
				String key = generateSummaryKey(topicID, annotator);
				summaries.put(key, readFileToString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return summaries;
	}
	
	/**
	 * 生成摘要文件夹的key，便于快速查找对应topic和annotator的summary
	 * @param topicId
	 * @param annotator
	 * @return
	 */
	private static String generateSummaryKey(String topicId, String annotator){
		return topicId + "-" + annotator;
	}

	/**
	 * 读取全文
	 * @param folder
	 * @return
	 */
	static Map<String, Paper> batchReadOriginFile(File folder){
		Map<String, Paper> papers = new HashMap<>();
		for(File file : folder.listFiles()){
			String name = file.getName();
			if(!name.endsWith(".txt")){
				continue;
			}
			try {
				List<String> readLines = FileUtils.readLines(file);
				StringBuilder sb = new StringBuilder();
				for(String line : readLines){
				    line = line.trim();
				    if(line.equals("References")){
		                logger.info("find the references~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		                break;
		            }
				    sb.append(line+"\n");
				}
				String readFileToString = sb.toString();
				String readWhole = readFileToString;
				Paper paper = new Paper();
				paper.fileName = name;
				paper.wholeText = readWhole;
				paper.originText = readFileToString;
				papers.put(name, paper);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return papers;
	}
	
//	void batch() {
//		String dataFolder = Utils.loadPath();
//		String path = dataFolder + "/D1401_TRAIN/Annotation/D1401_TRAIN.A.ann.txt";
//		File file = new File(path);
//		parseCitantionFile(file);
//	}

	static List<Citantion> parseCitantionFile(File file) {
		List<Citantion> citantions = new ArrayList<>();
		List<String> array = new ArrayList<>();
		try {
			List<String> lines = FileUtils.readLines(file);
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				if (line.length() == 0) {
					array.add(sb.toString());
					sb = new StringBuilder();
				}
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : array) {
			Map<String, String> map = new HashMap<>();
//			System.out.println(line);
//			System.out.println("----");
			String[] tmps = line.split("\\|");
			for (String tmp : tmps) {
				String key = tmp.substring(0, tmp.indexOf(":")).trim().replaceAll(" ", "_");
				String value = tmp.substring(tmp.indexOf(":") + 1).trim();
//				logger.info("key : {}, value : {}", key, value);
				
				if(key.equals("Reference_offset")){
				    key = "Reference_Offset";
				}
				map.put(key, value);
			}
			
			Citantion ci = map2Citantion(map);
			citantions.add(ci);
		}
		
		return citantions;
	}
	private static Citantion map2Citantion(Map<String, String> map){
	    
		Citantion citantion = new Citantion();
		citantion.topicId = map.get("Topic_ID");
		citantion.annotator = map.get("Annotator");
		citantion.facet = map.get("Discourse_Facet");
		citantion.citanceNumber = Integer.parseInt(map.get("Citance_Number"));//没啥用
		
		String referenceText = map.get("Reference_Text");
//		citantion.referenceText = referenceText;
//		logger.info("referenceText: {}", referenceText);
		
		
		String referenceOffset = map.get("Reference_Offset");
		if(referenceOffset != null){
		    referenceOffset = referenceOffset.replaceAll("[\\['\\]]", "");
    //		citantion.referenceOffset = referenceOffset;
    		    
    		    Map<String, String> referenceMap = new HashMap<>();
    		    if(referenceOffset.contains(",")){
    //		    logger.info("referenceOffset: {}", referenceOffset);
    		        String[] split = referenceOffset.split(",");
    		        String[] textSplit = referenceText.split("\\.\\.\\.");
    		        if(split.length != textSplit.length){
    		            logger.error("**********************");
    		            
    //		        logger.info("split.size: {}", split.length);
    //		        logger.info("textSplit.size: {}", textSplit.length);
    //		        logger.info(referenceText);
    		            
    		        }
    		        for(int i = 0; i < split.length; i++){
    		            String offset = split[i];
    		            String textOffset = textSplit[i];
    		            referenceMap.put(offset, textOffset);
    		        }
    		    }else{
    		        referenceMap.put(referenceOffset, referenceText);
    //		    logger.info("referenceOffset ---- {}", referenceOffset);
    		    }
    		    citantion.referenceMap = referenceMap;
		}
		
		citantion.citationText = map.get("Citation_Text");
		citantion.citationOffset = map.get("Citation_Offset");
		
		citantion.citationMarkerOffset = map.get("Citation_Marker_Offset");
		citantion.citationMarker = map.get("Citation_Marker");
		
		citantion.CP = new Paper(map.get("Citing_Article"));
		citantion.RP = new Paper(map.get("Reference_Article"));
		
		return citantion;
	}
}
