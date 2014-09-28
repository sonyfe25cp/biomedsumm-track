package task1a;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Citantion;
import model.Paper;
import model.PaperInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.LanguageUtils;
import cn.techwolf.data.utils.vocabulary.Distance;
import cn.techwolf.data.utils.vocabulary.Vocabulary;

/**
 * @author Chen Jie
 * 
 *         对比 rp中所有句子与cp句子的相似度,看是否是最高相似度的句子为标注结果 Jul 9, 2014
 */
public class FindRelationsByCompare {

    static Logger logger = LoggerFactory.getLogger(FindRelationsByCompare.class);

    List<SampleForCompare> samples = new ArrayList<>();

    Vocabulary vocabulary = new Vocabulary();

    public static void main(String[] args) {
        FindRelationsByCompare frb = new FindRelationsByCompare();
        frb.prepare();
        //		frb.compare();
        //		frb.compareLabel();
    }

    /**
     * 1. 拿出所有cp和rp 2. 把同一个cp句子对应的不同rp放在一起 3. 对rp分句,并打标签(是否是标注) 4.
     * 统计全局词频等信息
     */
    void prepare() {
        List<PaperInstance> instances = FindBestTextSpans.getInstances();
        Set<String> rpCounter = new HashSet<>();
        for (PaperInstance instance : instances) {
            Paper rp = instance.RP;
            if (!rpCounter.contains(rp.wholeText)) {
                rpCounter.add(rp.wholeText);
                vocabulary.addText(rp.wholeText);
            }

            List<LabelSentence> labelSentence = labelSentence(rp);

            List<Citantion> citantions = instance.citantions;
            for (Citantion citantion : citantions) {
                String citationText = citantion.getCitationText();
                SampleForCompare sample = new SampleForCompare();
                sample.cpSentence = citationText;
                sample.rpSentences = labelSentence;
                samples.add(sample);
            }
        }
        logger.info("samples.size : " + samples.size() + " -- labelCount : " + labelCount
                + " -- cantLabel : " + cantLabel);
        vocabulary.addOver();
    }

    int labelCount = 0;

    int cantLabel = 0;

    /**
     * 把rp中的句子标一下
     * 
     * @param rp
     * @return
     */
    List<LabelSentence> labelSentence(Paper rp) {
        Set<LabelSentence> labelSet = new HashSet<>();
        Set<String> shortTexts = rp.shortTexts;
        Set<String> found = new HashSet<>();
        String wholeText = rp.wholeText;
        for (String txt : rp.shortTexts) {
            wholeText = wholeText.replace(txt, "");
            found.add(txt);
        }
        List<String> rpsentences = LanguageUtils.cutEnglishTextIntoSentences(rp.wholeText);
        here: for (String sentence : rpsentences) {
            sentence = sentence.substring(1, sentence.length() - 2).trim();
            LabelSentence labelSentence = new LabelSentence();
            labelSentence.sentence = sentence;
            for (String txt : shortTexts) {
                if (txt.contains("3?UTR")) {//特殊符号特殊处理
                    txt = txt.replaceAll("3\\?UTR", "3'UTR");
                }
                boolean simFlag = LanguageUtils.isSameSentence(sentence, txt);
                if (simFlag || sentence.contains(txt)) {
                    labelSentence.label = 1;
                    found.add(txt);
                    continue here;
                }
            }
            labelSet.add(labelSentence);
        }

        for (String find : found) {
            LabelSentence labelSentence = new LabelSentence();
            labelSentence.sentence = find;
            labelSentence.label = 1;
            labelSet.add(labelSentence);
        }

        if (labelSet.size() != shortTexts.size()) {

            for (String tmp : shortTexts) {
                logger.info("label : {}", tmp);
            }
            logger.error("有没标出的句子:{}, 应有：{}, 总数：{}", new String[] { rp.fileName, shortTexts.size() + "", labelSet.size()+""});
            int tc = 0;
            for (LabelSentence tmp : labelSet) {
                if (tmp.label == 1) {
                    logger.info("tmp : {}", tmp.sentence);
                    tc++;
                }
            }
            logger.info("tc : {}", tc);

            if (tc == shortTexts.size()) {
                logger.info("************************************************************");
            }
        }
        return new ArrayList<>(labelSet);
    }

    public void compare() {
        int maxLikeIsLabel = 0;
        int total = 0;
        for (SampleForCompare sample : samples) {
            String cp = sample.cpSentence;
            double cpVector[] = vocabulary.generateVector(cp);

            double maxSim = 0;
            LabelSentence maxLike = null;
            for (LabelSentence rpText : sample.rpSentences) {
                String sentence = rpText.sentence;
                int label = rpText.label;
                double[] rpVector = vocabulary.generateVector(sentence);
                double sim = Distance.cosine(cpVector, rpVector);
                if (sim > maxSim) {
                    maxSim = sim;
                    maxLike = rpText;
                }
            }

            boolean flag = maxLike.label == 1;

            if (flag) {
                maxLikeIsLabel++;
            }
            total++;

            //			logger.info(
            //					"cp sentence : {}, Max like sentence is : {}, similarity : {}, flag : {}",
            //					new String[] { cp, maxLike.sentence, maxSim + "", flag + "" });
        }
        logger.info("最相近句子是标注句的个数为 : {}, 总句子数: {}", maxLikeIsLabel, total);
    }

    /**
     * 比较cp与标注
     */
    public void compareLabel() {
        int maxLikeIsLabel = 0;
        int total = 0;
        for (SampleForCompare sample : samples) {
            String cp = sample.cpSentence;

            cp = LanguageUtils.removeCitantionNoise(cp);//去掉括号,方括号等引用

            double cpVector[] = vocabulary.generateVector(cp);

            for (LabelSentence rpText : sample.rpSentences) {
                int label = rpText.label;
                if (label != 1) {
                    continue;
                }
                String sentence = rpText.sentence;
                //				sentence = LanguageUtils.removeCitantionNoise(sentence);//去掉括号,方括号等引用

                double[] rpVector = vocabulary.generateVector(sentence);
                double sim = Distance.cosine(cpVector, rpVector);
                logger.info("cp sentence : {}, label sentence is : {}, similarity : {}",
                        new String[] { cp, sentence, sim + "" });
            }
        }
        logger.info("最相近句子是标注句的个数为 : {}, 总句子数: {}", maxLikeIsLabel, total);
    }

    class SampleForCompare {

        String cpSentence;

        List<LabelSentence> rpSentences;
    }

    class LabelSentence {

        String sentence;

        int label;
    }

    class NotFoundMatchSentenceException extends RuntimeException {

    }
}
