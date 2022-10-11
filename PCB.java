import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

public abstract class PCB {
	enum Event {
		move
	}
	
	public int CPUs;
	// We're assuming all io devices are of the same type (akin to typical cpu cores)
	public int IOs;
	
	public HashMap<String, Queue<String>> queues;
	// time that it occured : 
	public HashMap<String, String> events;

	public PCB(ArrayList<ProcessActivity> scenario) {
		queues = new HashMap<String, Queue<String>>();

		queues.put("not ready",  new LinkedList<String>());
		queues.put("ready",      new LinkedList<String>());
		queues.put("running",    new LinkedList<String>());
		queues.put("waiting",    new LinkedList<String>());
		queues.put("terminated", new LinkedList<String>());

		int pidToAssign = 0;

		for (ProcessActivity p : scenario) {
			queues.get("not ready").add(Integer.toString(pidToAssign+1));
		}
	}

	public void visualizeDevice(boolean deviceIsIo) {
		if (deviceIsIo) {
			// 
		}
		else {

		}
		// Print all queues
		// Print all global simulation statistics ()
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
	public abstract void stepSimulation(int quantumTime, int simUnitTime);
}
