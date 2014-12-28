package cloudDSF;

/**
 * Superclass for relations between entities in the cloudDSF and cloudDSFPlus
 * 
 * @author Metz
 *
 */
public class Relation {
	private int source;
	private int target;
	private String dir;
	private String relationGroup;
	private String type;
	private String explanation;
	private String additionalInfo;

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

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getRelationGroup() {
		return relationGroup;
	}

	public void setRelationGroup(String relationGroup) {
		this.relationGroup = relationGroup;
	}
}
