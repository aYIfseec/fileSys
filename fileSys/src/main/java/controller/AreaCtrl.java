package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import service.AreaDictMapper;

import com.alibaba.fastjson.JSON;

import entity.AreaDict;

@Controller
@RequestMapping(value = "/area")
public class AreaCtrl {
	@Autowired
	private AreaDictMapper areaDictService;
	
	@RequestMapping(value = "cityTree")
	ModelAndView cityTree(){
		ModelAndView model = new ModelAndView("cityTree");
		List<AreaDict> areaList = areaDictService.selectAllArea();
		String jsonStr = JSON.toJSONString(areaList);
		model.addObject("treeJson", jsonStr);
		return model;
	}
	
	@RequestMapping(value = "addAreaLayer")
	ModelAndView addAreaLayer(String parentName){
		ModelAndView model = new ModelAndView("addAreaLayer");
		model.addObject("parentName", parentName);
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "addArea")
	String addArea(AreaDict newArea){
		int res = 0;
		try {
			res = areaDictService.insert(newArea);
		} catch (Exception e){
			e.printStackTrace();
			return "false";
		}
		return res > 0 ? "true" : "false";
	}
	
	@ResponseBody
	@RequestMapping("/updateArea")
	public String updateAreaName(AreaDict area) {
		int res = 0;
		try {
			res = areaDictService.updateByPrimaryKey(area);
		} catch(Exception e) {
			e.printStackTrace();
			return "false";
		}
		
		return res > 0 ? "true" : "false";
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public String delete(String areaCode) {
		int res = 0;
		try {
		    res = areaDictService.deleteByPrimaryKey(areaCode);
		} catch(Exception e) {
			e.printStackTrace();
			return "false";
		}
		
		return res > 0 ? "true" : "false";
	}
	
	@ResponseBody
	@RequestMapping("/getAreaByParentCode")
	public List<AreaDict> getAreaByParentCode(String parentCode) {
		List<AreaDict> areaList = new ArrayList<AreaDict>();
		areaList = areaDictService.selectAreaDictByParentCode(parentCode);
		return areaList;
	}
}

