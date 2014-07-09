package utils.tfidf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * kinds of distance 
 * @author Chen Jie
 * @date 5 Jul, 2014
 */
public class Distance {

    static Logger logger = LoggerFactory.getLogger(Distance.class);
    
    public static double cosine(double[] a, double[] b){
        if(a.length != b.length){
            logger.error("length not match");
            System.exit(0);
        }
        double dotValue = dot(a, b);
        double moa = mo(a);
        double mob = mo(b);
        return dotValue/(moa * mob);
    }
    
    public static double mo(double[] a){
        double res = 0;
        for(double aa : a){
            res += aa*aa;
        }
        return Math.sqrt(res);
    }
    
    public static double dot(double[] a, double[] b){
        if(a.length != b.length){
            logger.error("length not match");
            System.exit(0);
        }
        double res = 0;
        for(int i = 0; i < a.length ; i++){
            res += (a[i]*b[i]);
        }
        return res;
    }
}
