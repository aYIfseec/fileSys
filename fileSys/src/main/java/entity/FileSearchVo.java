package entity;

public class FileSearchVo extends BaseFile{
	String searchKey;
	
	String fileOwnerRealName;
	
	String timeFormat;
	
	String fileTypeName;
	
	private String areaName;
	
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getFileTypeName() {
		return fileTypeName;
	}

	public void setFileTypeName(String fileTypeName) {
		this.fileTypeName = fileTypeName;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getFileOwnerRealName() {
		return fileOwnerRealName;
	}

	public void setFileOwnerRealName(String fileOwnerRealName) {
		this.fileOwnerRealName = fileOwnerRealName;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
}
