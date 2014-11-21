package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents one decision of the cloudDSF
 * 
 * @author Metz
 *
 */
public class Decision extends CloudDSFEntity {

	private List<Outcome> outcomes = new ArrayList<Outcome>();

	public Decision(String label, String classification, int id, int parent) {
		this.setLabel(label);
		this.setClassification(classification);
		this.setId(id);
		this.setParent(parent);
		this.setType("decision");
	}

	/**
	 * Sorts array list to offer outcomes sorted by ascending id for json
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

	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}
}
