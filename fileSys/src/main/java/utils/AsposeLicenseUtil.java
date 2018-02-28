package utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class AsposeLicenseUtil {
	private static InputStream inputStream = null;
	
	private static Logger logger = Logger.getLogger(AsposeLicenseUtil.class);
	 
	/**
	 * 获取License的输入流
	 */
	private static InputStream getLicenseInput() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("license.xml"); 
		System.out.println("license.xml url " + url);
		
		try {
            String path = url.toURI().getPath();
            inputStream = new FileInputStream(path);
        } catch (Exception e) {
            logger.error("license not found!", e);
        }
        return inputStream;
	}
	
	/**
     * 设置License
     *
     * @return true表示已成功设置License, false表示失败
     */
    public static boolean setWordsLicense() {
        InputStream licenseInput = getLicenseInput();
        if (licenseInput != null) {
            try {
                com.aspose.words.License aposeLic = new com.aspose.words.License();
                aposeLic.setLicense(licenseInput);
                return aposeLic.getIsLicensed();
            } catch (Exception e) {
                logger.error("set words license error!", e);
            }
        }
        return false;
    }

    /**
     * 设置License
     *
     * @return true表示已成功设置License, false表示失败
     */
    public static boolean setCellsLicense() {
        InputStream licenseInput = getLicenseInput();
        if (licenseInput != null) {
            try {
                com.aspose.cells.License aposeLic = new com.aspose.cells.License();
                aposeLic.setLicense(licenseInput);
                return true;
            } catch (Exception e) {
                logger.error("set cells license error!", e);
            }
        }
        return false;
    }

    /**
     * 设置License
     *
     * @return true表示已成功设置License, false表示失败
     */
    public static boolean setSlidesLicense() {
        InputStream licenseInput = getLicenseInput();
        if (licenseInput != null) {
            try {
                com.aspose.slides.License aposeLic = new com.aspose.slides.License();
                aposeLic.setLicense(licenseInput);
                return aposeLic.isLicensed();
            } catch (Exception e) {
                logger.error("set ppt license error!", e);
            }
        }
        return false;
    }

    /**
     * 设置Aspose PDF的license
     * @return true表示设置成功，false表示设置失败
     */
    public static boolean setPdfLicense() {
        InputStream licenseInput = getLicenseInput();
        if (licenseInput != null) {
            try {
                com.aspose.pdf.License aposeLic = new com.aspose.pdf.License();
                aposeLic.setLicense(licenseInput);
                return true;
            } catch (Exception e) {
                logger.error("set pdf license error!", e);
            }
        }
        return false;
    }
}
