package gms.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Class borrowed from GMSHideSelectedDataInLogs by S. Shepotilova
 *
 * @author
 */

public class WorkWithResources {
    Date d = new Date();
    private static String currentTime;

    private String currentTimeNotStat;

    public void setTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        currentTime = format.format(d).toString();
    }

    public static String getCurrentTime() {
        return currentTime;
    }

    public static String setCurrentTime() {
        Date d = new Date();
        // SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        currentTime = format.format(d).toString();
        System.out.println("current time: ____________ " + currentTime);
        return currentTime;
    }

    public void setCurrentTimeNotStat() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        currentTimeNotStat = format.format(d).toString();
    }

    public String getTimeNotStat() {
        return currentTimeNotStat;
    }

    public void addSecondsToCurrentTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(getTimeNotStat());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        date = cal.getTime();
        currentTimeNotStat = df.format(date).toString();
    }


    public String getContentFromFileAsString(File file, String exportRuleAs) {
        //String content = deserializeString(file);
        String content = "";
        switch (exportRuleAs) {
            case "xml":
                try {
                    content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                break;
            case "log":
                try {
                    content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                break;
            case "xls":
                FileInputStream fileIS;
                try {
                    fileIS = new FileInputStream(file);
                    //Get the workbook instance for XLS file
                    HSSFWorkbook workbook = new HSSFWorkbook(fileIS);
                    //Get first sheet from the workbook
                    HSSFSheet sheet = workbook.getSheetAt(0);
                    //Iterate through each rows from first sheet
                    Iterator<Row> rowIterator = sheet.iterator();
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        //For each row, iterate through each columns
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_BOOLEAN:
                                    System.out.print(cell.getBooleanCellValue() + "\t\t");
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    System.out.print(cell.getNumericCellValue() + "\t\t");
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    System.out.print(cell.getStringCellValue() + "\t\t");
                                    content += (cell.getStringCellValue() + "  ");
                                    break;
                            }
                        }
                        System.out.println(" ");
                    }
                    fileIS.close();
                    FileOutputStream out =
                            new FileOutputStream(file);
                    workbook.write(out);
                    workbook.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        content = content.replace((char) 160, (char) 32);
        return content;
    }

    public void checkRuleIDInFile(String exportRuleAs,
                                  String content, String ruleID) {
        boolean ruleIDPresent = false;
        switch (exportRuleAs) {
            case "xml":
                ruleIDPresent = content.indexOf("<Rule_ID>" + ruleID + "</Rule_ID>") > 0;
            case "xls":
                ruleIDPresent = content.indexOf(ruleID) > 0;
        }
        assertEquals(" ruleID is not Present in imported file! ", true, ruleIDPresent);
    }

    public void checkRuleTypeInFile(String exportRuleAs,
                                    String content, String ruleType) {
        boolean ruleTypePresent = false;
        switch (exportRuleAs) {
            case "xml":
                ruleTypePresent = content.indexOf("<" + ruleType + ">") > 0;
                assertEquals("ruleType is not Present in imported file! ", true, ruleTypePresent);
            case "xls":
                ruleTypePresent = content.indexOf(ruleType) > 0;
                assertEquals("ruleType is not Present in imported file! ", true, ruleTypePresent);
        }
    }

    public void checkRulePhaseInFile(String exportRuleAs,
                                     String content, String rulePhase) {
        boolean rulePhasePresent = false;
        switch (exportRuleAs) {
            case "xml":
                rulePhasePresent = content.indexOf("<Rule_Phase>" + rulePhase + "</Rule_Phase>") > 0;
                assertEquals("rulePhase is not Present in imported file!", true, rulePhasePresent);
            case "xls":
                rulePhasePresent = content.indexOf(rulePhase) > 0;
                assertEquals("rulePhase is not Present in imported file!", true, rulePhasePresent);
        }
    }

    public void checkRuleDescriptionInFile(String exportRuleAs,
                                           String content, String ruleDescription) {
        boolean ruleDescriptionPresent = false;
        switch (exportRuleAs) {
            case "xml":
                ruleDescriptionPresent = content.indexOf("<Rule_Description>" + ruleDescription + "</Rule_Description>") > 0;
                assertEquals("ruleDescription is not Present in imported file!", true, ruleDescriptionPresent);
            case "xls":
                ruleDescriptionPresent = content.indexOf(ruleDescription) > 0;
                assertEquals("ruleDescription is not Present in imported file!", true, ruleDescriptionPresent);
        }
    }

    public void checkRuleNameInFile(String exportRuleAs, String content, String ruleName) {
        boolean ruleNamePresent = false;
        switch (exportRuleAs) {
            case "xml":
                ruleNamePresent = content.indexOf("<Rule_Name>" + ruleName + "</Rule_Name>") > 0;
                assertEquals("ruleName is not Present in imported file!", true, ruleNamePresent);
            case "xls":
                ruleNamePresent = content.indexOf(ruleName) > 0;
                assertEquals("ruleName is not Present in imported file!", true, ruleNamePresent);
        }
    }

    public void removeUploadedDir() {
        String pathToResourcesBath = System.getProperty("user.dir").replace("\\", "\\\\") + "\\\\resources\\\\";
        String command = "cmd /c start " + pathToResourcesBath + "removeDir.bat";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void removeDir(String dirAddress) {
//			String toChange = "folderAddressToChange";
        try {
            setDirAddressInBat(dirAddress);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//	        String pathToResourcesBath = System.getProperty("user.dir").replace("\\", "\\\\")+"\\\\resources\\\\";  
        String command = "cmd /c start C:\\deleteFolder.bat";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void setDirAddressInBat(String logsDirAddress) throws FileNotFoundException, UnsupportedEncodingException {
//	    	String toChange = "folderAddressToChange";
        final File file = new File("C:\\deleteFolder.bat");
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String command = "rmdir /s /q " + logsDirAddress;
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(command);
        writer.close();
    }

    /**
     * Load a text file contents as a <code>String<code>.
     * This method does not perform enconding conversions
     *
     * @param file The input file
     * @return The file contents as a <code>String</code>
     * @throws IOException IO Error
     */
    public String deserializeString(File file)
            throws IOException {
        int len;
        char[] chr = new char[4096];
        final StringBuffer buffer = new StringBuffer();
        final FileReader reader = new FileReader(file);
        try {
            while ((len = reader.read(chr)) > 0) {
                buffer.append(chr, 0, len);
            }
        } finally {
            reader.close();
        }
        return buffer.toString();
    }

    public boolean checkDataInFile(String content, String nodeName,
                                   String dataNode) {
        return content.contains("<" + nodeName + ">" + dataNode);
    }

    public static File fileWhichNameStartsWith(String path, String partNameOfFile) {
        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        File GMSLogs = null;
        String filesName = "";
        for (File checkedFile : listOfFiles) {
            System.out.println("____" + checkedFile.getName());
            if (checkedFile.isFile()) {
                filesName = checkedFile.getName();
                System.out.println("file exists: " + filesName);
                if (filesName.contains(partNameOfFile)) {
                    System.out.println("found =================");
                    GMSLogs = new File(path + filesName);
                }
            }
        }
        return GMSLogs;
    }

    public boolean checkFileContains(File file, String extention, String containsThis) {
        String content;
        String content1;
        content = getContentFromFileAsString(file, extention);
//        	positionLastIndexOf(content,currentTime);
        setTime();
        content = StringUtils.substringAfter(content, currentTime);
        System.out.println("currentTime " + currentTime);
        System.out.println("content ------ " + content);
//        	content1=StringUtils.substringBefore(content, currentTime);
//        	System.out.println("content ------ "+content1);
//        	content1=StringUtils.substring(content, positionLastIndexOf(content,currentTime));
//        	System.out.println("content ------ "+content1);
        return content.contains(containsThis);
    }

    public static void startGMS(String addressToGetBat) {
        String[] cmdList = {"cmd.exe", "/C", "start startServer.bat"};
        ProcessBuilder pb = new ProcessBuilder(cmdList);
        File file = new File(addressToGetBat + "\\");
        pb.directory(file);
        try {
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("!!!!!!! exception!! when pb start");
        }
    }

    public int positionLastIndexOf(String contentString, String subString) {
        //lastindexof (string sub), 
        return contentString.lastIndexOf(subString);
    }

    public static File[] getFilesFromDir(String dirPath, final String fileName) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(fileName);
            }
        });
        if (files.length == 0) {
            throw new NullPointerException(String.format("File \"%s\" doesn't exist in directory \"%s\"", fileName, dirPath));
        }
        return files;
    }

    public static File getLastModifiedFileByName(String dirPath, String fileName) {
        File[] files = getFilesFromDir(dirPath, fileName);
        //sort files to get newest one
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    public static String getFileAsString(String dirPath, String fileName) {
        String content = "";
        File file = getLastModifiedFileByName(dirPath, fileName);
        //read file content to string
        try {
            content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("File \"%s\" from directory \"%s\" to string:%n\"%s\"%n", fileName, dirPath, content);
        return content;
    }

    public static int countSubStringsInFile(String dirPath, String fileName, String subString) {
        String string = getFileAsString(dirPath, fileName);
        Pattern p = Pattern.compile(subString);
        Matcher m = p.matcher(string);
        int counter = 0;
        while (m.find()) {
            counter++;
        }
        System.out.printf("File \"%s\" from directory \"%s\" contains %s substrings of \"%s\".%n", fileName, dirPath, counter, subString);
        return counter;
    }

    public static void deleteFileInDir(String dirPath, String fileName) {
        File file = getLastModifiedFileByName(dirPath, fileName);
        if (file.delete()) {
            System.out.printf("File \"%s\" in directory \"%s\" is deleted!%n", fileName, dirPath);
        } else {
            System.out.println("Delete operation is failed.");
        }
    }

    public static List<String> getAllMatchesInString(String string, String regex) {
        List<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        while (m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }

}
