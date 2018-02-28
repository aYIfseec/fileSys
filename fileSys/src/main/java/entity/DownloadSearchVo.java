package entity;

public class DownloadSearchVo extends DownloadRecord{
	
	String selectCity;
	
	String selectTown;
	
	String fileClass;
	
	String fileClassDetail;
	
	String operateType;

	public String getSelectCity() {
		return selectCity;
	}

	public void setSelectCity(String selectCity) {
		this.selectCity = selectCity;
	}

	public String getSelectTown() {
		return selectTown;
	}

	public void setSelectTown(String selectTown) {
		this.selectTown = selectTown;
	}

	public String getFileClass() {
		return fileClass;
	}

	public void setFileClass(String fileClass) {
		this.fileClass = fileClass;
	}

	public String getFileClassDetail() {
		return fileClassDetail;
	}

	public void setFileClassDetail(String fileClassDetail) {
		this.fileClassDetail = fileClassDetail;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	@Override
	public String toString() {
		return "DownloadSearchVo [selectCity=" + selectCity + ", selectTown="
				+ selectTown + ", fileClass=" + fileClass
				+ ", fileClassDetail=" + fileClassDetail + ", operateType="
				+ operateType + "]";
	}
	
}
