import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Runner {
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println();

		ProcessFuncs.getActivityStructFromJson();
		
		GlobalState globState = new GlobalState();

		Scanner scnr = new Scanner(System.in);

		String badInputMsg = "Invalid input. Please try again.";

		String quantumPrompt = "Enter quantum time (ms): ";
		String simUnitTimePrompt = "Enter time interval between simulation steps (seconds): ";
		String algoPrompt = "Enter the scheduling algorithm to use (options are:" + 
												"\"fcfs\", \"rr\", \"ps\", and \"sjf\"): ";

		List<String> algoOptions = Arrays.asList("fcfs", "rr", "ps", "sjf");

		globState.quantumTime    = promptPosIntInput(scnr,  badInputMsg, quantumPrompt);
		globState.simUnitTime    = promptPosIntInput(scnr,  badInputMsg, simUnitTimePrompt);
		globState.schedulingAlgo = promptStrEnumInput(scnr, badInputMsg, algoPrompt, algoOptions);
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
}
