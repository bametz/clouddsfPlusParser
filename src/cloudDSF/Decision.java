package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a decision of the cloudDSF
 * 
 * @author Metz
 *
 */
public class Decision extends CloudDSFEntity {

	private List<Outcome> outcomes = new ArrayList<Outcome>();

	public Decision(String label, int id, int cluster, int parent,
			String classification, String description, String additionalInfo, String abbrev) {
		super(id, "dec", label);
		this.setClassification(classification);
		this.setDescription(description);
		this.setCluster(cluster);
		this.setGroup("dec" + cluster);
		this.setParent(parent);
		this.setAdditionalInfo(additionalInfo);
		this.setAbbrev(abbrev);
		
	}

	/**
	 * Decision constructor for legacy cloudDSF
	 * 
	 * @param label
	 * @param classification
	 * @param id
	 * @param parent
	 */
	public Decision(String label, String classification, int id, int parent) {
		super(id, "decision", label);
		this.setClassification(classification);
		this.setParent(parent);
	}

	/**
	 * Sorts outcomes by ascending id for better readability in json
	 */
	public void sortOutcomes() {
		Collections.sort(outcomes, new CloudDSFEntityComparator());
	}

	public Outcome getOutcome(String key) {
		for (Outcome outcome : outcomes) {
			if (outcome.getLabel().equals(key)) {
				return outcome;
			}
		}
		return null;
	}

	public void addOutcome(Outcome outcome) {
		outcomes.add(outcome);
	}

	@JsonProperty("children")
	public List<Outcome> getOutcomes() {
		return outcomes;
	}
}
