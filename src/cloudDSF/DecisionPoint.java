package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;

import com.google.gson.annotations.SerializedName;

/**
 * Represents one Decision Point of the cloudDSF
 * 
 * @author Metz
 *
 */
public class DecisionPoint extends CloudDSFEntity {

	@SerializedName("children")
	private List<Decision> decisions = new ArrayList<Decision>();

	public DecisionPoint(String label, int id, String classification) {
		this.setLabel(label);
		this.setClassification(classification);
		this.setId(id);
		this.setType("decisionPoint");
	}

	/**
	 * Sorts decisions by ascending id for json
	 */
	public void prepareSortedDecisions() {
		Collections.sort(decisions, new CloudDSFEntityComparator());
	}

	public Decision getDecision(String key) {
		for (Decision decision : decisions) {
			if (decision.getLabel().equals(key)) {
				return decision;
			}
		}
		return null;
	}

	public List<Decision> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<Decision> sortedDecisionList) {
		this.decisions = sortedDecisionList;
	}

	public void addDecision(Decision decision) {
		this.decisions.add(decision);
	}
}
