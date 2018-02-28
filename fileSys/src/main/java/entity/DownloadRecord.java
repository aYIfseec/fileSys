package entity;

import java.util.Date;

public class DownloadRecord {
    private String donwFileId;

    private String downUserId;

    private Date downTime;

    public String getDonwFileId() {
        return donwFileId;
    }

    public void setDonwFileId(String donwFileId) {
        this.donwFileId = donwFileId == null ? null : donwFileId.trim();
    }

    public String getDownUserId() {
        return downUserId;
    }

    public void setDownUserId(String downUserId) {
        this.downUserId = downUserId == null ? null : downUserId.trim();
    }

    public Date getDownTime() {
        return downTime;
    }

    public void setDownTime(Date downTime) {
        this.downTime = downTime;
    }
}