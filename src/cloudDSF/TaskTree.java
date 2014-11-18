package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TaskTree {
	private final int id = 9;
	private final String type = "root";
	private final String label = "Tasks";

	private transient HashMap<String, Task> tasks = new HashMap<String, Task>();
	private List<Task> children = new ArrayList<Task>();

	public void setChildren(HashMap<String, Task> hashMap) {
		this.tasks = hashMap;
	}

	public void prepareSortedTasks() {
		children.clear();
		for (Task t : tasks.values()) {
			children.add(t);
		}
		Collections.sort(children, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				int i = t1.getId() - t2.getId();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});
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
