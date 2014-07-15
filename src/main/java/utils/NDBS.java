package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.tfidf.Distance;
import utils.tfidf.Similarity;


public class NDBS {
    static Logger logger = LoggerFactory.getLogger(NDBS.class);
    private double radius = 1;

    public static void main(String[] args) {
        NDBS cluster = new NDBS();
        cluster.prepare();
        cluster.loop();

    }

    int size = 100;

    private void prepare() {
        for (int i = 0; i < 100; i++) {
            double x = randomPoint();
            double y = randomPoint();
            Sample sample = new Sample();
            sample.feature = new double[] { x, y };
            samples.add(sample);
        }
        try {
            String output = "/tmp/" + NDBS.class.getName() + "-points.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output)));
            for (Sample sample : samples) {
                double[] feature = sample.feature;
                String str = feature[0] + "\t" +feature[1];
                sample.name = str;
                logger.info(str);
                bw.write(str+"\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int range = 100;

    private double randomPoint() {
        Random random = new Random();
        double point = random.nextDouble() * range;
        return point;
    }

    List<Sample> samples = new ArrayList<>();

    double minR = Double.MAX_VALUE;
    double maxR = Double.MIN_VALUE;
    void loop() {
        for (int i = 0; i < samples.size(); i++) {
            Sample a = samples.get(i);
            for (int j = i + 1; j < samples.size(); j++) {
                Sample b = samples.get(j);
                double distance = distance(a, b);
                Similarity similarity = new Similarity(a.name, b.name, distance);
                similarities.add(similarity);
                
                minR = Math.min(minR, distance);
                maxR = Math.max(maxR, distance);
            }
        }
        
        logger.info("minR : {}, maxR :{}", minR, maxR);
        radius = (minR + maxR) /2 ;
        
        for (Sample sample : samples) {
            double dens = density(sample);
            sample.density = dens;
        }
        Collections.sort(samples);

        for (Sample sample : samples) {
            double distance = nearestBigBrother(sample);
            sample.distance = distance;
        }
        String output = "/tmp/" + NDBS.class.getName() + ".txt";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output)));
            for (Sample sample : samples) {
                double x = sample.density;
                double y = sample.distance;
                bw.write(x + "\t" + y+"\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double distance(Sample sampleA, Sample sampleB) {
        
        double distance = Distance.euler(sampleA.feature, sampleB.feature);
//        logger.info("A: {}, B: {}, distance :{}", new String[]{sampleA.name, sampleB.name, distance+""});
        return distance;
    }

    private double nearestBigBrother(Sample sam) {
        double min = maxR;
        double density = sam.density;
        for (Sample sample : samples) {
            if (sample.name.equals(sam.name)) {
                continue;
            }
            double tmp = sample.density;
            if (tmp - density > 0) {
                double tmpD = distance(sample, sam);
                min = Math.min(tmpD, min);
            }
        }
        return min;
    }

    List<Similarity> similarities = new ArrayList<>();

    /**
     * no contains itself
     * 
     * @param sample
     * @return
     */
    private double density(Sample sample) {
        String name = sample.name;
        int count = 0;
        for (Similarity similarity : similarities) {
            if (similarity.a.equals(similarity.b)) {
                continue;
            }
            if (similarity.a.equals(name) || similarity.b.equals(name)) {
                double dis = similarity.sim;
                if (dis < radius) {
                    count++;
                }
            }
        }
        return count;
    }

    class Sample implements Comparable<Sample> {

        String name;

        double density;

        double distance;

        double[] feature;

        @Override
        public int compareTo(Sample o) {
            if (density > o.density) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
