package utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class FileUtil {

	static Logger slf4jLogger = LoggerFactory.getLogger("FileUtil");
	
	public static File[] getInputFiles(){
		
		slf4jLogger.info("looking here for an input file:  c:/dev/weatherMark/input/");
		File folder = new File("c:/dev/weatherMark/input/");
		File[] listOfFiles = folder.listFiles();

		//debug
		for (File file : listOfFiles ) {
		    if (file.isFile()) {
		    	slf4jLogger.info("current input file: " + file.getName());
 		    }
		}
		return listOfFiles;
	}
	
}
