package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import service.AreaDictMapper;
import service.BaseFileMapper;
import service.BaseUserMapper;
import service.CommonDictMapper;
import service.DownloadRecordMapper;
import utils.BeanUtils;
import utils.DateUtil;
import utils.TransformToPdfUtil;
import utils.UploadUtil;
import utils.page.Pagination;
import entity.AreaDict;
import entity.BaseFile;
import entity.BaseUser;
import entity.CommonDict;
import entity.DownloadRecord;
import entity.DownloadRetVo;
import entity.DownloadSearchVo;
import entity.FileSearchVo;
import entity.UserSearchVo;

@Controller
@RequestMapping(value = "/file")
public class FileCtrl {
	@Autowired
	private BaseFileMapper baseFileService;
	@Autowired
	private BaseUserMapper baseUserService;
	@Autowired
	private CommonDictMapper commonDictService;
	@Autowired
	private AreaDictMapper areaDictService;
	@Autowired
	private DownloadRecordMapper DownloadRecordService;
	
	@ResponseBody
	@RequestMapping(value = "/getFileByCondition")
	Pagination<FileSearchVo> getFileByConditon(Pagination<FileSearchVo> pagination, FileSearchVo searchVo){		
		
		Integer count = baseFileService.getSearchCountByFileVo(searchVo);
		List<BaseFile> fileList = baseFileService.searchFileByFileVo(pagination, searchVo);
		
		//获取file所属人真实姓名  以及时间戳转时间  以及filetype真实名字
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
		return pagination;
	}
	
	@RequestMapping(value = "addFileLayer")
	ModelAndView getAddFileLayer(String type){
		ModelAndView model = new ModelAndView("addFileLayer");
		
		if ("1".equals(type)) {
			List<CommonDict> fileLevel = commonDictService.getDictByDescript("文件级别");
			model.addObject("fileLevelList", fileLevel);
		} else {
			List<CommonDict> workClass = commonDictService.getDictByDescript("文件工作类型");
			model.addObject("workClassList", workClass);
		}
		
		model.addObject("category", Integer.parseInt(type));
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "uploadFile")
	String uploadFile(HttpServletRequest request, HttpSession session, MultipartFile file, BaseFile fileDetail){
		// 物理上传
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String relativepath = "uploadfile/";
		String title = file.getOriginalFilename();
		String storageName = UploadUtil.doUpload(rootpath, relativepath, file);

		if (storageName == null) {
			// 上传失败
			return "false";
		}
		
		//逻辑上传 添加到数据库
		String loginName = (String) session.getAttribute("loginName");
		UserSearchVo vo = new UserSearchVo();
		vo.setLoginName(loginName);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(null, vo);
		
		if(userList != null && userList.size() > 0){
			fileDetail.setFileOwner(userList.get(0).getUserId() );
		}
		
		fileDetail.setFileId(java.util.UUID.randomUUID().toString() ); //GUID
		fileDetail.setFileName(title);
		fileDetail.setPath(storageName);
		fileDetail.setUploadTime(new Date() );
		fileDetail.setState((short)0);
		
		try{
			baseFileService.insert(fileDetail);
		}catch(Exception e){
			e.printStackTrace();
			return "false";
		}
		
		return "true";
	}
	
	//文件预览
	@RequestMapping(value = "previewFile")
	ModelAndView previewFile(BaseFile file){
		
		String fileName = file.getPath();
		
		String ext = fileName.substring(fileName.lastIndexOf("."));
		
		ModelAndView model = null;
		if (".pdf".equals(ext) || ".doc".equals(ext) || ".docx".equals(ext) || 
				".xls".equals(ext) || ".xlsx".equals(ext) || 
				".ppt".equals(ext) || ".pptx".equals(ext)) {
			
			model = new ModelAndView("previewFile");
			model.addObject("file", file);
		} else {
			model = new ModelAndView("warning");
			model.addObject("message", "不支持此文件格式！");
		}
		return model;
	}
	
	@ResponseBody
    @RequestMapping(value = "/getPdf")
    public String displayPDF(HttpSession session, HttpServletResponse response, 
    		HttpServletRequest request, BaseFile previewFile) {
    	String fileName = previewFile.getPath();
    	String path = request.getSession().getServletContext().getRealPath("/");
    	String targetFileName = "pdfFile/" + fileName.substring(fileName.indexOf("/")+1, fileName.lastIndexOf(".")) + ".pdf";
    	
    	File file = new File(path + targetFileName);//PDF文件保存路径
    	if (!file.exists() || file.length() == 0) {//对应的pdf不存在，则转pdf
    		TransformToPdfUtil.transformToPdf(path, fileName, targetFileName);
        }
    	
    	return targetFileName;
    	/*OutputStream outputStream = null;
    	FileInputStream fileInputStream = null;
    	try {
        	fileInputStream = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment;fileName=" + targetFileName);
            response.setContentType("multipart/form-data");
            outputStream = response.getOutputStream();
            IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
        } catch(Exception e) {
            e.printStackTrace();
        }
    	try {
    		if (outputStream != null) {
    			outputStream.flush();
    			outputStream.close();
    		}
    		if (fileInputStream != null) {
    			fileInputStream.close();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    }
	
	
	@ResponseBody
	@RequestMapping(value = "deleteFile")
	String deleteFile(FileSearchVo searchVo, HttpSession session){
		String areaCode = null;
		String userId = null;
		String loginName = (String) session.getAttribute("loginName");
		UserSearchVo vo = new UserSearchVo();
		vo.setLoginName(loginName);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(null, vo);
		if (userList != null && userList.size() > 0){
			areaCode = userList.get(0).getArea();
			userId = userList.get(0).getUserId();
		}
		
		if (areaCode == null || !"0000".equals(areaCode)) {//是否有删除权限
			BaseFile file = baseFileService.selectByPrimaryKey(searchVo.getFileId());
			if (file == null) {
				return "false";
			}
			if (areaCode == null || !file.getFileOwner().equals(userId)) {
				return "no power";
			}
		}
		//只做逻辑删除
		try{
			baseFileService.deleteByPrimaryKey(searchVo.getFileId() );
		}catch(Exception e){
			return "false";
		}
		
		return "true";
	}
	
	@RequestMapping(value = "downloadLayer")
	ModelAndView downloadLayer(String path, String fileName){
		try {
			fileName = URLDecoder.decode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ModelAndView model = new ModelAndView("downloadLayer");
		model.addObject("path", path);
		model.addObject("fileName", fileName);
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "updateFile")
	String updateFile(BaseFile file){
		int res = 0;
		try {
			res = baseFileService.updateByPrimaryKeySelective(file);
		} catch(Exception e) {
			e.printStackTrace();
			return "false";
		}
		
		return res > 0 ? "true" : "false";
	}
	
	@ResponseBody
	@RequestMapping(value = "downloadFile")
	public String  downloadFile(BaseFile file, HttpSession session,
			HttpServletRequest request, HttpServletResponse response){
		String path = request.getSession().getServletContext().getRealPath("/") + file.getPath();
		response.setContentType("application/octet-stream");
		response.reset();
		try{
			File newFile = new File(path);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[bis.available()];
			bis.read(buffer);
			
            //将下载文件重命名为真实文件名
            response.setHeader("Content-disposition", "attachment; filename="
            		+ new String(file.getFileName().getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", "" + newFile.length() );  
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());//构造输出流  
            bos.write(buffer);
            bis.close();
            bos.close();
		}catch(IOException e){
			e.printStackTrace();
			return "false";
		}
		
		DownloadRecord record = new DownloadRecord();
		record.setDownTime(new Date());
		
		FileSearchVo fileSearch = new FileSearchVo();
		fileSearch.setPath(file.getPath());
		List<BaseFile> fileList = baseFileService.searchFileByFileVo(null, fileSearch);
		if (fileList != null && fileList.size() > 0) {
			record.setDonwFileId(fileList.get(0).getFileId());
		}
		
		String loginName = (String) session.getAttribute("loginName");
		UserSearchVo vo = new UserSearchVo();
		vo.setLoginName(loginName);
		List<BaseUser> userList = baseUserService.searchUserByUserVo(null, vo);
		if (userList != null && userList.size() > 0){
			record.setDownUserId(userList.get(0).getUserId() );
		}
		
		DownloadRecordService.insert(record);
		
		return "true";
	}
	
	@ResponseBody
	@RequestMapping(value = "getFileClassDict")
	List<CommonDict> getFileClassDict(String descript){
		List<CommonDict> dictList = commonDictService.getDictByDescript(descript);
		return dictList;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "getDownloadStatisticData")
	List<DownloadRetVo> getDownloadStatisticData(DownloadSearchVo searchVo){
		List<DownloadRetVo> retList = new ArrayList<DownloadRetVo>();
		
		//从searchVo获取区域码
		if(searchVo.getSelectTown() != null && !"".equals(searchVo.getSelectTown()) ){
			//乡镇级区域码不为空 仅查询该乡镇的情况
			DownloadRetVo vo = new DownloadRetVo();
			vo.setAreaRealName( areaDictService.selectByPrimaryKey(searchVo.getSelectTown()).getAreaName() );
			String count = DownloadRecordService.getRecordCountByAreaCode(searchVo.getSelectTown(), searchVo.getFileClass(), searchVo.getFileClassDetail());
			vo.setCount( count );
			retList.add(vo);
		}		
		else if(searchVo.getSelectCity() != null  && !"".equals(searchVo.getSelectCity())){
			//查询该县级下 所有乡镇情况   或者宜春市下所有县级情况
			List<AreaDict> areaList = areaDictService.selectAreaDictByParentCode(searchVo.getSelectCity() );
			for (AreaDict area : areaList) {
				DownloadRetVo vo = new DownloadRetVo();
				vo.setAreaRealName( area.getAreaName() );
				String count = DownloadRecordService.getRecordCountByAreaCode(area.getAreaCode(), searchVo.getFileClass(), searchVo.getFileClassDetail());
				vo.setCount(count);
				
				retList.add(vo);
			}
		}
		
		if(retList != null){
			Collections.sort(retList);
			for(int i = 1; i <= retList.size(); ++i) {
				retList.get(i-1).setSortId(i);  //设置排序号
			}
		}
		
		return retList;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "getUploadStatisticData")
	List<DownloadRetVo> getUploadStatisticData(DownloadSearchVo searchVo){
		List<DownloadRetVo> retList = new ArrayList<DownloadRetVo>();
		
		//从searchVo获取区域码
		if(searchVo.getSelectTown() != null && !"".equals(searchVo.getSelectTown()) ){
			//乡镇级区域码不为空 仅查询该乡镇的情况
			DownloadRetVo vo = new DownloadRetVo();
			String areaCode = searchVo.getSelectTown();
			vo.setAreaRealName( areaDictService.selectByPrimaryKey(searchVo.getSelectTown()).getAreaName() );
			vo.setCount( baseFileService.getRecordCountByCondition(areaCode, searchVo.getFileClass(), searchVo.getFileClassDetail() ) );
			retList.add(vo);
		}		
		else if(searchVo.getSelectCity() != null  ){
			//查询该县级下 所有乡镇情况   或者宜春市下所有县级情况
			List<AreaDict> areaList = areaDictService.selectAreaDictByParentCode(searchVo.getSelectCity() );
			for(AreaDict area : areaList){
				DownloadRetVo vo = new DownloadRetVo();
				String count = baseFileService.getRecordCountByCondition(area.getAreaCode(), searchVo.getFileClass(), searchVo.getFileClassDetail() );
				vo.setAreaRealName( area.getAreaName() );
				vo.setCount( count );
				retList.add(vo);
			}
		}
		
		if(retList != null){
			Collections.sort(retList);
			for(int i = 1; i <= retList.size(); ++i){
				retList.get(i-1).setSortId(i);  //设置排序号
			}
		}
		
		return retList;
	}
}
