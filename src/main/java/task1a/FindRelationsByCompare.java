package task1a;

import java.util.List;

import model.Citantion;
import model.Paper;
import model.PaperInstance;

/**
 * @author Chen Jie
 *
 * 对比 rp中所有句子与cp句子的相似度,看是否是最高相似度的句子为标注结果
 * Jul 9, 2014
 */
public class FindRelationsByCompare {
	
	
	/**
	 * 1. 拿出所有cp和rp
	 * 2. 把同一个cp句子对应的不同rp放在一起
	 * 3. 对rp分句,并打标签(是否是标注)
	 */
	void prepare(){
		List<PaperInstance> instances = FindBestTextSpans.getInstances();
		for(PaperInstance instance : instances){
			Paper rp = instance.RP;
			List<Citantion> citantions = instance.citantions;
			for(Citantion citantion : citantions){
				String citationText = citantion.getCitationText();
				String annotator = citantion.annotator;
			}
		}
	}
	
	
	
	class SampleForCompare{
		String cpSentence;
		List<LabelSentence> rpSentences;
	}
	class LabelSentence{
		String sentence;
		int label;
	}
}
