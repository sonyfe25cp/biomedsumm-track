package prepare;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import model.Paper;

public class CreateFeatures {

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

    Map<String, Double[]> lsaResultsMaper(String cp){
        Map<String, Double[]>  map = new HashMap<>();
        
        
        
        
        
        
        
        
        return map;
    }
    
}
