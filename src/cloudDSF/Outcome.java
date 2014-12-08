package cloudDSF;

/**
 * Represents a outcome of the cloudDSF
 * 
 * @author Metz
 *
 */

public class Outcome extends CloudDSFEntity {

	public Outcome(String label, int id, int cluster, int parent,
			String description) {
		super(id, "out", label);
		this.setDescription(description);
		this.setCluster(cluster);
		this.setGroup("out" + cluster);
		this.setParent(parent);
		// todo
		// this.setAdditionalInfo(additionalInfo);
	}

	/**
	 * Outcome constructor for legacy cloudDSF
	 * 
	 * @param label
	 * @param id
	 * @param parent
	 */
	public Outcome(String label, int id, int parent) {
		super(id, "outcome", label);
		this.setParent(parent);
	}
}
