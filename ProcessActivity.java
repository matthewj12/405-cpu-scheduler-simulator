import org.json.*;

import java.util.ArrayList;
import java.util.List;


public class ProcessActivity {
	public String name;
	public String arrivalTime;
	public String priority;
	public List<String> cpuBursts;
	public List<String> ioBursts;

	public ProcessActivity(JSONObject jsonObj) {
		name = jsonObj.get("name").toString();
		arrivalTime = jsonObj.get("arrival time").toString();
		priority = jsonObj.get("priority").toString();

		cpuBursts = new ArrayList<String>();
		ioBursts = new ArrayList<String>();

		for (Object o : jsonObj.getJSONArray("cpu bursts")) {
			cpuBursts.add(o.toString());
		}

		for (Object o : jsonObj.getJSONArray("io bursts")) {
			ioBursts.add(o.toString());
		}
	}
}