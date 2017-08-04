package gms.files;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.util.zip.GZIPInputStream;


public class WorkWithCompressedFiles {
	
	/**
	 * decompress .zip files
	 * @param source e.g. c:\\export_12345.json.zip
	 * @param destination e.g c:\\export_12345.json
	 */
	public static void decompressZipFile(String source, String destination){
//	 String source = "some/compressed/file.zip";
//	    String destination = "some/destination/folder";
	    String password = "password";

	    try {
	         ZipFile zipFile = new ZipFile(source);
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password);
	         }
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
	    
	}
	/**
	 * decompress .zip files
	 * @param source
	 * @param destination
	 * @param password
	 */
	public static void decompressZipFile(String source, String destination, String password){//		

		    try {
		         ZipFile zipFile = new ZipFile(source);
		         if (zipFile.isEncrypted()) {
		            zipFile.setPassword(password);
		         }
		         zipFile.extractAll(destination);
		    } catch (ZipException e) {
		        e.printStackTrace();
		    }
		    
		}
	
	/**
	 * decompress .gzip files
	 * @param source
	 * @param destination
	 */
	public static void decompressGzipFile(String source, String destination){
		FileInputStream fis2;
		GZIPInputStream gis;
		FileOutputStream fos2;
		try {
			fis2 = new FileInputStream(source);
			gis = new GZIPInputStream(fis2);
			fos2 = new FileOutputStream(destination);
			doCopy(gis, fos2); // copy and uncompress
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		

		
	}
	public static void doCopy(InputStream is, OutputStream os) throws Exception {
		int oneByte;
		while ((oneByte = is.read()) != -1) {
			os.write(oneByte);
		}
		os.close();
		is.close();
	}
	

}
