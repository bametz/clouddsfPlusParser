package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents one Decision Point of the cloudDSF
 * 
 * @author Metz
 *
 */
public class DecisionPoint {
	private int id;
	private final String type = "decisionPoint";
	private int parent;
	private String classification;
	private String label;

	@SerializedName("children")
	private List<Decision> decisions = new ArrayList<Decision>();

	public DecisionPoint(String label, int id, String classification) {
		this.label = label;
		this.classification = classification;
		this.id = id;
	}

	/**
	 * Sorts decisions by ascending id for json
	 */
	public void prepareSortedDecisions() {
		Collections.sort(decisions, new Comparator<Decision>() {
			public int compare(Decision d1, Decision d2) {
				int i = d1.getId() - d2.getId();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});
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

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addDecision(Decision decision) {
		this.decisions.add(decision);
	}
}
