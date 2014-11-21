package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;

import com.google.gson.annotations.SerializedName;

/**
 * Represents one decision of the cloudDSF
 * 
 * @author Metz
 *
 */
public class Decision extends CloudDSFEntity {

	@SerializedName("children")
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
	public void prepareSortedOutcomes() {
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

	public List<Outcome> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}
}
