package cloudDSF;

/**
 * Superclass for relations between entities in the cloudDSF and cloudDSFPlus.
 * 
 * @author Metz
 *
 */
public class Relation {
  /**
   * id of decision / outcome.
   */
  private int source;
  /**
   * id of decision / outcome.
   */
  private int target;
  /**
   * Direction of relation.
   */
  private String dir;
  /**
   * type of relation group (decRel, outRel...)
   */
  private String relationGroup;
  /**
   * Relationship type.
   */
  private String type;
  /**
   * Additional info if necessary.
   */
  private String explanation;

  /**
   * Constructor for Relation.
   * 
   * @param source id of source of relation
   * @param target id of target of relation
   * @param type relationship type
   */
  public Relation(int source, int target, String type) {
    this.source = source;
    this.target = target;
    this.type = type;
  }

  public int getTarget() {
    return target;
  }

  public void setTarget(int target) {
    this.target = target;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public String getDir() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public String getRelationGroup() {
    return relationGroup;
  }

  public void setRelationGroup(String relationGroup) {
    this.relationGroup = relationGroup;
  }
}
