package service;

import java.util.List;

import entity.AreaDict;

public interface AreaDictMapper {
    int deleteByPrimaryKey(String areaCode);

    int insert(AreaDict record);

    int insertSelective(AreaDict record);

    AreaDict selectByPrimaryKey(String areaCode);

    int updateByPrimaryKeySelective(AreaDict record);

    int updateByPrimaryKey(AreaDict record);

	List<AreaDict> selectAllArea();

	List<AreaDict> selectAreaDictByParentCode(String parentCode);
}