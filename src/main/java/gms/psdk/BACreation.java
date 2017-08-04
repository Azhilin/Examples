package gms.psdk;

import org.junit.Test;

public class BACreation {
	Reconfiguration reconfiguration = new Reconfiguration();
	@Test
	public void createBAObjects(){
		reconfiguration.openConnectionToConfig();
		//??? 
//		createServiceTypeBA();
//		createServiceTypeValues();
		
		createStateTypeBA();
		createStateTypeValues();
		
		createTaskTypeBA();
		createTaskTypeValues();
		
		createResourceTypeBA();
		createResourceTypeValues();
		
		createMediaTypeBA();
		createMediaTypeValues();
		
		reconfiguration.closeConnectionToConfig();		
	}
	
	private void createServiceTypeBA(){
		reconfiguration.createEnumerator("ServiceType","Service Type","");
	}
	
	private void createStateTypeBA(){
		reconfiguration.createEnumerator("StateType","State Type","");
	}
	
	private void createTaskTypeBA(){
		reconfiguration.createEnumerator("TaskType","Task Type","");
	}
	
	private void createResourceTypeBA(){
		reconfiguration.createEnumerator("ResourceType","Resource Type","");
	}
	
	private void createMediaTypeBA(){
		reconfiguration.createEnumerator("MediaType","Media Type","");
	}
	
	private void createServiceTypeValues(){
		String enumName="ServiceType";		
		reconfiguration.createEnumeratorValue(enumName, "MyService1", "MyService1", "");
		reconfiguration.createEnumeratorValue(enumName, "MyService2", "MyService2", "");
		reconfiguration.createEnumeratorValue(enumName, "MyService3", "MyService3", "");
		reconfiguration.createEnumeratorValue(enumName, "MyService4", "My Service4", "");
		reconfiguration.createEnumeratorValue(enumName, "Sales", "Sales", "");
		reconfiguration.createEnumeratorValue(enumName, "Support", "Support", "");
		reconfiguration.createEnumeratorValue(enumName, "Service Type Attribute With Blank Chars", "Service Type Attribute With Blank Chars", "");	
		reconfiguration.createEnumeratorValue(enumName, "default", "default", "Default Service Type",true);	
	}
	
	private void createStateTypeValues(){
		String enumName="StateType";
		reconfiguration.createEnumeratorValue(enumName, "MyState1", "MyState1", "");
		reconfiguration.createEnumeratorValue(enumName, "MyState2", "MyState2", "");
		reconfiguration.createEnumeratorValue(enumName, "MyState3", "MyState3", "");
		reconfiguration.createEnumeratorValue(enumName, "MyState4", "My State4", "");	
		reconfiguration.createEnumeratorValue(enumName, "State Type Attribute With Blank Chars", "State Type Attribute With Blank Chars", "");	
		reconfiguration.createEnumeratorValue(enumName, "default", "default", "Default State Type",true);	
	}
	
	private void createTaskTypeValues(){
		String enumName="TaskType";
		reconfiguration.createEnumeratorValue(enumName, "MyTask1", "MyTask1", "");
		reconfiguration.createEnumeratorValue(enumName, "MyTask2", "MyTask2", "");
		reconfiguration.createEnumeratorValue(enumName, "MyTask3", "MyTask3", "");
		reconfiguration.createEnumeratorValue(enumName, "MyTask4", "My Task4", "");	
		reconfiguration.createEnumeratorValue(enumName, "Task Type Attribute With Blank Chars", "Task Type Attribute With Blank Chars", "");	
		reconfiguration.createEnumeratorValue(enumName, "default", "default", "Default Task Type",true);	
	}
	
	private void createResourceTypeValues(){
		String enumName="ResourceType";
		reconfiguration.createEnumeratorValue(enumName, "ResType1", "ResType1", "");
		reconfiguration.createEnumeratorValue(enumName, "ResType2", "ResType2", "");	
		reconfiguration.createEnumeratorValue(enumName, "ResourceType4", "Resource Type4", "");	
		reconfiguration.createEnumeratorValue(enumName, "Resource Type attribute with blank chars", "Resource Type attribute with blank chars", "");	
		reconfiguration.createEnumeratorValue(enumName, "default", "default", "Default Resource Type",true);	
	}
	
	private void createMediaTypeValues(){
		String enumName="MediaType";
		reconfiguration.createEnumeratorValue(enumName, "mymedia", "mymedia", "");
		reconfiguration.createEnumeratorValue(enumName, "mymedia1", "mymedia1", "");
		reconfiguration.createEnumeratorValue(enumName, "mymedia2", "mymedia2", "");
		reconfiguration.createEnumeratorValue(enumName, "mymedia3", "mymedia3", "");
		reconfiguration.createEnumeratorValue(enumName, "mymedia4", "mymedia4", "");
		reconfiguration.createEnumeratorValue(enumName,"voice", "voice","");
		reconfiguration.createEnumeratorValue(enumName, "any", "any", "Default media type",true);			
			
	}
	
	

}
