package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import entity.BaseUser;
import entity.UserSearchVo;
import service.BaseUserMapper;

@Controller
public class LoginCtrl {
	@Autowired
	private BaseUserMapper baseUserService;
	
	@RequestMapping(value = "/login")
	ModelAndView login(HttpSession session) {
		session.invalidate();//销毁session
		ModelAndView model = new ModelAndView("login");
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "/doLogin")
	String doLogin(String loginInputName, String loginInputPwd, UserSearchVo userSearchVo,
			HttpSession session){
		userSearchVo.setLoginName(loginInputName);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(null, userSearchVo);
		BaseUser user = null;
		
		if(userList != null && userList.size() > 0){
			user = userList.get(0);
		}
		
		if(user != null && user.getLoginPwd() != null){
			if(user.getLoginPwd().equals(loginInputPwd)){
				session.setAttribute("loginName", user.getLoginName() );
				return "true";
			}
		}
		
		return "false";
	}
	
}
