package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;


public class TransformToPdfUtil {
	
	public static boolean transformToPdf(String path, String fileName, String targetFileName) {
		String ext = fileName.substring(fileName.lastIndexOf("."));
		
		File file = new File(path + fileName);
		File targetFile = new File(path + targetFileName);
		
		boolean isTransform = false;
		if (".pdf".equals(ext)) {//若为pdf文件，直接复制到预览文件夹下
			fileChannelCopy(file, targetFile);
		} else {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				inputStream = new FileInputStream(file);
				outputStream = new FileOutputStream(targetFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return isTransform;
			}
	        
	        if (".doc".equals(ext) || ".docx".equals(ext)) {
	        	isTransform = wordToPdf(inputStream, outputStream);
	        } else if (".xls".equals(ext) || ".xlsx".equals(ext)) {
	        	isTransform = excelToPdf(inputStream, outputStream);
	        } else if (".ppt".equals(ext) || ".pptx".equals(ext)) {
	        	isTransform = pptToPdf(inputStream, outputStream);
	        }
		}
		return isTransform;
	}
	
	public static boolean fileChannelCopy(File source, File target) {
		boolean isCopy = false;
		
        FileInputStream fi = null;
        FileOutputStream fo = null;
        
        FileChannel in = null;
        FileChannel out = null;
        
        try {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            isCopy = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCopy;
    }
	
	public static boolean wordToPdf(InputStream inputStream, OutputStream outputStream) {
		try {
            if (AsposeLicenseUtil.setWordsLicense()) {
                Document doc = new Document(inputStream);
                //insertWatermarkText(doc, "水印水印"); // 添加水印
                PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
                pdfSaveOptions.setSaveFormat(SaveFormat.PDF);
                pdfSaveOptions.getOutlineOptions().setHeadingsOutlineLevels(3); // 设置3级doc书签需要保存到pdf的heading中
                pdfSaveOptions.getOutlineOptions().setExpandedOutlineLevels(1); // 设置pdf中默认展开1级
                
                doc.save(outputStream, pdfSaveOptions);
                
                return true;
            } else {
            	return false;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        } finally {
        	try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

	public static boolean pptToPdf(InputStream inputStream, OutputStream outputStream) {
		
		try {
            if (AsposeLicenseUtil.setSlidesLicense()) {
            	Presentation presentation = new Presentation(inputStream);
            	presentation.save(outputStream, com.aspose.slides.SaveFormat.Pdf);
                return true;
            } else {
            	return false;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        } finally {
        	try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public static boolean excelToPdf(InputStream inputStream, OutputStream outputStream) {
		try {
            if (AsposeLicenseUtil.setCellsLicense()) {
            	
                Workbook workbook = new Workbook(inputStream);
                workbook.save(outputStream, com.aspose.cells.SaveFormat.PDF);
                
                return true;
            } else {
            	return false;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        } finally {
        	try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

}
