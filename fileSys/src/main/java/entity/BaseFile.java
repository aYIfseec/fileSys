package entity;

import java.util.Date;

public class BaseFile {
    private String fileId;

    private String fileOwner;

    private String fileName;

    private Date uploadTime;

    private String path;

    private Short fileClass;

    private String fileType;

    private Short state;

    
    
    @Override
	public String toString() {
		return "BaseFile [fileId=" + fileId + ", fileOwner=" + fileOwner
				+ ", fileName=" + fileName + ", uploadTime=" + uploadTime
				+ ", path=" + path + ", fileClass=" + fileClass + ", fileType="
				+ fileType + ", state=" + state + "]";
	}

	public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId == null ? null : fileId.trim();
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public void setFileOwner(String fileOwner) {
        this.fileOwner = fileOwner == null ? null : fileOwner.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Short getFileClass() {
        return fileClass;
    }

    public void setFileClass(Short fileClass) {
        this.fileClass = fileClass;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }
}