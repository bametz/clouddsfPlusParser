package cloudDSF;

public class TaskRelation extends Relation {

	public TaskRelation(int source, int target, String dir,
			String label) {
		super(source, target, dir, 1, label, "taskRel");
	}
}
