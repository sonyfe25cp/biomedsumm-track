package model;

import java.util.List;

public class Instance {

	public int id;
	public List<PaperInstance> paperInstances;
	public List<SummaryInstance> summaryInstances;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<PaperInstance> getPaperInstances() {
		return paperInstances;
	}
	public void setPaperInstances(List<PaperInstance> paperInstances) {
		this.paperInstances = paperInstances;
	}
	public List<SummaryInstance> getSummaryInstances() {
		return summaryInstances;
	}
	public void setSummaryInstances(List<SummaryInstance> summaryInstances) {
		this.summaryInstances = summaryInstances;
	}
	
}
