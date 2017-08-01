package utils;

import java.io.File; 

public class FileUtil {

	
	public static File[] getInputFiles(){
		
		File folder = new File("c:/dev/weatherMark/input/");
		File[] listOfFiles = folder.listFiles();

		//debug
		for (File file : listOfFiles ) {
		    if (file.isFile()) {
 		    	System.out.println("current input file: " + file.getName());
		    }
		}
		return listOfFiles;
	}
	
}
