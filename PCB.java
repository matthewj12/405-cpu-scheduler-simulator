import java.io.File;
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
	public int globalTick;

	public HashMap<String, Integer> cpuWaitTimes;
	public HashMap<String, Integer> ioWaitTimes;
	public HashMap<String, Integer> responseTimes;
	public HashMap<String, Integer> turnaroundTimes;
	public int totalTicks;
	public int cpuUtilTicks;

	public boolean silent;

	public PCB(ArrayList<Process> scenario, String logFileName, boolean silent) {
		this.logFileName = logFileName;
		this.silent = silent;
		// Don't count the first tick (tick 0) because no time has elapsed
		totalTicks = -1;
		cpuUtilTicks = 0;
		
		queues = new HashMap<String, LinkedList<String>>();

		queues.put("notReady",   new LinkedList<String>());
		queues.put("ready",      new LinkedList<String>());
		queues.put("running",    new LinkedList<String>());
		queues.put("waiting",    new LinkedList<String>());
		queues.put("terminated", new LinkedList<String>());
		
		processes = new HashMap<String, Process>();
		cpuWaitTimes = new HashMap<String, Integer>();
		ioWaitTimes = new HashMap<String, Integer>();
		responseTimes = new HashMap<String, Integer>();
		turnaroundTimes = new HashMap<String, Integer>();

		nextPidToAssign = 0;
		unarrivedPids = new ArrayList<String>();
		for (Process p : scenario) {
			String pid = Integer.toString(nextPidToAssign++);
			unarrivedPids.add(pid);
			processes.put(pid, p);

			cpuWaitTimes.put(pid, 0);
			ioWaitTimes.put(pid, 0);
			responseTimes.put(pid, 0);
			turnaroundTimes.put(pid, 0);
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
			queues.get("notReady").add(pid);
		}
	}

	public void moveProcess(String pid, String curQueue, String destQueue, boolean silent) throws IOException {
		// if (destQueue.equals("running")) {

		// }
		
		queues.get(curQueue).remove(pid);
		queues.get(destQueue).add(pid);

		String logMsg = "Process " + pid + " moved from " + curQueue + " to " + destQueue + " during tick " + globalTick;	

		FileWriter fw = new FileWriter(logFileName, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(logMsg);
		bw.newLine();
		bw.close();

		if (!silent) {
			System.out.println(logMsg);
		}
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
		System.out.println();
		
		System.out.println("Tick: " + curTick);
		System.out.println();

		if (queues.get("running").isEmpty()){
			System.out.println("|--------------|");
			System.out.println("| CPU: Idle... |");
			System.out.println("|--------------|");
		}
		else {
			System.out.println("|--------------------|");
			System.out.println("| CPU: Running pid " + queues.get("running").get(0) + " |");
			System.out.println("|--------------------|");
		}

		System.out.println();

		if (queues.get("waiting").isEmpty()){
			System.out.println("|-------------|");
			System.out.println("| IO: Idle... |");
			System.out.println("|-------------|");
		}
		else {
			System.out.println("|-------------------|");
			System.out.println("| IO: Running pid " + queues.get("waiting").get(0) + " |");
			System.out.println("|-------------------|");
		}

		System.out.println();

		for (String toPrint : Arrays.asList("notReady", "ready", "running", "waiting", "terminated")) {
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
			moveProcess(currentlyRunning, "running", "ready", silent);
			moveProcess(pid, "ready", "running", silent);
		}
	}

	public void swapWithRunningIfHigherPriority(String pid) throws IOException {
		String currentlyRunning = queues.get("running").get(0);
		
		// Lower priority val = higher priority
		if (processes.get(pid).priority < processes.get(currentlyRunning).priority) {
			moveProcess(currentlyRunning, "running", "ready", silent);
			moveProcess(pid, "ready", "running", silent);
		}
	}

	public void stepSimulation(SimulationSettings simSettings, int globalTick, boolean silent) throws IOException {
		this.globalTick = globalTick;
		
		for (int tick = 0; tick < simSettings.ticksPerStep; tick++) {
			addNewlyArrivedProccessesToNotReady(globalTick);
			
			if (!silent) {
				visualize(globalTick);
			}

			ArrayList<String> handledTickForThem = new ArrayList<String>();

			for (String pid : processes.keySet()) {
				if (processes.get(pid).remainingBursts.isEmpty()) {
					continue;
				}
				
				String curQueue = getProcessQueue(pid);

				if (curQueue.equals("notReady")) {
					moveProcess(pid, "notReady", "ready", silent);
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
							moveProcess(pid, "running", "terminated", silent);
						}
						else {
							moveProcess(pid, "running", "waiting", silent);
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
							moveProcess(pid, "waiting", "terminated", silent);
						}
						else {
							moveProcess(pid, "waiting", "ready", silent);
							curQueue = "ready";
							// We can move multiple queues in a single tick
							decideWhoGetsCpu(simSettings, pid);
						}
					}
				}
			}

			for (String pid : queues.get("ready")) {
				cpuWaitTimes.put(pid, cpuWaitTimes.get(pid) + 1);
			}

			for (String pid : queues.get("waiting")) {
				ioWaitTimes.put(pid, ioWaitTimes.get(pid) + 1);
			}

			for (String pid : queues.get("running")) {
				if (responseTimes.get(pid) == 0) {
					responseTimes.put(pid, globalTick);
				}
			}

			for (String pid : queues.get("terminated")) {
				if (turnaroundTimes.get(pid) == 0) {
					turnaroundTimes.put(pid, globalTick);
				}
			}

			totalTicks++;
			if (!queues.get("running").isEmpty()) {
				cpuUtilTicks++;
			}
		}
	}

	public void decideWhoGetsCpu(SimulationSettings simSettings, String pid) throws IOException {
		if (queues.get("running").isEmpty()) {
			// FCFS
			if (pid == queues.get("ready").get(0)) {
				moveProcess(pid, "ready", "running", silent);
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


	public void printStats() throws FileNotFoundException {
		int cpuWaitSum = 0;
		int ioWaitSum = 0;
		int responseSum = 0;
		int turnaroundSum = 0;


		for (String pid : responseTimes.keySet()) {
			int waitTime = responseTimes.get(pid);
			System.out.println("Response time for pid " + pid + " : " + waitTime + " ticks");
			responseSum += waitTime;
		}

		System.out.println();

		for (String pid : turnaroundTimes.keySet()) {
			int waitTime = turnaroundTimes.get(pid);
			System.out.println("Turnaround time for pid " + pid + " : " + waitTime + " ticks");
			turnaroundSum += waitTime;
		}

		System.out.println();

		for (String pid : cpuWaitTimes.keySet()) {
			int waitTime = cpuWaitTimes.get(pid);
			System.out.println("Total CPU wait time for pid " + pid + " : " + waitTime + " ticks");
			cpuWaitSum += waitTime;
		}

		System.out.println();

		for (String pid : ioWaitTimes.keySet()) {
			int waitTime = ioWaitTimes.get(pid);
			System.out.println("Total IO wait time for pid " + pid + " : " + waitTime + " ticks");
			ioWaitSum += waitTime;
		}

		System.out.println();

		System.out.println("Average CPU wait time: " + ((double) cpuWaitSum / processes.size()));

		System.out.println();

		System.out.println("Average IO wait time: " + ((double) ioWaitSum / processes.size()));

		System.out.println();

		System.out.println("CPU utilization: " + ((double) cpuUtilTicks / totalTicks * 100) + " %");
	}


	public void outputStatsCsv(String fileName, String schedulingAlgo, String scenarioFile) throws IOException, FileNotFoundException {
		File out = new File(fileName);
		FileWriter fw = new FileWriter(out, true);
		BufferedWriter bw = new BufferedWriter(fw);

		int cpuWaitSum = 0;
		int ioWaitSum = 0;
		int responseSum = 0;
		int turnaroundSum = 0;


		for (String pid : responseTimes.keySet()) {
			int waitTime = responseTimes.get(pid);
			// System.out.println("Response time for pid " + pid + " : " + waitTime + " ticks");
			responseSum += waitTime;
		}

		for (String pid : turnaroundTimes.keySet()) {
			int waitTime = turnaroundTimes.get(pid);
			// System.out.println("Turnaround time for pid " + pid + " : " + waitTime + " ticks");
			turnaroundSum += waitTime;
		}

		for (String pid : cpuWaitTimes.keySet()) {
			int waitTime = cpuWaitTimes.get(pid);
			// System.out.println("Total CPU wait time for pid " + pid + " : " + waitTime + " ticks");
			cpuWaitSum += waitTime;
		}

		for (String pid : ioWaitTimes.keySet()) {
			int waitTime = ioWaitTimes.get(pid);
			// System.out.println("Total IO wait time for pid " + pid + " : " + waitTime + " ticks");
			ioWaitSum += waitTime;
		}

		bw.write('\n' + scenarioFile + ",");

		bw.write(schedulingAlgo + ",");

		bw.write(((double) cpuWaitSum / processes.size()) + ",");

		bw.write(((double) ioWaitSum / processes.size()) + ",");

		bw.write(((double) responseSum / processes.size()) + ",");

		bw.write(((double) turnaroundSum / processes.size()) + ",");

		bw.write(((double) cpuUtilTicks / totalTicks * 100) + "");

		bw.close();
		fw.close();
	}
}


