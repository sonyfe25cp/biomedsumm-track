package model;

import java.util.List;
import java.util.Set;

public class Paper {
	

	public int id;
	public String fileName;
	public Set<String> shortTexts;
	public String wholeText;
	public String originText;
	
	
	public Paper(String fileName) {
		super();
		this.fileName = fileName;
	}
	public Paper() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getWholeText() {
		return wholeText;
	}
	public void setWholeText(String wholeText) {
		this.wholeText = wholeText;
	}
	public String getOriginText() {
		return originText;
	}
	public void setOriginText(String originText) {
		this.originText = originText;
	}
    
    public Set<String> getShortTexts() {
        return shortTexts;
    }
    
    public void setShortTexts(Set<String> shortTexts) {
        this.shortTexts = shortTexts;
    }
    
	
	
	
}
