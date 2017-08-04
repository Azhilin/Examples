package gms.files;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class ReadingJSONobjectFromFile {

	private static JSONArray verifyExportFile(String fileName) {
		JSONParser parser = new JSONParser();

		
		// JSONArray companyList = (JSONArray) jsonObject.get("Company List");
	
		// System.out.println("\nCompany List:");
		// Iterator<String> iterator = companyList.iterator();
		// while (iterator.hasNext()) {
		// System.out.println(iterator.next());
		// }
	
		
		JSONArray jsonArray=null;
		try {
			Object obj = parser.parse(new FileReader(fileName));
			jsonArray = (JSONArray) obj;

		} catch (Exception e) {
			e.printStackTrace();
			return jsonArray;
		}
		return jsonArray;

		}

	
	/**
	 * 
	 * @param jsonObjectNumber 0,1,2
	 * @param fileName
	 * @param element "service_type" etc
	 */
	public static String verifyValueInExportFile(int jsonObjectNumber, String fileName, String element){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		String elementValue = (String) jsonObject.get(element);
		System.out.println(elementValue);
		return elementValue;
	}
	
	public static String getJsonObjectAsString(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		
		System.out.println(jsonObject.toString());
		return jsonObject.toString();
	}
	
	public static JSONArray getAllJson(String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		
		return jsonArray;
	}
	
	public static String getValueInExportFileStarted(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONObject started = (JSONObject) jsonObject.get("started");
		System.out.println(started.toString());
		
		return started.toString();
		
	}
	
	public static String getValueInExportFileCompleted(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONObject started = (JSONObject) jsonObject.get("completed");
		System.out.println(started.toString());
		
		return started.toString();
		
	}
	
	public static String getValueInExportFileActiveStates(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONArray activeStates = (JSONArray)jsonObject.get("active_states");	
		
		return activeStates.toString();
		
	}
	public static String getValueInExportFileCompletedStates(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONArray completedStates = (JSONArray)jsonObject.get("completed_states");	
		
		return completedStates.toString();
		
	}
	
	public static String getValueInExportFileActiveTasks(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONArray activeTasks = (JSONArray)jsonObject.get("active_tasks");	
		
		return activeTasks.toString();
		
	}
	public static String getValueInExportFileCompletedTasks(int jsonObjectNumber, String fileName){
		JSONArray jsonArray= verifyExportFile(fileName);
		JSONObject jsonObject = (JSONObject) jsonArray.get(jsonObjectNumber);
		JSONArray completedTasks = (JSONArray)jsonObject.get("completed_tasks");	
		
		return completedTasks.toString();
		
	}
	
	
	public static void verifyAllValuesInExportFile( String fileName, String element){
		JSONArray jsonArray= verifyExportFile(fileName);
		for (int i = 0; i < jsonArray.size(); i++) {
			 JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			 String elementValue = (String) jsonObject.get(element);
			 System.out.println(elementValue);
			
		}
	
	}

}
