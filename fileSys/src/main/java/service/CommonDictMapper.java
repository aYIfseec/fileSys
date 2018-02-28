package service;

import java.util.List;

import entity.CommonDict;

public interface CommonDictMapper {
    int deleteByPrimaryKey(String code);

    int insert(CommonDict record);

    int insertSelective(CommonDict record);

    CommonDict selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(CommonDict record);

    int updateByPrimaryKey(CommonDict record);
    
    List<CommonDict> getDictByDescript(String descript);
}