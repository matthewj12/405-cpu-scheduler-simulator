import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Runner {
	public static String invalidPathMsg =
		"Invalid path.";

	public static String invalidJsonMsg = 
		"You have an error in your json syntax. Please modify the file, save it, and then try again.";

	public static String invalidObjMsg =
		"Invalid JSON to construct Activity from. Please modify the file, save it, and then try again.";

	
	public static void main(String[] args) throws FileNotFoundException {
		SimulationSettings simSettings = getSimSettingsViaPrompts();

		ProcessActivity processActivity = getProcessActivityFromJson();
		ArrayList<ProcessActivity> n = new ArrayList<ProcessActivity>();
		n.add(processActivity);
		
		PCB pcb = new FCFS_PCB(n);

		System.out.println("Starting");

		while (!pcb.allQueuesEmpty()) {
			System.out.println("Stepping...");
			pcb.stepSimulation(simSettings.quantumTime, simSettings.simUnitTime);
		}

		System.out.println("Done");
	}


	public static SimulationSettings getSimSettingsViaPrompts() {
		System.out.println();
		
		SimulationSettings simSettings = new SimulationSettings();

		Scanner scnr = new Scanner(System.in);

		String badInputMsg = "Invalid input. Please try again.";

		String quantumPrompt = "Enter quantum time (ms): ";
		String simUnitTimePrompt = "Enter time interval between simulation steps (seconds): ";
		String algoPrompt = "Enter the scheduling algorithm to use (options are:" + 
												"\"fcfs\", \"rr\", \"ps\", and \"sjf\"): ";

		List<String> algoOptions = Arrays.asList("fcfs", "rr", "ps", "sjf");

		simSettings.quantumTime    = promptPosIntInput(scnr,  badInputMsg, quantumPrompt);
		simSettings.simUnitTime    = promptPosIntInput(scnr,  badInputMsg, simUnitTimePrompt);
		simSettings.schedulingAlgo = promptStrEnumInput(scnr, badInputMsg, algoPrompt, algoOptions);

		return simSettings;
	}
	

	public static String promptStrEnumInput(Scanner scnr, String badInputMsg, String prompt, List<String> validOptions) {
		String curInput = "";

		System.out.print(prompt);
		curInput = scnr.nextLine();

		while (!validOptions.contains(curInput)) {
			System.out.println(badInputMsg);
			curInput = scnr.nextLine();
		}

		return curInput;
	}


	// positive integer
	public static int promptPosIntInput(Scanner scnr, String badInputMsg, String prompt) {
		String curInput = "";
		int toReturn;

		System.out.print(prompt);
		curInput = scnr.nextLine();

		do {
			toReturn = Integer.parseInt(curInput);

			if (toReturn > 0) {
				toReturn = Integer.parseInt(curInput);
				break;
			}
			else {
				System.out.println(badInputMsg);
				System.out.print(prompt);
				curInput = scnr.nextLine();
			}

		} while (true);

		return toReturn;
	}


	// --------------------------------------------------------------------------------------------


	public static boolean isValidFilePath(String path) throws FileNotFoundException {
		try {
			File f = new File(path);
			Scanner scnr = new Scanner(f);
		}
		catch (Exception e) {
			return false;
		}

		return true;
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

	// currently only loads in ONE activity struct (should would with several)
	public static ProcessActivity getProcessActivityFromJson() throws FileNotFoundException {
		Scanner scnr = new Scanner(System.in);

		String jsonPath;
		String jsonStr;

		while (true) {
			System.out.print("Enter JSON path: ");
			jsonPath = scnr.nextLine();

			if (!isValidFilePath(jsonPath)) {
				System.out.println(invalidPathMsg);
				continue;
			}

			jsonStr = getFileContents(jsonPath);

			if (!isValidJson(jsonStr)) {
				System.out.println(invalidJsonMsg);
				continue;
			}

			if (!isValidJsonObjToConstructFrom(jsonStr)) {
				System.out.println(invalidObjMsg);
				continue;
			}

			break;
		}


		return new ProcessActivity(new JSONObject(jsonStr));

	}
}
