package cloudDSF;

/**
 * Represents relation between two outcomes for the cloudDSFPlus.
 * 
 * @author Metz
 *
 */
public class OutcomeRelation extends Relation {
/**
 * Constructor for an outcome relation.
 * @param source
 * @param target
 * @param type
 * @param explanation
 */
  public OutcomeRelation(int source, int target, String type, String explanation) {
    super(source, target, type);
    this.setRelationGroup("outRel");
    // No explanation needed
    // this.setExplanation(explanation);
  }

  public OutcomeRelation(int source, int target, String type) {
    super(source, target, type);
  }
}
