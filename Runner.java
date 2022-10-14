import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String logFileName = "log.txt";
		
		Scanner scnr = new Scanner(System.in);
		SimulationSettings simSettings = getSimSettingsViaPrompts();
		ArrayList<Process> processes = getProcessesFromJson();
		// Java gives an error if we don't instantiate this here
		AbstractPcb pcb = new FcfsPcb(processes, logFileName);

		switch (simSettings.schedulingAlgo) {
			case "fcfs":
				pcb = new FcfsPcb(processes, logFileName);
				break;
			// case "ps":
			// 	pcb = new PsPcb(processes, logFileName);
			// 	break;
			// case "sjf":
			// 	pcb = new SjfPcb(processes, logFileName);
			// 	break;
			// case "rr":
			// 	pcb = new RrPcb(processes, logFileName);
			// 	break;
		}

		System.out.println("Starting");
		System.out.println();

		while (!pcb.allQueuesEmpty()) {
			if (!simSettings.autoStep) {
				System.out.println("Press enter to step simulation... ");
				scnr.nextLine();
			}
			System.out.println("Stepping...");

			pcb.stepSimulation(simSettings);
		}

		System.out.println("Done");
	}


	public static SimulationSettings getSimSettingsViaPrompts() {
		System.out.println();
		SimulationSettings simSettings = new SimulationSettings();
		Scanner scnr = new Scanner(System.in);
		String badInputMsg = "Invalid input. Please try again.";

		simSettings.autoStep = promptStrEnumInput(scnr, badInputMsg,
			"Step simulation automatically? (y/n): ",
			Arrays.asList("y", "n")
		).equals("y");
		simSettings.quantumTime = promptPosIntInput(scnr,  badInputMsg,
			"Enter quantum time (ms): "
		);
		simSettings.simUnitTime    = promptPosIntInput(scnr,  badInputMsg,
			"Enter time interval between simulation steps (seconds): "
		);
		simSettings.schedulingAlgo = promptStrEnumInput(scnr, badInputMsg,
			"Enter the scheduling algorithm to use (options are: \"fcfs\", \"rr\", \"ps\", and \"sjf\"): ",
			Arrays.asList("fcfs", "rr", "ps", "sjf")
		);

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
			try {
				JSONArray o = new JSONArray(str);
			}
			catch (Exception ex) {
				return false;
			}
		}

		return true;
	}


	public static boolean isValidJsonObjToConstructFrom(String str) {
		return true;
	}

	// currently only loads in ONE activity struct (should would with several)
	public static ArrayList<Process> getProcessesFromJson() throws FileNotFoundException {
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

		ArrayList<Process> toReturn = new ArrayList<Process>();
		JSONArray arr = new JSONArray(jsonStr);

		for (Object obj : arr) {
			toReturn.add(new Process(obj.toString()));
		}

		return toReturn;
	}
}
