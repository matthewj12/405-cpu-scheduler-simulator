public class SimulationSettings {
	public boolean autoStep;
	public int quantumTime;
	public int ticksPerStep;
	public String schedulingAlgo;

	public SimulationSettings() {
	}

	public SimulationSettings(boolean as, int qt, int sut, String sa) {
		autoStep = as;
		quantumTime = qt;
		ticksPerStep = sut;
		schedulingAlgo = sa;
	}
}
