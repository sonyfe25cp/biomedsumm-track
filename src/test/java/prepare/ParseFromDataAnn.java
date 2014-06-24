package prepare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Citantion;
import model.Instance;
import model.Paper;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseFromDataAnn {
	static Logger logger = LoggerFactory.getLogger(ParseFromDataAnn.class);

	public static void main(String[] args) {
		ParseFromDataAnn pfda = new ParseFromDataAnn();
		pfda.batch();
	}
	
	/**
	 * 输入根路径（data）
	 * @param folder
	 * @return
	 */
	public List<List<Instance>> batchParse(String dataPath){
		List<List<Instance>> groups = new ArrayList<>();
		File rootFolder = new File(dataPath);
		for(File subFolder : rootFolder.listFiles()){
			List<Instance> group = new ArrayList<>();
			for(File diffFolder : subFolder.listFiles()){
				String folderName = diffFolder.getName();
				List<MergeSummary> summaries;
				List<Paper> papers;
				List<List<Citantion>> originGroups = new ArrayList<>(subFolder.list().length);
				switch(folderName){
				case "Annotation":
					for(File differentPersonAnn : diffFolder.listFiles()){
						List<Citantion> onePerson = parseCitantionFile(differentPersonAnn);
						originGroups.add(onePerson);
					}
					break;
				case "Documents_PDF":
					break;
				case "Documents_Text":
					papers = batchReadOriginFile(diffFolder);
					break;
				case "Summary":
					summaries = batchReadSummary(diffFolder);
					break;
				}
			}
			
		}
		
		
		return groups;
	}
	
	List<MergeSummary> batchReadSummary(File folder){
		List<MergeSummary> summaries = new ArrayList<>();
		for(File file : folder.listFiles()){
			try {
				MergeSummary ms = new MergeSummary();
				String name = file.getName();
				//D1401_TRAIN.A.ann
				String[] tmps = name.split("\\.");
				String topicID = tmps[0];
				String annotator = tmps[1];
				String readFileToString = FileUtils.readFileToString(file);
				ms.annotator = annotator;
				ms.content = readFileToString;
				ms.topicId = topicID;
				summaries.add(ms);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return summaries;
	}
	

	/**
	 * 读取全文
	 * @param folder
	 * @return
	 */
	List<Paper> batchReadOriginFile(File folder){
		List<Paper> papers = new ArrayList<>();
		for(File file : folder.listFiles()){
			String name = file.getName();
			if(!name.endsWith(".txt")){
				continue;
			}
			try {
				String readFileToString = FileUtils.readFileToString(file);
				Paper paper = new Paper();
				paper.fileName = name;
				paper.wholeText = readFileToString; 
				papers.add(paper);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return papers;
	}
	
	void batch() {
		String path = "/home/coder/data/TAC_2014_BiomedSumm_Training_Data/data/D1401_TRAIN/Annotation/D1401_TRAIN.A.ann.txt";
		File file = new File(path);
		parseCitantionFile(file);
	}

	List<Citantion> parseCitantionFile(File file) {
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
			System.out.println(line);
			System.out.println("----");
			String[] tmps = line.split("\\|");
			for (String tmp : tmps) {
				String key = tmp.substring(0, tmp.indexOf(":")).trim().replaceAll(" ", "_");
				String value = tmp.substring(tmp.indexOf(":") + 1).trim();
				logger.info("key : {}, value : {}", key, value);
				map.put(key, value);
			}
			Citantion ci = map2Citantion(map);
			citantions.add(ci);
		}
		
		for(Citantion ci : citantions){
			logger.info(ci.toString());
		}
		
		return citantions;
	}
	private Citantion map2Citantion(Map<String, String> map){
		Citantion citantion = new Citantion();
		citantion.topicId = map.get("Topic_ID");
		citantion.annotator = map.get("Annotator");
		citantion.facet = map.get("Discourse_Facet");
		
		citantion.referenceText = map.get("Reference_Text");
		citantion.referenceOffset = map.get("Reference_Offset");
		
		citantion.citationText = map.get("Citation_Text");
		citantion.citationOffset = map.get("Citation_Offset");
		citantion.citationMarkerOffset = map.get("Citation_Marker_Offset");
		citantion.citationMarker = map.get("Citation_Marker");
		
		citantion.CP = new Paper(map.get("Citing_Article"));
		citantion.RP = new Paper(map.get("Reference_Article"));
		
		return citantion;
	}
}
class MergeSummary{
	public String annotator;
	public String topicId;
	public String content;
	
}

