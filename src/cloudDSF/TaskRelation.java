package cloudDSF;

/**
 * Represents a relation between decision and task for the legacy cloudDSF
 * 
 * @author Metz
 *
 */
public class TaskRelation extends Relation {

	public TaskRelation(int source, int target, String dir) {
		super(source, target, "taskRel");
		this.setDir(dir);
	}
}
