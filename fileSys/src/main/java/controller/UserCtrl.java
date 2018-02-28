package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import service.AreaDictMapper;
import service.BaseUserMapper;
import utils.page.Pagination;
import entity.AreaDict;
import entity.BaseFile;
import entity.BaseUser;
import entity.FileSearchVo;
import entity.UserSearchVo;

@Controller
@RequestMapping(value = "/user")
public class UserCtrl {
	@Autowired
	private BaseUserMapper baseUserService;
	
	@Autowired
	private AreaDictMapper areaDictService;
	
	@RequestMapping(value = "addUserLayer")
	ModelAndView addUserLayer(){
		AreaDict area = areaDictService.selectByPrimaryKey("0000");
		
		List<AreaDict> cityAreaList = new ArrayList<AreaDict>();
		cityAreaList = areaDictService.selectAreaDictByParentCode("0000");
		
		ModelAndView model = new ModelAndView("addUserLayer");
		model.addObject("area", area);
		model.addObject("cityAreaList", cityAreaList);
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "addUser")
	String addUser(BaseUser user){
		
		String[] areas = user.getArea().split(",");
		if (areas.length > 1) {
			user.setArea(areas[1]);
		} else {
			user.setArea(areas[0]);
		}
		
		user.setUserId(UUID.randomUUID().toString());
		
		if ("0000".equals(user.getArea())) {
			user.setCheckPrivilege((short)1);
		}
		
		int res = 0;
		try {
			res = baseUserService.insertSelective(user);
		} catch(Exception e) {
			e.printStackTrace();
			return "false";
		}
		return res > 0 ? "true" : "false";
	}
	
	@RequestMapping(value = "/modifyOtherPwdLayer")
	ModelAndView modifyOtherPwd(BaseUser user){
		ModelAndView mv = new ModelAndView("modifyOtherPwdLayer");
		mv.addObject("userId", user.getUserId());
		return mv;
	}
	
	@ResponseBody
	@RequestMapping("/updateUser")
	public String updateUser(BaseUser user, String newPwd, HttpSession session) {
		
		if (user.getUserId() == null) {
			String userName = (String) session.getAttribute("loginName");
			UserSearchVo sUser = new UserSearchVo();
			user.setLoginName(userName);
			List<BaseUser> userList = baseUserService.searchUserByUserVo(null, sUser);
			if(userList != null && userList.size() > 0){
				if (user.getLoginPwd() == null || !userList.get(0).getLoginPwd().equals(user.getLoginPwd())) {
					return "error";
				}
				user.setUserId(userList.get(0).getUserId());
			}
		}
		user.setLoginPwd(newPwd);
		
		int res = 0;
		try {
			res = baseUserService.updateByPrimaryKeySelective(user);
		} catch(Exception e) {
			e.printStackTrace();
			return "false";
		}
		
		return res > 0 ? "true" : "false";
	}
	
	@ResponseBody
	@RequestMapping(value = "deleteUser")
	String deleteUser(UserSearchVo searchVo){
		int res = 0;
		try{
			res = baseUserService.deleteByPrimaryKey(searchVo.getUserId());
		}catch(Exception e){
			return "false";
		}
		
		return res > 0 ? "true" : "false";
	}
}
