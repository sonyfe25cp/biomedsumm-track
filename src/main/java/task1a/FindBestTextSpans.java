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

	private static List<PaperInstance> instances = new ArrayList<>();
	
	
	public static List<PaperInstance> getInstances() {
		return instances;
	}

	static{
		List<Instance> complete = ParseFromDataAnn.loadDataSet();
		for(Instance instance : complete){
			instances.addAll(instance.paperInstances);
		}
	}
	
	public static void main(String[] args) {
		FindBestTextSpans.debug();
	}
	
	static void debug(){
		for(PaperInstance instance : instances){
			debugPaperInstance(instance);
		}
		logger.info("样本共{}个", instances.size());
	}

	/**
	 * 查看解析结果
	 * @param instance
	 */
	static void debugPaperInstance(PaperInstance instance) {
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
