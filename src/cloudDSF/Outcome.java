package cloudDSF;

/**
 * Represents a outcome of the cloudDSF(Plus)
 * 
 * @author Metz
 *
 */

public class Outcome extends CloudDSFEntity {
	/**
	 * Outcome constructor for the cloudDSFPlus
	 * 
	 * @param label
	 * @param id
	 * @param cluster
	 * @param parent
	 * @param description
	 * @param additionalInfo
	 * @param abbrev
	 */
	public Outcome(String label, int id, int cluster, int parent,
			String description, String additionalInfo, String abbrev) {
		super(id, "out", label);
		this.setDescription(description);
		this.setCluster(cluster);
		this.setGroup("out" + cluster);
		this.setParent(parent);
		this.setAdditionalInfo(additionalInfo);
		this.setAbbrev(abbrev);
	}

	/**
	 * Outcome constructor for the cloudDSF
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
