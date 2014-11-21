package cloudDSF;

import java.util.ArrayList;
import java.util.List;

public class TaskTree extends CloudDSFEntity {
	
	private List<Task> tasks = new ArrayList<Task>();

	public TaskTree() {
		this.setId(9);
		this.setType("root");
		this.setLabel("Tasks");
	}

	public TaskTree(int id, String type, String label) {
		this.setId(id);
		this.setType(type);
		this.setLabel(label);
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
