package model;

public class Paper {
	

	public int id;
	public String fileName;
	public String shortText;
	public String wholeText;
	
	
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
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public String getWholeText() {
		return wholeText;
	}
	public void setWholeText(String wholeText) {
		this.wholeText = wholeText;
	}
	
	
	
}
