import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class SjfPcb extends AbstractPcb {
	public SjfPcb(ArrayList<Process> processes, String logFileName) {
		super(processes, logFileName);
	}


	public void swapWithRunningIfShorter(String pid) throws IOException {
		String currentlyRunning = queues.get("running").get(0);
		
		if (sumRemainingBursts(pid) < sumRemainingBursts(currentlyRunning)) {
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

				switch (curQueue) {
					case "not ready":
						moveProcess(pid, "not ready", "ready");
						break;

					case "ready":
						moveToRunningIfRunningEmpty(pid);
						swapWithRunningIfShorter(pid);
						break;

					case "running":
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
							}
						}

						break;

					case "waiting":
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
								// We can move multiple queues in a single tick
								moveToRunningIfRunningEmpty(pid);
							}
						}

						break;
				}
			}
		}
	}
}
