package cloudDSF;

/**
 * Represents relation between two outcomes.
 * 
 * @author Metz
 *
 */
public class OutcomeRelation extends Relation {

	public OutcomeRelation(int source, int target, String label) {
		super(source, target, "auto", 1, label, "OutRel");

	}
}
