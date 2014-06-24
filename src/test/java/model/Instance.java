package model;

import java.util.List;

public class Instance {

	public int id;
	public String summary;
	public String annotator;
	public Paper RP;
	public List<Paper> CPs;
	public List<Citantion> citantions;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAnnotator() {
		return annotator;
	}
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}
	public Paper getRP() {
		return RP;
	}
	public void setRP(Paper rP) {
		RP = rP;
	}
	public List<Paper> getCPs() {
		return CPs;
	}
	public void setCPs(List<Paper> cPs) {
		CPs = cPs;
	}
}
