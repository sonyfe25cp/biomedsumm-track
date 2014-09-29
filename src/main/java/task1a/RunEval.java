package task1a;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import model.Citantion;
import model.Instance;
import model.Paper;
import model.PaperInstance;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prepare.ParseFromDataAnn;
import utils.LanguageUtils;
import utils.Utils;
import cn.techwolf.data.utils.vocabulary.Distance;
import cn.techwolf.data.utils.vocabulary.Vocabulary;


public class RunEval {

    static Logger logger = LoggerFactory.getLogger(RunEval.class);
    
    public static void main(String[] args) {
        RunEval re = new RunEval();
        re.runMaxSim();
        re.runTop(3);
        re.runTop(5);
        System.out.println(ccc+"个没搞定");
    }
    
    void runMaxSim(){
        ParseFromDataAnn pfda = new ParseFromDataAnn();
        String rootPath = Utils.EvaPoath;
        List<Instance> batchParse = pfda.batchParse(rootPath);
        String runId = "cm";
        File output = new File("/tmp/bt/"+runId);
        if(output.exists()){
            output.delete();
        }
        
        for(Instance instance : batchParse){
            List<PaperInstance> paperInstances = instance.paperInstances;
            for(PaperInstance paperInstance : paperInstances){
                List<Citantion> citantions = paperInstance.citantions;
                for(Citantion citantion : citantions){
                    String citationText = citantion.citationText;
                    String citationMarker = citantion.citationMarker;
                    Paper rp = citantion.RP;
                    String fileName = rp.getFileName();
//                    logger.info("marker : {}", citationMarker);
                    logger.info("ct : {}, rp: {}", citationText, fileName);
                    String wholeText = rp.getWholeText();
                    
                    List<String> candicates = LanguageUtils.cutTextSentences(wholeText);
                    double max = 0;
                    Vocabulary voc = new Vocabulary();
                    voc.setStopWords(true);
                    for(String candicate : candicates){
                        voc.addText(candicate);
                    }
                    voc.addOver();
                    double[] cvector = voc.generateVector(citationText);
                    String best = null;
                    for(String candicate : candicates){
                        double[] generateVector = voc.generateVector(candicate);
                        double cosine = Distance.cosine(cvector, generateVector);
                        if(cosine > max){
                            max = cosine;
                            best = candicate;
                        }
                    }
                    
                    logger.info("max : {}, best :{}", max, best);
                    
                    if(best == null || best.length() == 0){
                        Random random = new Random();
                        int pos = Math.round(random.nextFloat() * candicates.size());
                        best = candicates.get(pos);
                        logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!not find");
                    }
                    
                    
                    String result = createResult(citantion.topicId, citantion.citanceNumber, best,  wholeText, runId);//cm:cosineMax
                    if(result == null){
                        logger.info("citext :{}", citantion.citationText);
                        logger.error("name : {}, topicId : {}",rp.fileName, citantion.topicId);
                        System.exit(0);
                    }
                    
                    result = result +"\n";
                    try {
                        FileUtils.write(output, result, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
    }

    
    void runTop(int top){
        ParseFromDataAnn pfda = new ParseFromDataAnn();
        String rootPath = Utils.EvaPoath;
        List<Instance> batchParse = pfda.batchParse(rootPath);
        String runId = "t"+top;
        File output = new File("/tmp/bt/"+runId);
        if(output.exists()){
            output.delete();
        }
        
        for(Instance instance : batchParse){
            List<PaperInstance> paperInstances = instance.paperInstances;
            for(PaperInstance paperInstance : paperInstances){
                List<Citantion> citantions = paperInstance.citantions;
                for(Citantion citantion : citantions){
                    String citationText = citantion.citationText;
                    String citationMarker = citantion.citationMarker;
                    Paper rp = citantion.RP;
                    String fileName = rp.getFileName();
//                    logger.info("marker : {}", citationMarker);
                    logger.info("ct : {}, rp: {}", citationText, fileName);
                    String wholeText = rp.getWholeText();
                    
                    List<String> candicates = LanguageUtils.cutTextSentences(wholeText);
                    Vocabulary voc = new Vocabulary();
                    voc.setStopWords(true);
                    for(String candicate : candicates){
                        voc.addText(candicate);
                    }
                    voc.addOver();
                    double[] cvector = voc.generateVector(citationText);
                    
                    Map<Double, String> topMap = new HashMap<>();
                    
                    for(String candicate : candicates){
                        double[] generateVector = voc.generateVector(candicate);
                        double cosine = Distance.cosine(cvector, generateVector);
                        topMap.put(cosine, candicate);
                    }
                    
                    List<Entry<Double, String>> earray = new ArrayList<>(topMap.entrySet());
                    if(earray.size() > 2){
                        try{
                        Collections.sort(earray, new Comparator<Entry<Double, String>>() {
                            @Override
                            public int compare(Entry<Double, String> o1, Entry<Double, String> o2) {
                                Double key = o1.getKey();
                                Double key2 = o2.getKey();
                                
                                double d1 = key.doubleValue();
                                double d2 = key2.doubleValue();
                                if(Double.isNaN(d1) || Double.isNaN(d2)){//0.0/0.0 = NaN
                                    return 1;
                                }
                                
                                if(d1 >= d2){
                                    return -1;
                                }else{
                                    return 1;
                                }
                            }
                        });
                        }catch(Exception  e){
                            for(Entry<Double, String> entry : earray){
                                System.out.println(entry.getKey().doubleValue());
                            }
                            System.exit(0);
                        }
                    }
                    
                    Random random = new Random();
                    int pos = Math.round(random.nextFloat() * top);
                    if(pos >= earray.size()){
                        logger.error("too short..................................................................................");
                        pos = 0 ;
                    }
                    Entry<Double, String> selected = earray.get(pos);
                    String best = selected.getValue();
                    
                    logger.info("max : {}, best :{}", best);
                    
                    
                    
                    String result = createResult(citantion.topicId, citantion.citanceNumber, best,  wholeText, runId);//cm:cosineMax
                    if(result == null){
                        logger.error(rp.fileName);
                        System.exit(0);
                    }
                    result = result +"\n";
                    try {
                        FileUtils.write(output, result, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
    }
    
    static int ccc = 0;
    
    private static String createResult(String topicId, int id, String referText, String wholeText,String runId) {
        
        referText = referText.substring(1, referText.length() -1).trim();
        
        StringBuilder sb = new StringBuilder();
        sb.append(topicId);
        sb.append("|");
        sb.append(id);
        sb.append("|");
        
        int indexOf = -1;
        String head = referText;
        do{
            head = head.substring(0, head.length() /2);
            indexOf = wholeText.indexOf(head);
            
            if(indexOf == -1){
                logger.error("---------------------------------------------------------------------------------");
                logger.info("refer: {}", referText);
                logger.info("head:{}", head);
            }
        }while(indexOf == -1 && head.length() > 2);
        
        if(indexOf == -1){
            ccc++;
        }
        
        int endOf = indexOf+referText.length();
        
        
        String offset = indexOf+"-"+endOf;
        
        sb.append(offset);
        sb.append("|");
        sb.append(referText);
        sb.append("|");
        
        String facet = getFacRandom();
        try {
            facet = LoadLabel.testBC(referText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sb.append(facet);//Hypothesis_Citation,    Method_Citation,    Results_Citation,   Implication_Citation,   Discussion_Citation
        sb.append("|");
        sb.append(runId);
        
        return sb.toString();
        
    }
    
    static String getFacRandom(){
        String[] facets = {"Hypothesis_Citation", "Method_Citation", "Results_Citation", "Implication_Citation", "Discussion_Citation"};
        Random random = new Random();
        int pos = Math.round(random.nextFloat() * (facets.length-1));
        String facet = facets[pos];
        return facet;
    }
    
}
