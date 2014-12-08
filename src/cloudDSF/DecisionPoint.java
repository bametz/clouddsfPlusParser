package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a decision point of the cloudDSF
 * 
 * @author Metz
 *
 */
public class DecisionPoint extends CloudDSFEntity {

	private List<Decision> decisions = new ArrayList<Decision>();

	public DecisionPoint(String label, int id, int cluster,
			String classification, String description) {
		super(id, "dP", label);
		this.setClassification(classification);
		this.setDescription(description);
		this.setCluster(cluster);
		this.setGroup("dP" + cluster);
		// this.setAdditionalInfo(additionalInfo);
		// parent stays null
	}

	/**
	 * Decision point constructor for legacy cloudDSF
	 * 
	 * @param label
	 * @param id
	 * @param classification
	 */
	public DecisionPoint(String label, int id, String classification) {
		super(id, "decisionPoint", label);
		this.setClassification(classification);
		// parent stays null
	}

	/**
	 * Sorts decisions by ascending id for better readability in json
	 */
	public void sortDecisions() {
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

	@JsonProperty("children")
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
