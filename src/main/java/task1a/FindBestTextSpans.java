package task1a;

import java.util.ArrayList;
import java.util.List;

import model.Citantion;
import model.Instance;
import model.Paper;
import model.PaperInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prepare.ParseFromDataAnn;

/**
 * 根据引用找出该引用在RP中应该是对应哪一句话
 * @author Sonyfe25cp
 *
 * 2014年6月25日
 */
public class FindBestTextSpans {
	static Logger logger = LoggerFactory.getLogger(FindBestTextSpans.class);

	static List<PaperInstance> instances = new ArrayList<>();
	static{
		List<Instance> complete = ParseFromDataAnn.loadDataSet();
		for(Instance instance : complete){
			instances.addAll(instance.paperInstances);
		}
	}
	
	public static void main(String[] args) {
		FindBestTextSpans.prepare();
	}
	
	static void prepare(){
		for(PaperInstance instance : instances){
			compareCitantionAndReferSpan(instance);
		}
		logger.info("样本共{}个", instances.size());
	}

	/**
	 * 对比引用文与原文
	 * @param instance
	 */
	static void compareCitantionAndReferSpan(PaperInstance instance) {
		Paper rp = instance.RP;
		String text = rp.wholeText;
		String spanText = rp.shortText;
		List<Citantion> citantions = instance.citantions;
		for(Citantion citantion : citantions){
			String citationText = citantion.citationText;
			logger.info("citationText: {}", citationText);
			logger.info(" -- ");
			logger.info("referText: {}", spanText);
			logger.info(" -- ");
			logger.info("annotator: {}", citantion.annotator);
			logger.info(" -- ");
			logger.info("citation file : {}, rp:{}", citantion.CP.fileName, citantion.RP.fileName);
			logger.info(" ******************************** ");
//			logger.info("cp :{}, rp:{}, cp-marker : {}, annotator:{}",new String[]{citantion.CP.fileName, rp.fileName, citantion.citationMarker, citantion.annotator});
		}
	}
	
	
}
