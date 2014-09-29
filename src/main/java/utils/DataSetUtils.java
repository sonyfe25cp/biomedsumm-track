package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.LabelSentence;
import model.Paper;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Jama.Matrix;
import cn.techwolf.data.utils.vocabulary.Vocabulary;

/**
 * 
 * 该数据集特有问题的工具
 * 
 * @author Chen Jie
 * 
 *         Jul 10, 2014
 */
public class DataSetUtils {

    static Logger logger = LoggerFactory.getLogger(DataSetUtils.class);

    /**
     * 针对提供d文本中缺少标点符号,会造成分句错误d问题 对于一行中没有标点的短句 && 下一行开头是大写 进行处理
     * 
     * @param file
     * @return
     */
    public static String readWhole(File file) {
        String readFileToString = "";
        try {
            readFileToString = FileUtils.readFileToString(file);
            String[] lines = readFileToString.split(System.getProperty("line.separator"));
            for (int i = 0; i < lines.length - 1; i++) {
                String line = lines[i];
                String nextLine = "";
                if (i != line.length() - 2) {
                    nextLine = lines[i + 1];
                }

                boolean flag1 = isBeginWithUpperCase(line);
                boolean flag2 = hasNoSignal(line);
                boolean flag3 = isNotEndWithEndFlag(line);
                boolean flag4 = isBeginWithUpperCase(nextLine);

                //				System.out.println(line+"\n------------------------------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readFileToString;
    }

    private static boolean isNotEndWithEndFlag(String line) {
        return false;
    }

    private static boolean hasNoSignal(String line) {
        return false;
    }

    private static boolean isBeginWithUpperCase(String line) {
        return false;
    }

    /**
     * 把rp中的句子标一下
     * 
     * @param rp
     * @return
     */
    public static List<LabelSentence> labelSentence(Paper rp) {
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
            LabelSentence labelSentenceTmp = new LabelSentence();
            labelSentenceTmp.sentence = sentence;
            for (String txt : shortTexts) {
                if (txt.contains("3?UTR")) {//特殊符号特殊处理
                    txt = txt.replaceAll("3\\?UTR", "3'UTR");
                }
                boolean simFlag = LanguageUtils.isSameSentence(sentence, txt);
                if (simFlag || sentence.contains(txt)) {
                    labelSentenceTmp.label = 1;
                    found.add(txt);
                    continue here;
                }
            }
            labelSet.add(labelSentenceTmp);
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
            logger.error("有没标出的句子:{}, 应有：{}, 总数：{}", new String[] { rp.fileName,
                    shortTexts.size() + "", labelSet.size() + "" });
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

        List<LabelSentence> labelSentence = new ArrayList<>(labelSet);

        //标注数据向量

        Collections.sort(labelSentence, new Comparator<LabelSentence>() {//确保标注数据在上面

                    @Override
                    public int compare(LabelSentence o1, LabelSentence o2) {
                        int label = o1.label;
                        int label2 = o2.label;
                        if (label >= label2) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

        Vocabulary voc = new Vocabulary();
        voc.setStopWords(true);
        voc.setSingleWord(false);

        //        Map<String, Integer> sentenceMarker = new HashMap<>();

        for (int i = 0; i < labelSentence.size(); i++) {
            LabelSentence labelSentence2 = labelSentence.get(i);
            String sentence = labelSentence2.sentence;
            voc.addText(sentence);
        }
        voc.addOver();
        int high = Math.round(labelSentence.size() / 1.5f);
        voc.adjust(5, high);
        Map<String, Integer> weightMap = voc.getPosMap();

        int wordsSize = weightMap.size();

        Matrix matrix = new Matrix(labelSentence.size(), wordsSize);
        for (int i = 0; i < labelSentence.size(); i++) {
            LabelSentence labelSentence2 = labelSentence.get(i);
            String sentence = labelSentence2.sentence;

            List<String> cut = voc.cut(sentence);
            for (String word : cut) {
                Integer position = voc.getPosition(word);
                if (position == null) {
                    continue;
                }
                int j = position.intValue();
                Double weight = voc.getWordWeight(word);
                matrix.set(i, j, weight);
            }
        }

        int rowDimension = matrix.getRowDimension();
        int columnDimension2 = matrix.getColumnDimension();
        logger.info("row: {}, col: {}", rowDimension, columnDimension2);

        Matrix lsa = MatrixUtils.lsa(matrix);
//        MatrixUtils.printMatrixUtils(lsa);

        for (int i = 0; i < labelSentence.size(); i++) {
            LabelSentence labelSentence2 = labelSentence.get(i);
            int label = labelSentence2.label;
            double[] head = new double[] { label };
            double[] matrixRow = getMatrixRow(matrix, i);
            
            double[] addhead = addhead(head, matrixRow);
            labelSentence2.vectors = addhead;
        }

        return labelSentence;
    }

    private static double[] getMatrixRow(Matrix matrix, int rowCount) {
        int columnDimension = matrix.getColumnDimension();
        double[] v = new double[columnDimension];
        for (int i = 0; i < columnDimension; i++) {
            double d = matrix.get(rowCount, i);
            v[i] = d;
        }
        return v;
    }

    private static double[] addhead(double[] head, double[] left) {
        double[] result = new double[head.length + left.length];
        for (int i = 0; i < head.length; i++) {
            result[i] = head[i];
        }
        for (int i = 0; i < left.length; i++) {
            result[i + head.length] = left[i];
        }
        return result;
    }
}
