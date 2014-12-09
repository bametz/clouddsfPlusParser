package cloudDSF;


/**
 * Superclass for all entities of the cloudDSF
 * 
 * @author Metz
 *
 */
public class CloudDSFEntity {
	// Basic Information
	// unique id
	private int id;
	// name of object
	private String label;
	// object type e.g. out, dec, dp
	private String type;
	// parent in hierarchy
	private int parent;
	// classification (legacy)
	private String classification;
	// decision point group e.g. 1, 2, 3, 4
	private int cluster;
	// type and cluster e.g. out1, dp4
	private String group;
	// description of the object
	private String description;
	// additional information
	private String additionalInfo;
	private String abbrev;

	public CloudDSFEntity(int id, String type, String label) {
		this.id = id;
		this.type = type;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setParent(int parent) {
		this.parent = parent;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getClassification() {
		return classification;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public int getParent() {
		return this.parent;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
}
