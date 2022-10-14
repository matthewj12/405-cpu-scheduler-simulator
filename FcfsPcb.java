import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class FcfsPcb extends AbstractPcb {
	public FcfsPcb(ArrayList<Process> processes, String logFileName) {
		super(processes, logFileName);
	}


	public void stepSimulation(SimulationSettings simSettings) throws IOException {
		visualize();

		moveProcess("0", "not ready", "ready");

		visualize();

		// Make it so all queues are empty
		queues.put("not ready",  new LinkedList<String>());
		queues.put("ready",      new LinkedList<String>());
		queues.put("running",    new LinkedList<String>());
		queues.put("waiting",    new LinkedList<String>());
		queues.put("terminated", new LinkedList<String>());
		
/*
	//quantum time should (for example, if quantum time = 2 and simUnitTime = 10, we loop 5 times)
	// tick = smallest unit of time used to specify burst duration (e.g. millisecond)
	// simSettings.quantumTime = maximum chunk of time allocated for preemptive algorithms like round robin
	// simSettings.simUnitTime = time between user log entries and, optionally, user input (if simSettings.autoStep = false)

	for each tick {
		for (String pid : notReady) {
			moveProcess(pid, "not ready", "ready");
		}

		for (String pid : ready) {
			if (theSchedulingAlgoGivesThisPidTheGoAhead) {
				moveProcess(pid, "ready", "running");
				
			}
		}

		// only loops once for our single-core simulation
		for (String pid : running) {
			--processes.get(pid).remainingBursts.get(0);
			
			if (processes.get(pid).remainingBursts.get(0) == 0) {
				processes.get(pid).remainingBursts.remove(0);
				moveProcess(pid, "running", "waiting");
			}
		}

		// can loop multiple times even though our simulation only has one IO device due to 
		// the fact that there's no "ready" queue for IO devices like there is for the CPU
		for (String pid : waiting) {
			--processes.get(pid).remainingBursts.get(0);
			
			if (processes.get(pid).remainingBursts.get(0) == 0) {
				processes.get(pid).remainingBursts.remove(0);
				moveProcess(pid, "waiting", "running");
			}
		}
	}

*/

	}
}
