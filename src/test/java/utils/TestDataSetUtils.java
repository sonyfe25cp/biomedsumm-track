package utils;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TestDataSetUtils extends TestCase{
	
	@Test
	public void testReadWhole(){
		String path = "/data/TAC_2014_BiomedSumm_Training_Data/data/D1401_TRAIN/Documents_Text/Voorhoeve.txt";
		File file = new File(path);
		
		String readWhole = DataSetUtils.readWhole(file);
		
		List<String> lines = LanguageUtils.cutEnglishTextIntoSentences(readWhole);
		
//		for(String str : lines){
//			System.out.println(str+"\n--------------------------------------------\n");
//		}
		
	}
	
	@Test
	public void testMatchLabel() {
//		String label = "To further substantiate LATS2 as a direct target of miR372&3, we cloned its 3′UTR downstream of the firefly luciferase gene (pGL3-LATS2). We transfected either pGL3-LATS2 or the controls pGL3-372 and pGL3-373 (containing a miR-complementary sequence in their 3′UTR) or pGL3 into Tera1 and MCF-7 cells (respectively positive and negative for miR-371-3) ( Figures 4D and S6) ";
		
		String label = "In a screen for miRNAs that cooperate with oncogenes in cellular transformation, we identified miR-372 and miR-373, each permitting proliferation and tumorigenesis of primary human cells that harbor both oncogenic RAS and active wild-type p53 ... We provide evidence that these miRNAs are potential novel oncogenes participating in the development of human testicular germ cell tumors by numbing the p53 pathway, thus allowing tumorigenic growth in the presence of wild-type p53";
		
		String label1 = "Next, we examined the functionality of the miR-Vec system to suppress gene expression by using both GFP, tagged with a sequence complementary to miR-19, and luciferase containing either the wt 3'UTR of G6PD, a predicted miR-1 target, or control with two mutated miR-1 binding sequences (Lewis et al., 2003)";
		
		
		//3?UTF => 3'UTR
		String label2 = "To further substantiate LATS2 as a direct target of miR372&3, we cloned its 3'UTR downstream of the firefly luciferase gene (pGL3-LATS2). We transfected either pGL3-LATS2 or the controls pGL3-372 and pGL3-373 (containing a miR-complementary sequence in their 3'UTR) or pGL3 into Tera1 and MCF-7 cells (respectively positive and negative for miR-371-3) ( Figures 4D and S6) ... Significantly, a potent inhibition of luciferase activity was also mediated by the 3'UTR of LATS2 in either MCF-7 ectopically expressing miR-372 or in Tera1 cells but not by a construct mutated at both miR-372-predicted target sites.";
		
		List<String> labelLines = LanguageUtils.cutEnglishTextIntoSentences(label1);
		
		for(String line : labelLines){
			System.err.println(line);
		}
		
		String path = "/data/TAC_2014_BiomedSumm_Training_Data/data/D1401_TRAIN/Documents_Text/Voorhoeve.txt";
		File file = new File(path);
		
		String readWhole = DataSetUtils.readWhole(file);
		
		List<String> lines = LanguageUtils.cutEnglishTextIntoSentences(readWhole);
		
		for(String labelLine : labelLines){
			for(String line : lines){
				boolean sameSentence = LanguageUtils.isSameSentence(labelLine, line);
				if(sameSentence){
					System.err.println("haha");
				}
			}
		}
		
	}
	
	
	public void testSim(){
		String str = "In a screen for miRNAs that cooperate with oncogenes in cellular transformation, we identified miR-372 and miR-373, each permitting proliferation and tumorigenesis of primary human cells that harbor both oncogenic RAS and active wild-type p53";
		String str2 = "We provide evidence that these miRNAs are potential novel oncogenes participating in the development of human testicular germ cell tumors by numbing the p53 pathway, thus allowing tumorigenic growth in the presence of wild-type p53.";
		String aim = "In a screen for miRNAs that cooperate with oncogenes in cellular transformation, we identified miR-372 and miR-373, each permitting proliferation and tumorigenesis of primary human cells that harbor both oncogenic RAS and active wild-type p53. These miRNAs neutralize p53-mediated CDK inhibition, possibly through direct inhibition of the expression of the tumor-suppressor LATS2. We provide evidence that these miRNAs are potential novel oncogenes participating in the development of human testicular germ cell tumors by numbing the p53 pathway, thus allowing tumorigenic growth in the presence of wild-type p53.";
		boolean sameSentence = LanguageUtils.isSameSentence(str, aim);
		System.err.println(sameSentence);
	}
	
	

}
