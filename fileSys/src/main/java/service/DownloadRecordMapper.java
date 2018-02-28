package service;

import entity.DownloadRecord;


public interface DownloadRecordMapper {
    int insert(DownloadRecord record);

    int insertSelective(DownloadRecord record);
    
    String getRecordCountByAreaCode(String areaCode, String fileClass, String fileType);
}