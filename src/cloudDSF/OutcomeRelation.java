package cloudDSF;

/**
 * Represents relation between two outcomes for the cloudDSFPlus
 * 
 * @author Metz
 *
 */
public class OutcomeRelation extends Relation {

	public OutcomeRelation(int source, int target, String type,
			String explanation, String additionalInfo) {
		super(source, target, type);
		this.setRelationGroup("outRel");
		// No additional info or explanation needed
		// this.setAdditionalInfo(additionalInfo);
		// this.setExplanation(explanation);
	}

	public OutcomeRelation(int source, int target, String type) {
		super(source, target, type);
	}
}
