package service;

import java.util.List;

import entity.BaseFile;
import entity.FileSearchVo;
import utils.page.Pagination;

public interface BaseFileMapper {
    int deleteByPrimaryKey(String fileId);

    int insert(BaseFile record);

    int insertSelective(BaseFile record);

    BaseFile selectByPrimaryKey(String fileId);

    int updateByPrimaryKeySelective(BaseFile record);

    int updateByPrimaryKey(BaseFile record);
    
    List<BaseFile> searchFileByFileVo(Pagination<?> page, FileSearchVo file);
    
    Integer getSearchCountByFileVo(FileSearchVo file);
    
    String getRecordCountByCondition(String areaCode, String fileClass, String fileType);
}