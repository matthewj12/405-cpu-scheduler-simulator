import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Arrays;

public class PCB {
	// process data loaded from JSON file
	public ArrayList<String> unarrivedPids;
	// arrived process pid, process data
	public HashMap<String, Process> processes;
	// queue name, queue that holds pids
	public HashMap<String, LinkedList<String>> queues;

	public String logFileName;
	public int nextPidToAssign;

	public PCB(ArrayList<Process> scenario, String logFileName) {
		this.logFileName = logFileName;
		
		queues = new HashMap<String, LinkedList<String>>();

		queues.put("not ready",  new LinkedList<String>());
		queues.put("ready",      new LinkedList<String>());
		queues.put("running",    new LinkedList<String>());
		queues.put("waiting",    new LinkedList<String>());
		queues.put("terminated", new LinkedList<String>());
		
		processes = new HashMap<String, Process>();

		nextPidToAssign = 0;
		unarrivedPids = new ArrayList<String>();
		for (Process p : scenario) {
			String pid = Integer.toString(nextPidToAssign++);
			unarrivedPids.add(pid);
			processes.put(pid, p);
		}
	}

	void addNewlyArrivedProccessesToNotReady(int curTick) {
		ArrayList<String> newlyArrived = new ArrayList<String>();
		
		for (String pid : unarrivedPids) {
			// We can use == instead of >= because stepSimulation steps by ticks, the smallest unit of "time" in the entire simulation
			if (curTick == processes.get(pid).arrivalTime) {
				newlyArrived.add(pid);
			}
		}

		for (String pid : newlyArrived) {
			unarrivedPids.remove(pid);
			queues.get("not ready").add(pid);
		}
	}

	public void moveProcess(String pid, String curQueue, String destQueue) throws IOException {
		queues.get(curQueue).remove(pid);
		queues.get(destQueue).add(pid);

		String logMsg = "Process " + pid + " moved from " + curQueue + " to " + destQueue;

		FileWriter fw = new FileWriter(logFileName, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(logMsg);
		bw.newLine();
		System.out.println(logMsg + '\n');

		bw.close();
	}

	public void printQueue(String queueToPrint) {
		LinkedList<String> queue = queues.get(queueToPrint);
		
		System.out.print(queueToPrint + " queue: ");
		for (int i = queueToPrint.length(); i < 10; i++) {
			System.out.print(" ");
		}

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

	public void visualize(int curTick) {
		System.out.println("Tick: " + curTick);

		// printProcs();

		for (String toPrint : Arrays.asList("not ready", "ready", "running", "waiting", "terminated")) {
			printQueue(toPrint);
		}

		System.out.println();
	}

	public boolean allProcessesCompleted() {
		for (String pid : processes.keySet()) {
			if (!processes.get(pid).remainingBursts.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void printProcs() {
		for (String pid : processes.keySet()) {
			Process p = processes.get(pid);
			System.out.print(p.name + " | ");

			for (int b : p.remainingBursts) {
				System.out.print(b + " ");
			}
			System.out.println();
		}
	}

	public void decrementBurstByTick(String pid) {
		String curQueue = getProcessQueue(pid);
		
		if (!curQueue.equals("terminated")) {
			processes.get(pid).remainingBursts.set(0, processes.get(pid).remainingBursts.get(0) - 1);
		}
	}

	public String getProcessQueue(String pid) {
		String curQueue = "";

		for (String queueName : queues.keySet()) {
			if (queues.get(queueName).contains(pid)) {
				curQueue = queueName;
				break;
			}
		}

		return curQueue;
	}

	public int sumRemainingBursts(String pid) {
		int toReturn = 0;

		for (int burst : processes.get(pid).remainingBursts) {
			toReturn += burst;
		}

		return toReturn;
	}

	public void swapWithRunningIfShorter(String pid) throws IOException {
		String currentlyRunning = queues.get("running").get(0);
		
		if (sumRemainingBursts(pid) < sumRemainingBursts(currentlyRunning)) {
			moveProcess(currentlyRunning, "running", "ready");
			moveProcess(pid, "ready", "running");
		}
	}

	public void swapWithRunningIfHigherPriority(String pid) throws IOException {
		String currentlyRunning = queues.get("running").get(0);
		
		// Lower priority val = higher priority
		if (processes.get(pid).priority < processes.get(currentlyRunning).priority) {
			moveProcess(currentlyRunning, "running", "ready");
			moveProcess(pid, "ready", "running");
		}
	}

	public void stepSimulation(SimulationSettings simSettings, int globalTick) throws IOException {
		for (int tick = 0; tick < simSettings.ticksPerStep; tick++) {
			addNewlyArrivedProccessesToNotReady(globalTick);
			
			visualize(globalTick);

			ArrayList<String> handledTickForThem = new ArrayList<String>();

			for (String pid : processes.keySet()) {
				if (processes.get(pid).remainingBursts.isEmpty()) {
					continue;
				}
				
				String curQueue = getProcessQueue(pid);

				if (curQueue.equals("not ready")) {
					moveProcess(pid, "not ready", "ready");
					curQueue = "ready";
				}

				if (curQueue.equals("ready")) {
					decideWhoGetsCpu(simSettings, pid);
				}
				
				if (curQueue.equals("running")) {
					if (!handledTickForThem.contains(pid)) {
						decrementBurstByTick(pid);
						handledTickForThem.add(pid);
					}

					if (processes.get(pid).remainingBursts.get(0) == 0) {
						processes.get(pid).remainingBursts.remove(0);
						
						if (processes.get(pid).remainingBursts.isEmpty()) {
							moveProcess(pid, "running", "terminated");
						}
						else {
							moveProcess(pid, "running", "waiting");
							curQueue = "waiting";
						}
					}
				}

				if (curQueue.equals("waiting")) {
					if (!handledTickForThem.contains(pid)) {
						decrementBurstByTick(pid);
						handledTickForThem.add(pid);
					}

					if (processes.get(pid).remainingBursts.get(0) == 0) {
						processes.get(pid).remainingBursts.remove(0);

						if (processes.get(pid).remainingBursts.isEmpty()) {
							moveProcess(pid, "waiting", "terminated");
						}
						else {
							moveProcess(pid, "waiting", "ready");
							curQueue = "ready";
							// We can move multiple queues in a single tick
							decideWhoGetsCpu(simSettings, pid);
						}
					}
				}
			}
		}
	}


	public void decideWhoGetsCpu(SimulationSettings simSettings, String pid) throws IOException {
		if (queues.get("running").isEmpty()) {
			// FCFS
			if (pid == queues.get("ready").get(0)) {
				moveProcess(pid, "ready", "running");
			}
		}
		
		else {
			switch (simSettings.schedulingAlgo) {
				case "sjf":
					swapWithRunningIfShorter(pid);
					break;

				case "ps":
					swapWithRunningIfHigherPriority(pid);
					break;
			}
		}
	}
}
