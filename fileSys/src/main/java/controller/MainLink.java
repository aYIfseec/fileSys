package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import entity.AreaDict;
import entity.BaseFile;
import entity.BaseUser;
import entity.CommonDict;
import entity.FileSearchVo;
import entity.UserSearchVo;
import service.AreaDictMapper;
import service.BaseFileMapper;
import service.BaseUserMapper;
import service.CommonDictMapper;
import utils.BeanUtils;
import utils.DateUtil;
import utils.page.Pagination;


@Controller
public class MainLink {
	@Autowired
	private BaseUserMapper baseUserService;
	@Autowired
	private BaseFileMapper baseFileService;
	@Autowired
	private CommonDictMapper commonDictService;
	@Autowired
	private AreaDictMapper areaDictService;
	
	@RequestMapping(value = "/main")
	ModelAndView main(HttpSession session){
		String loginName = (String) session.getAttribute("loginName");
		ModelAndView model =   new ModelAndView("main");
		model.addObject("loginName", loginName);
		return model;
	}
	
	@RequestMapping(value = "/modifySelfPwdLayer")
	ModelAndView modifySelfPwd(){
		return new ModelAndView("modifySelfPwdLayer");
	}
	
	@RequestMapping(value = "/fileList")
	ModelAndView fileList(Pagination<FileSearchVo> pagination, HttpSession session){
		ModelAndView model = new ModelAndView("fileList");
		
		FileSearchVo searchFileVo = new FileSearchVo();
		searchFileVo.setFileClass((short)1);
		searchFileVo.setState((short)1);
		
		Integer count = baseFileService.getSearchCountByFileVo(searchFileVo);
		List<BaseFile> fileList = baseFileService.searchFileByFileVo(pagination, searchFileVo);
		
		//获取file所属人真实姓名
		List<FileSearchVo> extendFileList = new ArrayList<FileSearchVo>();
		for(BaseFile baseFile : fileList){
			FileSearchVo extendFile = new FileSearchVo();
			BeanUtils.copyProperties(baseFile, extendFile);
			
			UserSearchVo baseUser = new UserSearchVo();
			baseUser.setUserId(baseFile.getFileOwner() );
			List<BaseUser> userList = baseUserService.searchUserByUserVo(null, baseUser);
			if(userList != null && userList.size() > 0){
				BaseUser user = userList.get(0);
				extendFile.setFileOwnerRealName(user.getRealName() );
				//获得所属地区
				AreaDict area = areaDictService.selectByPrimaryKey(user.getArea());
				AreaDict parentArea = areaDictService.selectByPrimaryKey(area.getParentCode());
				
				String areaName = "";
				if (parentArea != null) {
					areaName += parentArea.getAreaName() + " -- ";
				}
				areaName += area.getAreaName();
				extendFile.setAreaName(areaName);
			}
			
			//时间戳转换成时间字符串
			if(baseFile.getUploadTime() != null){
				//DateUtil dateUtil = new DateUtil(DateUtil.COMMON_FULL);
				String timeStr = DateUtil.getDateText(baseFile.getUploadTime(), "yyyy-MM-dd HH:mm:ss");
				extendFile.setTimeFormat(timeStr);
			}
			
			//获取filetype真实名字
			if(baseFile.getFileType() != null){
				CommonDict dict = commonDictService.selectByPrimaryKey(baseFile.getFileType() );
				extendFile.setFileTypeName(dict.getDetail() );
			}
			
			
			extendFileList.add(extendFile);
		}
		
		pagination.setTotalItemsCount(count);
		pagination.setItems(extendFileList);
		model.addObject("page", pagination);
		
		//获取文件审核权限
		String userName = (String) session.getAttribute("loginName");
		UserSearchVo searchVo = new UserSearchVo();
		searchVo.setLoginName(userName);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(null, searchVo);
		BaseUser user = null;
		
		if(userList != null && userList.size() > 0){
			user = userList.get(0);
		}
		
		if(user != null && user.getCheckPrivilege() != null){
			model.addObject("check", user.getCheckPrivilege());
		}
		
		//获取文件归档以及文件分类
		List<CommonDict> fileLevel = commonDictService.getDictByDescript("文件级别");
		model.addObject("fileLevelList", fileLevel);
		
		List<CommonDict> workClass = commonDictService.getDictByDescript("文件工作类型");
		model.addObject("workClassList", workClass);
		
		
		AreaDict area = areaDictService.selectByPrimaryKey("0000");
		List<AreaDict> cityAreaList = new ArrayList<AreaDict>();
		cityAreaList = areaDictService.selectAreaDictByParentCode("0000");
		model.addObject("area", area);
		model.addObject("cityAreaList", cityAreaList);
		
		return model;
	}
	
	@RequestMapping(value = "/userList")
	ModelAndView userList(Pagination<UserSearchVo> pagination, UserSearchVo searchVo){
		ModelAndView model = new ModelAndView("userList");
		
		Integer count = baseUserService.getSearchCountByUserVo(searchVo);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(pagination, searchVo);
		
		List<UserSearchVo> extendUserList = new ArrayList<UserSearchVo>();
		for (BaseUser baseUser : userList) {
			UserSearchVo extendUser = new UserSearchVo();
			BeanUtils.copyProperties(baseUser, extendUser);
			
			//获得用户所属地区
			AreaDict area = areaDictService.selectByPrimaryKey(extendUser.getArea());
			AreaDict parentArea = areaDictService.selectByPrimaryKey(area.getParentCode());
			
			String areaName = "";
			if (parentArea != null) {
				areaName += parentArea.getAreaName() + " -- ";
			}
			areaName += area.getAreaName();
			
			extendUser.setAreaName(areaName);
			extendUserList.add(extendUser);
		}
		
		pagination.setTotalItemsCount(count);
		pagination.setItems(extendUserList);
		
		model.addObject("page", pagination);
		model.addObject("searchKey", searchVo.getSearchKey());
		return model;
	}
	
	@RequestMapping(value = "/areaEdit")
	ModelAndView areaEdit(){
		ModelAndView model = new ModelAndView("areaEdit");
		return model;
	}
	
	@RequestMapping(value = "/statistic")
	ModelAndView statistic(){
		AreaDict area = areaDictService.selectByPrimaryKey("0000");
		
		List<AreaDict> cityAreaList = new ArrayList<AreaDict>();
		cityAreaList = areaDictService.selectAreaDictByParentCode("0000");
		ModelAndView model = new ModelAndView("statistic");
		model.addObject("area", area);
		model.addObject("cityAreaList", cityAreaList);
		return model;
	}
	
	@RequestMapping(value = "/uploadedFile")
	ModelAndView uploadedFile(Pagination<BaseFile> pagination, HttpSession session){
		
		ModelAndView model = new ModelAndView("uploadedFile");
		Integer count = 0;
		List<BaseFile> fileList = null;
		
		String userName = (String) session.getAttribute("loginName");
		if (userName != null) {
			
			UserSearchVo user = new UserSearchVo();
			FileSearchVo fileSearch = new FileSearchVo();
			
			user.setLoginName(userName);
			List<BaseUser> userList = baseUserService.searchUserByUserVo(null,
					user);
			
			if (userList != null && userList.size() > 0) {
				fileSearch.setFileOwner(userList.get(0).getUserId());
			}
			
			count = baseFileService.getSearchCountByFileVo(fileSearch);
			fileList = baseFileService.searchFileByFileVo(pagination,
					fileSearch);
			
		}
		pagination.setTotalItemsCount(count);
		pagination.setItems(fileList);
		
		model.addObject("page", pagination);
		return model;
	}
}
