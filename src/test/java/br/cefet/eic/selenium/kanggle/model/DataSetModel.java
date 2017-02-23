package br.cefet.eic.selenium.kanggle.model;

public class DataSetModel {
	private String title = "";
	private String subtitle = "";
	private String size = "";
	private String sizeUnit = "";
	private String url = "";
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSizeUnit() {
		return sizeUnit;
	}
	public void setSizeUnit(String sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
	//Converte em formato CSV
	@Override
	public String toString() {
		return (this.title + ";" + this.subtitle + ";"
				+ this.size + ";" + this.sizeUnit + ";" + this.url);
	}
}
