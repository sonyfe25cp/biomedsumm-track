package model;

public class Citantion {

	public int id;
	public String topicId;
	public Paper RP;
	public Paper CP;
	public String annotator;//标注人
	public String facet;//方面
	
	public String citationMarkerOffset;
	public String citationMarker;
	
	public String citationOffset;
	public String citationText;//引用
	
	public String referenceOffset;
	public String referenceText;//可能的出处
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public Paper getRP() {
		return RP;
	}
	public void setRP(Paper rP) {
		RP = rP;
	}
	public Paper getCP() {
		return CP;
	}
	public void setCP(Paper cP) {
		CP = cP;
	}
	public String getAnnotator() {
		return annotator;
	}
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}
	public String getFacet() {
		return facet;
	}
	public void setFacet(String facet) {
		this.facet = facet;
	}
	public String getCitationMarkerOffset() {
		return citationMarkerOffset;
	}
	public void setCitationMarkerOffset(String citationMarkerOffset) {
		this.citationMarkerOffset = citationMarkerOffset;
	}
	public String getCitationMarker() {
		return citationMarker;
	}
	public void setCitationMarker(String citationMarker) {
		this.citationMarker = citationMarker;
	}
	public String getCitationOffset() {
		return citationOffset;
	}
	public void setCitationOffset(String citationOffset) {
		this.citationOffset = citationOffset;
	}
	public String getCitationText() {
		return citationText;
	}
	public void setCitationText(String citationText) {
		this.citationText = citationText;
	}
	public String getReferenceOffset() {
		return referenceOffset;
	}
	public void setReferenceOffset(String referenceOffset) {
		this.referenceOffset = referenceOffset;
	}
	public String getReferenceText() {
		return referenceText;
	}
	public void setReferenceText(String referenceText) {
		this.referenceText = referenceText;
	}
	@Override
	public String toString() {
		return "Citantion [id=" + id + ", topicId=" + topicId + ", RP=" + RP
				+ ", CP=" + CP + ", annotator=" + annotator + ", facet="
				+ facet + ", citationMarkerOffset=" + citationMarkerOffset
				+ ", citationMarker=" + citationMarker + ", citationOffset="
				+ citationOffset + ", citationText=" + citationText
				+ ", referenceOffset=" + referenceOffset + ", referenceText="
				+ referenceText + "]";
	}
	
	
}
