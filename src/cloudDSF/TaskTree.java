package cloudDSF;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TaskTree {
	private int id;
	private String type;
	private String label;

	@SerializedName("children")
	private List<Task> tasks = new ArrayList<Task>();

	public TaskTree() {
		this.id = 9;
		this.type = "root";
		this.label = "Tasks";
	}

	public TaskTree(int id, String type, String label) {
		this.id = id;
		this.type = type;
		this.label = label;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}
}
