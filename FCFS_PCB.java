import java.util.ArrayList;
import java.util.LinkedList;

public class FCFS_PCB extends PCB {
	public int i;

	public FCFS_PCB(ArrayList<ProcessActivity> processes) {
		super(processes);

		i = 0;
	}

	// returns true if, after the very next advancement of unit time ms (at the end of stepSimulation), the current burst will be completed
	public boolean burstIsOnLastStep(String pid, String burstType) {
		return true;
	}

	// returns true if the current burst is the last burst before the process terminates
	public boolean processIsOnLastBurst(String pid) {
		return true;
	}

	public void stepSimulation(int quantumTime, int simUnitTime) {
		i++;

		if (i == 10) {
			queues.put("not ready",  new LinkedList<String>());
			queues.put("ready",      new LinkedList<String>());
			queues.put("running",    new LinkedList<String>());
			queues.put("waiting",    new LinkedList<String>());
			queues.put("terminated", new LinkedList<String>());
		}

		// String queueToMoveTo = "don't move";
		// 	for (String pid : notReady) {
		// 		queueToMoveTo = "ready";
		// 	}

		// 	for (String pid : ready) {
		// 		if (running.size() < CPUs && theSchedulingAlgoGivesThisPidTheGoAhead()) {
		// 			queueToMoveTo = "running";
		// 		}
		// 	}

		// 	for (String pid : running) {
		// 		if (burstIsOnLastStep(pid, "cpu")) {
		// 			queueToMoveTo = 
		// 		}
		// 	}

		// 	for (String pid : waiting) {
		// 		if (burstIsOnLastStep(pid, "io")) {

		// 		}
		// 	}

/* 		for each p in running
				advance progress by unit time

				if the advancement in waiting time caused p to finish it's current cpu burst:
					change state to waiting
		
				need to print a message every time a process moves to a different queue
			
				if p is waiting
						move to waiting queue
				if interrupted (only relevant in preemptive scheduling algos)
						move to ready */


/* 			for each p in waiting
				if p is has finished
				
				*/


		// if there's an open spot in running (only one CPU core atm), choose a p from ready to move to running (this is the heart of the scheduling algorithm aspect)


	}

}
