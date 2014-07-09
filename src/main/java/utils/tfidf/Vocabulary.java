package utils.tfidf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Vocabulary {

    static Logger logger = LoggerFactory.getLogger(Vocabulary.class);
    
    private Map<String, Integer> wordCountTotal = new ConcurrentHashMap<>();//全部unit中的数量
    
    private Map<String, Integer> wordCountPerUnit = new ConcurrentHashMap<>();//不同词出现的unit数

    private Map<String, Integer> posMap = new ConcurrentHashMap<>(); //词典顺序
    
    private Map<String, Double> weightMap = new ConcurrentHashMap<>();//词重
    
    private boolean overFlag = false;
    
    public Vocabulary() {
        super();
    }
    
    public void saveToDisk(){
        if(!overFlag){
            logger.error("必须计算完毕才能保存到本地文件");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(this);
        String output = "/tmp/"+Vocabulary.class+".voc";
        try {
            FileUtils.write(new File(output), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Vocabulary loadFromDB(){
        String input = "/tmp/"+Vocabulary.class+".voc";
        File file = new File(input);
        if(!file.exists()){
            logger.error("词典文件不存在, {}", input);
        }
        String text;
        try {
            text = FileUtils.readFileToString(file);
            Gson gson = new Gson();
            Vocabulary voc = gson.fromJson(text, Vocabulary.class);
            return voc;
        } catch (IOException e) {
            logger.error("gson转化失败");
            return null;
        }
    }
    
    public int size(){
        if(!overFlag){
            logger.error("先调用addOver()");
            return 0;
        }
        return posMap.size();
    }
    
    public double[] generateVector(String sentence) {
        if(!overFlag){
            logger.error("先调用addOver()");
            return null;
        }
        double[] vector = new double[size()];
        HashMap<String, Integer> counter = wordCount(sentence);
        for(Entry<String, Integer> entry : counter.entrySet()){
            String word = entry.getKey();
            int count = entry.getValue();
            
            Integer pos = posMap.get(word);
            Double weight = weightMap.get(word);
            if(pos ==null || weight == null){
                continue;
            }
            vector[pos.intValue()] = count * weight;
        }
        return vector;
    }
    
    private HashMap<String, Integer> wordCount(String sentence) {
    	String[] englishWords = sentence.split(" ");
//        List<Term> terms = ToAnalysis.parse(sentence);
        HashMap<String, Integer> counter= new HashMap<>();
        for(String word : englishWords){
//            String word = term.getName();
            Integer c = counter.get(word);
            if(c == null){
                c = 0;
            }
            c ++;
            counter.put(word, c);
        }
        return counter;
    }

    public Integer getPosition(String word){
        if(!overFlag){
            logger.error("先调用addOver()");
            return null;
        }
        Integer pos =  posMap.get(word);
        if(pos ==null){
            logger.error("在字典中找不到: {}",word);
            return null;
        }else{
            return pos;
        }
    }
    
    public Double getWordWeight(String word){
        Double weight = weightMap.get(word);
        if(weight == null){
            logger.error("字典中没有这个词: {}", word);
            return null;
        }else{
            return weight;
        }
    }
    
    /**
     * 每次一个描述单位(句子/文章)
     * @param text
     */
    public void addText(String text) {
        if(overFlag){
            logger.info("重新打开输入，需要再次结束才可以继续使用");
            overFlag = false;
        }
        int pos = 0;
        Map<String, Integer> subMapForUnit = new HashMap<>();
//        List<Term> terms = ToAnalysis.parse(text);
//        for (Term term : terms) {
//            String word = term.getName();
        String[] words  = text.split(" ");
        for(String word : words){
            //for pos
            if (!posMap.containsKey(word)) {
                posMap.put(word, pos);
                pos++;
            }
            Integer subCount = subMapForUnit.get(word);
            if(subCount == null){
               subCount = 0;
            }
            subCount++;
            subMapForUnit.put(word, subCount);
        }
        for(Entry<String, Integer> entry : subMapForUnit.entrySet()){
            String word = entry.getKey();
            Integer count = entry.getValue();
            
            //for unit
            Integer unitCount = wordCountPerUnit.get(word);
            if(unitCount == null){
                unitCount = 0;
            }
            unitCount++;
            wordCountPerUnit.put(word, unitCount);
            //for total
            Integer totalCount = wordCountTotal.get(word);
            if(totalCount ==  null){
                totalCount = 0;
            }
            totalCount = totalCount + count;
            wordCountTotal.put(word, totalCount);
        }
    }
    
    /**
     * compute after add
     */
    public void addOver(){
        for(Entry<String, Integer> entry : posMap.entrySet()){
            String word = entry.getKey();
            int totalCount = wordCountTotal.get(word);
            int unitCount = wordCountPerUnit.get(word);
            double idf = Math.log((double)totalCount/unitCount);
            weightMap.put(word, idf);
        }
        logger.info("word weight compute over");
        overFlag = true;
    }
    

}
