package task1a;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Citantion;
import model.Instance;
import model.PaperInstance;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prepare.ParseFromDataAnn;
import utils.Stemmer;
import utils.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class LoadLabel {

    static Logger logger = LoggerFactory.getLogger(LoadLabel.class);
    public static void main(String[] args) {
        LoadLabel ll = new LoadLabel();
//        ll.load();
        try {
            String sentence = "wo ai beijing tianan men";
            ll.testBC(sentence);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void load(){
        ParseFromDataAnn pfda = new ParseFromDataAnn();
        String rootPath = Utils.loadPath();
        List<Instance> batchParse = pfda.batchParse(rootPath);
        
        List<Unit> units = new ArrayList<>();
        
        for(Instance instance : batchParse){
            List<PaperInstance> paperInstances = instance.paperInstances;
            for(PaperInstance paperInstance : paperInstances){
                List<Citantion> citantions = paperInstance.citantions;
                for(Citantion citantion : citantions){
                    String citationText = citantion.citationText;
                    String facet = citantion.facet;
//                    logger.info("{} --- {}", facet, citationText);
                    Unit unit = new Unit(facet, citationText);
                    units.add(unit);
                }
            }
        }
        
        Map<String, Integer> py = new HashMap<>();
        Map<String, Integer> pxy = new HashMap<>();
        
        for(Unit unit : units){
            String facet = unit.facet;
            String citationText = unit.citationText;
            logger.info(citationText);
            List<String> simpleCut = simpleCut(citationText);
            
            Integer integer = py.get(facet);
            if(integer == null){
                integer=0;
            }
            integer++;
            py.put(facet, integer);
            
            for(String x : simpleCut){
                String key = key(x, facet);
                Integer integer2 = pxy.get(key);
                if(integer2 == null){
                    integer2 =0;
                }
                integer2++;
                pxy.put(key, integer2);
            }
        }
        
        Gson gson = new Gson();
        String pyjson = gson.toJson(py);
        String pxyjson = gson.toJson(pxy);
        File pyfile = new File("/tmp/py");
        File pxyfile = new File("/tmp/pxy");
        try {
            FileUtils.write(pyfile, pyjson);
            FileUtils.write(pxyfile, pxyjson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static String key(String x, String y){
        return x+"$$$"+y;
    }
    
    static Set<String> stops = new HashSet<>();
    static {
        stops.add("a");
        stops.add("an");
        stops.add("the");
        stops.add("is");
        stops.add("are");
        stops.add("et");
        stops.add("al");
        stops.add("in");
        stops.add("le");
        stops.add("or");
        stops.add("of");
        stops.add("to");
        stops.add("and");
        stops.add("on");
    }
    final static Pattern pureNum = Pattern.compile("\\d+");
    public static List<String> simpleCut(String citationText){
        List<String> array = new ArrayList<>();
        String[] split = citationText.split("[ ?!\\.,)(\\]\\[{}]");
        for(String str : split){
            str = str.toLowerCase();
            if(str.trim().length() == 0){
                continue;
            }
            if(stops.contains(str)){
                continue;
            }
            Matcher matcher = pureNum.matcher(str);//含数字的词都不要
            if(matcher.find()){
                String o = str;
                str = "XXXXXXX";
                logger.info("{} == > {}", o, str);
            }
            Stemmer stemmer = new Stemmer();
            String stem = stemmer.stem(str);
            
            array.add(stem);
        }
        return array;
    }
    
    public static String testBC(String sentencesX) throws IOException{
        File pyfile = new File("/tmp/py");
        File pxyfile = new File("/tmp/pxy");
        String pystr = FileUtils.readFileToString(pyfile);
        String pxystr = FileUtils.readFileToString(pxyfile);
        
        Gson gson = new Gson();
        
        Map<String, Integer> py = gson.fromJson(pystr, new TypeToken<Map<String, Integer>>() {}.getType());
        Map<String, Integer> pxy = gson.fromJson(pxystr, new TypeToken<Map<String, Integer>>() {}.getType());
        
        logger.info("py.size: {}", py.size());
        logger.info("pxy.size: {}", pxy.size());
        
        
        List<String> simpleCut = simpleCut(sentencesX);
        Map<String, Integer> map = new HashMap<>();
        for(String str : simpleCut){
            for(String fac : facets){
                String key = key(str, fac);
                Integer pyvalue = py.get(fac);
                Integer pxyvalue = pxy.get(key);
                if(pxyvalue == null){
                    System.out.println(key);
                    continue;
                }
                int tmpfenzi = pyvalue * pxyvalue;
                Integer integer = map.get(fac);
                if(integer == null){
                    integer = 0;
                }
                integer += tmpfenzi;
                map.put(fac, integer);
            }
        }
        int max = 0;
        String best = null;
        for(Entry<String, Integer> entry : map.entrySet()){
            String fac = entry.getKey();
            Integer value = entry.getValue();
            logger.info("{} - {}", entry.getKey(), entry.getValue());
            if(value.intValue() > max){
                max = value.intValue();
                best = fac;
            }
        }
        if(best == null){
            logger.info("random select ");
            Random random = new Random();
            int pos = Math.round(random.nextFloat() * (facets.length-1));
           best  = facets[pos];
        }
        logger.info("best : {}", best);
        return best;
        
    }
    
    
    
    
    static String[] facets = {"Hypothesis_Citation", "Method_Citation", "Results_Citation", "Implication_Citation", "Discussion_Citation"};
    
    class Unit{
        String facet;
        String citationText;
        public Unit(String facet, String citationText) {
            super();
            this.facet = facet;
            this.citationText = citationText;
        }
        
    }
    
}
