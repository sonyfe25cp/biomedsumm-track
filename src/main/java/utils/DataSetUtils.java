package utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import ch.qos.logback.classic.pattern.LineSeparatorConverter;

/**
 * 
 * 该数据集特有问题的工具
 * @author Chen Jie
 *
 * Jul 10, 2014
 */
public class DataSetUtils {

	
	/**
	 * 针对提供d文本中缺少标点符号,会造成分句错误d问题
	 * 对于一行中没有标点的短句 && 下一行开头是大写  进行处理
	 * @param file
	 * @return
	 */
	public static String readWhole(File file){
		String readFileToString = "";
		try {
			readFileToString = FileUtils.readFileToString(file);
			String[] lines = readFileToString.split(System.getProperty("line.separator"));
			for(int i = 0; i< lines.length-1; i++){
				String line = lines[i];
				String nextLine = "";
				if(i != line.length()-2){
					nextLine = lines[i+1];
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
	
}
