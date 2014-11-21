package cloudDSF;

public class Task extends CloudDSFEntity {

	public Task(int id, String label) {
		this.setType("task");
		this.setId(id);
		this.setLabel(label);
	}
}
