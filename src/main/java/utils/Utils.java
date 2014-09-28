package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;


public class Utils {

    public static final String getResouce(String name) {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(name)) {
            return IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException("not found " + name, e);
        }
    }
    
    public static final List<String> getResourceList(String name){
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(name);
        List<String> lines = null;
        try {
            lines = IOUtils.readLines(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
	
	
	/**
	 * 返回数据文件夹路径
	 * 
	 * @return
	 */
	public static String loadPath() {
		String path = null;
		try {
			InputStream is = Utils.class.getClassLoader().getResourceAsStream("config");
			List<String> readLines = IOUtils.readLines(is);
			if (readLines.size() == 2) {
				path = readLines.get(1);
				System.out.println(path);
			}
		} catch (Exception ignore) {
			System.err.println("Path error about the dataset");
		}
		if (path == null || path.length() == 0) {
			System.err.println("没有找到系统数据路径");
			System.exit(0);
		}
		System.out.println("dataset path is : " + path);
		return path;
	}

	public List<String> splitPaper(String text) {
		List<String> sentences = new ArrayList<>();
		char[] array = text.toCharArray();
		int begin = 0;
		for (int i = 0; i < array.length; i++) {
			char current = array[i];
			boolean endFlag = false;
			switch (current) {
			case '.':
				if (i != array.length - 1) {
					char next = array[i + 1];
					if (next == '.') {

					} else if (next == ' ') {

					} else if (isBigger(next)) {
						endFlag = true;
					}
				} else {
					endFlag = true;
				}
				break;
			case '?':
				if (i != array.length - 1) {
					char next = array[i + 1];
					if (isBigger(next)) {
						endFlag = true;
					}
				} else {
					endFlag = true;
				}
				break;
			}
			if (endFlag) {
				String sentence = text.substring(begin, i);
				begin = i;
				sentences.add(sentence);
			}
		}
		return sentences;
	}

	private static boolean isBigger(char next) {
		Pattern notSmall = Pattern.compile("[A-Z0-9]+");
		Matcher matcher = notSmall.matcher(next + "");
		if (matcher.find()) {
			return true;
		}
		return false;
	}

}
