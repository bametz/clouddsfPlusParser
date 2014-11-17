package cloudDSF;

/**
 * Represents relation between two outcomes.
 * 
 * @author Metz
 *
 */
public class OutcomeRelation {
	private int source;
	private int target;
	private String dir;
	private int weight;
	private String label;
	private String type;

	public OutcomeRelation(int source, int target, String dir, int weight,
			String label, String type) {
		this.source = source;
		this.target = target;
		this.label = label;
		this.type = type;
		this.weight = weight;
		this.dir = dir;
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
