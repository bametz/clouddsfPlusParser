package cloudDSF;

/**
 * Represents a relation between two decisions.
 * 
 * @author Metz
 *
 */
public class DecisionRelation extends Relation {

	public DecisionRelation(int source, int target, String type,
			String explanation, String additionalInfo) {
		super(source, target, type);
		this.setRelationGroup("decRel");
		// this.setAdditionalInfo(additionalInfo);
		// this.setExplanation(explanation);
	}

	/**
	 * Decision relation constructor for legacy cloudDSF
	 * @param source
	 * @param target
	 */
	public DecisionRelation(int source, int target) {
		super(source, target, "DecRel");
		this.setDir("auto");
	}
}
