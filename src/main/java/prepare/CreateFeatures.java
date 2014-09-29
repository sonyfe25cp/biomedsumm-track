package prepare;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.LabelSentence;
import model.Paper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DataSetUtils;
import utils.MatrixUtils;
import Jama.Matrix;
import cn.techwolf.data.utils.vocabulary.Vocabulary;

public class CreateFeatures {

    static Logger logger = LoggerFactory.getLogger(CreateFeatures.class);
    
    int lsaLength = 100;

    int featureLength = lsaLength + 2;

    /**
     * make features
     * 头两个特征
     * @param cpSentence
     * @param rpSentence
     * @return
     */
    public double[] makeHead(String cpSentence, String rpSentence) {
        int dis0 = Math.abs(cpSentence.length() - rpSentence.length());
        int dis1 = cross(cpSentence, rpSentence);
        double[] head = new double[] { dis0, dis1 };
        return head;
    }

    private int cross(String cpSentence, String rpSentence) {
        Map<String, Integer> map = new HashMap<>();
        for (String tmp : cpSentence.split(" ")) {
            Integer integer = map.get(tmp);
            if (integer == null) {
                integer = 0;
            }
            integer++;
            map.put(tmp, integer);
        }
        int c = 0;
        for (Entry<String, Integer> entry : map.entrySet()) {
            Integer value = entry.getValue();
            if (value == 2) {
                c++;
            }
        }
        return c;
    }

    Map<String, Double[]> lsaResultsMaper(String cp, Paper rp){
        Map<String, Double[]>  map = new HashMap<>();
        
        
        //create matrix about rp
        List<LabelSentence> labelSentence = DataSetUtils.labelSentence(rp);
        
        
        return null;
    }
    
    
    
    
    
    
    public static void main(String[] args) {
        String text = "Given the extensive involvement of miRNA in physiology, dysregulation of miRNA expression can be associated with cancer pathobiology including oncogenesis [11], proliferation [12], epithelial-mesenchymal transition [13], metastasis [14], aberrations in metabolism [15], and angiogenesis [16], among others";
        Vocabulary voc = new Vocabulary();
        voc.setStopWords(true);
        List<String> cut = voc.cut(text);
        for(String c : cut){
            System.err.println(c);
        }
        
    }
    
}
