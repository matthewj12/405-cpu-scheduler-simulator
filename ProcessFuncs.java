import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;


public class ProcessFuncs {
	public static String invalidJsonMsg = 
	"You have an error in your json syntax. Please modify the file, save it, and then try again.";

	public static String invalidObjMsg =
	"Invalid JSON to construct ActivityToSimulate from. Please modify the file, save it, and then try again.";

	public static void main(String[] args) {
		isValidJsonObjToConstructFrom(args[0]);
	}
	
	// cocatenates text file's contents minus the newlines
	public static String getFileContents(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		Scanner scnr = new Scanner(file);
		String toReturn = "";

		while (scnr.hasNextLine()) {
			toReturn += scnr.nextLine();
		}

		scnr.close();

		return toReturn;
	}

	public static boolean isValidJson(String str) {
		try {
			JSONObject o = new JSONObject(str);
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	public static boolean isValidJsonObjToConstructFrom(String str) {
		return true;
	}

	public static ProcessActivityToSimulateStruct getActivityStructFromJson() throws FileNotFoundException {
		boolean okToParseJson;
		boolean okToConstruct;
		Scanner scnr = new Scanner(System.in);

		String jsonPath;
		String jsonStr;

		do {
			System.out.print("Enter JSON path: ");
			jsonPath = scnr.nextLine();

			jsonStr = getFileContents(jsonPath);

			okToParseJson = isValidJson(jsonStr);
			okToConstruct = isValidJsonObjToConstructFrom(jsonStr);

			if (!okToParseJson) {
				System.out.println(invalidJsonMsg);
			}
			else if (!okToConstruct) {
				System.out.println(invalidObjMsg);
			}
		} while (!okToParseJson || !okToConstruct);


		return new ProcessActivityToSimulateStruct(new JSONObject(jsonStr));

	}
}
