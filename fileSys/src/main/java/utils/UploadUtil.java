package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.stream.FileImageOutputStream;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
/**
 * 附件上传
 * 
 */
public class UploadUtil {
	
	public static String doUpload(String realPath ,MultipartFile file){
		return doUpload(realPath,"/uploadfile/projectfile/",file);
	}
	
	public static String doUpload(String realPath ,String modulePath ,MultipartFile file){
		String fileName = file.getOriginalFilename();  
		if(!StringUtils.isEmpty(fileName)){
			String ext = fileName.substring(fileName.indexOf("."));
			fileName = Calendar.getInstance().getTimeInMillis() + ext;
			//fileName = Calendar.getInstance().getTimeInMillis() + ".pdf";
			
			String pathname = realPath + modulePath + fileName;
			File targetFile = new File(pathname);
	        if(!targetFile.exists()){
	            targetFile.mkdirs();
	        }
	        //保存
	        try {
	            file.transferTo(targetFile);
	        } catch (Exception e) {
	            e.printStackTrace();  
	        } 
	        return modulePath + fileName;
		}
		return null;
	}
	
	public static String byteToImg(String realPath ,byte[] bytes){
		if(bytes != null && bytes.length > 0){
			String imagePath = "/uploadfile/projectfile/" + UUID.randomUUID().toString() + ".jpg";
			FileImageOutputStream imageOutput;
			try {
				File file = new File(realPath + imagePath);
				file.createNewFile();
				imageOutput = new FileImageOutputStream(file);
				imageOutput.write(bytes, 0, bytes.length);  
			 	imageOutput.close(); 
				return imagePath;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
		return null;
	}
	
}

