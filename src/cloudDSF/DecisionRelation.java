package cloudDSF;

/**
 * Represents one relation between two decisions.
 * 
 * @author Metz
 *
 */
public class DecisionRelation extends Relation{
	public DecisionRelation(int source, int target,
			String label) {
		super(source, target, "auto", 1, label, "DecRel");
	}
}
