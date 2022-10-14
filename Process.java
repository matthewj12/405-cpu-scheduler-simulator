import org.json.*;

import java.util.ArrayList;
import java.util.List;


public class Process {
	public String name;
	public String arrivalTime;
	public String priority;
	// We can tell if it's a cpu or io burst based on the number of bursts remaining (cpu, io, ..., io, cpu)
	public List<Integer> remainingBursts;

	public Process(String jsonString) {
		JSONObject jsonObj = new JSONObject(jsonString);

		name = jsonObj.get("name").toString();
		arrivalTime = jsonObj.get("arrival time").toString();
		priority = jsonObj.get("priority").toString();

		remainingBursts = new ArrayList<Integer>();

		for (Object o : jsonObj.getJSONArray("bursts")) {
			remainingBursts.add(Integer.parseInt(o.toString()));
		}
	}
}