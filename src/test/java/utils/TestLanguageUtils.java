package utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.process.DocumentPreprocessor;

public class TestLanguageUtils extends TestCase {
	
	static Logger logger = LoggerFactory.getLogger(TestLanguageUtils.class);

	@Test
	public void testSplitSentence() {

		// String text = Utils.getResouce("Cavallo.txt");

		// String text =
		// "Testicular Germ Cell Tumors (TGCT) and patient-derived cell lines are extremely sensitive to cisplatin and other interstrand cross-link (ICL) inducing agents. Nevertheless, a subset of TGCTs are either innately resistant or acquire resistance to cisplatin during treatment. Understanding the mechanisms underlying TGCT sensitivity/resistance to cisplatin as well as the identification of novel strategies to target cisplatin-resistant TGCTs have major clinical implications. Herein, we have examined the proficiency of five embryonal carcinoma (EC) cell lines to repair cisplatin-induced ICLs. Using γH2AX stainingas a marker of double strand break formation, we found that EC cell lines were either incapable of or had a reduced ability to repair ICL-induced damage. The defect correlated with reduced Homologous Recombination (HR) repair, as demonstrated by the reduction of RAD51 foci formation and by direct evaluation of HR efficiency using a GFP-reporter substrate. HR-defective tumors cells are known to be sensitiveto the treatment with poly(ADP-ribose) polymerase (PARP) inhibitor. In line with this observation, we found that EC cell lines were also sensitive to PARP inhibitor monotherapy. The magnitude of sensitivity correlated with HR-repair reduced proficiency and with the expression levels and activity of PARP1 protein. In addition, we found that PARP inhibition strongly enhanced the response of the most resistant EC cells to cisplatin, by reducing their ability to overcome the damage. These results point to a reduced proficiency of HR repair as a source of sensitivity of ECs to ICL-inducing agents and PARP inhibitor monotherapy, and suggest that pharmacological inhibition of PARP can be exploited to target the stem cell component of the TGCTs (namely ECs) and to enhance the sensitivity of cisplatin-resistant TGCTsto standard treatments.";
		// String text = "Hello . good morning. thanks! bye~";
		String text = "Recently, several cell culture models of human cell transformation have been described in which primary human cells are engineered to express combinations of dominantly acting cellular and viral oncogenes and subsequently measured for anchorage-independent proliferation, an in vitro hallmark of transformation (Elenbaas et al., 2001, Hahn et al., 1999 and Zhao et al., 2004). We sought to identify short-hairpin RNAs (shRNAs) that cooperate within the context of such a model";
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(
				text));
		List<String> sentences = new ArrayList<>();
		for (List sentence : dp) {
			String newS = sentence.toString().replaceAll(",", "");
			sentences.add(newS);
		}

		for (String str : sentences) {
			System.out.println(str);
		}

	}

	@Test
	public void testIsSimilar() {
		String str1 = "I love beijing tianan men";
		String str2 = "I love beijing tianan changcheng";

		boolean flag = LanguageUtils.isSameSentence(str1, str2);

		Assert.assertEquals(true, flag);
	}

	
	@Test
	public void testRemoveNoise(){
		String str = "Of note, miR-373 had been previously identified as a potential oncogene (together with miR-372) in testicular germ-cell tumors (Voorhoeve et al., 2006), although it has been proposed that the prometastatic and the oncogenic properties of this miRNA are due to the regulation of different genes (CD44 and LATS2, respectively)";
		
		String tmp = LanguageUtils.removeCitantionNoise(str);
		
		logger.info(str);
		logger.info(tmp);
	}
	
}
