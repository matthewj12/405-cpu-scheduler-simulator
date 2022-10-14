import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Arrays;

public abstract class AbstractPcb {
	String logFileName;
	// pid, process data
	public HashMap<String, Process> processes;

	// queue name, queue that holds pids
	public HashMap<String, LinkedList<String>> queues;

	public AbstractPcb(ArrayList<Process> scenario, String logFileName) {
		this.logFileName = logFileName;
		
		queues = new HashMap<String, LinkedList<String>>();

		queues.put("not ready",  new LinkedList<String>());
		queues.put("ready",      new LinkedList<String>());
		queues.put("running",    new LinkedList<String>());
		queues.put("waiting",    new LinkedList<String>());
		queues.put("terminated", new LinkedList<String>());

		int pidToAssign = 0;

		for (Process p : scenario) {
			queues.get("not ready").add(Integer.toString(pidToAssign++));
		}
	}

	public void moveProcess(String pid, String fromQueue, String destQueue) throws IOException {
		queues.get(fromQueue).remove(pid);
		queues.get(destQueue).add(pid);
		
		FileWriter fw = new FileWriter(logFileName);
		fw.write("Process " + pid + " moved from " + fromQueue + " to " + destQueue + "\n");
		fw.close();
	}

	public void printQueue(String queueToPrint) {
		LinkedList<String> queue = queues.get(queueToPrint);
		
		System.out.print(queueToPrint + " queue: ");

		if (queue.size() > 0) {
 			System.out.print(queue.get(0));
		}

		boolean first = true;
		for (String pid : queues.get(queueToPrint)) {
			if (first) {
				first = false;
			}
			else {
				System.out.print(", " + pid);
			}
		}

		System.out.println();
	}

	public void visualize() {
		for (String toPrint : Arrays.asList("not ready", "ready", "running", "waiting")) {
			printQueue(toPrint);
		}

		System.out.println();
	}

	public boolean allQueuesEmpty() {
		return 
			queues.get("not ready").isEmpty() &&
			queues.get("ready").isEmpty() &&
			queues.get("running").isEmpty() &&
			queues.get("waiting").isEmpty() &&
			queues.get("terminated").isEmpty();
	}

	// sum of quantum times waited (only relevant for RR) must be >= simUnitTime
	public abstract void stepSimulation(SimulationSettings simSettings) throws IOException;
}
