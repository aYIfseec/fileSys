package service;

import java.util.List;

import entity.BaseUser;
import entity.UserSearchVo;
import utils.page.Pagination;

public interface BaseUserMapper {
    int deleteByPrimaryKey(String userId);

    int insert(BaseUser record);

    int insertSelective(BaseUser record);

    BaseUser selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(BaseUser record);

    int updateByPrimaryKey(BaseUser record);
    
    List<BaseUser> searchUserByUserVo(Pagination<?> page, UserSearchVo user);
    
    Integer getSearchCountByUserVo(UserSearchVo user);
}