package cloudDSF;

/**
 * Represents a relation between two decisions in the cloudDSF(Plus).
 * 
 * @author Metz
 *
 */
public class DecisionRelation extends Relation {
	/**
	 * Decision relation constructor for cloudDSFPlus
	 * 
	 * @param source
	 * @param target
	 * @param type
	 * @param explanation
	 * @param additionalInfo
	 */
	public DecisionRelation(int source, int target, String type,
			String explanation) {
		super(source, target, type.toLowerCase());
		this.setRelationGroup("decRel");
		// this.setExplanation(explanation);
	}

	/**
	 * Decision relation constructor for cloudDSF
	 * 
	 * @param source
	 * @param target
	 */
	public DecisionRelation(int source, int target) {
		super(source, target, "DecRel");
		this.setDir("auto");
	}
}
