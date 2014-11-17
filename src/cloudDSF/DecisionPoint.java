package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DecisionPoint {
	private int id;
	private final String type = "decisionPoint";
	private int parent;
	private String classification;
	private String label;

	private transient HashMap<String, Decision> decisions = new HashMap<String, Decision>();

	@SerializedName("children")
	private List<Decision> decisionsSorted = new ArrayList<Decision>();

	public List<Decision> getSortedDecisionList() {
		return decisionsSorted;
	}

	public void setSortedDecisionList(List<Decision> sortedDecisionList) {
		this.decisionsSorted = sortedDecisionList;
	}

	public DecisionPoint(String label, int id, String classification) {
		this.label = label;
		this.classification = classification;
		this.id = id;
	}

	public void prepareSortedDecisions() {
//		decisionsSorted.clear();
//		for (Decision d : decisions.values()) {
//			decisionsSorted.add(d);
//		}
		Collections.sort(decisionsSorted, new Comparator<Decision>() {
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

	public HashMap<String, Decision> getDecisions() {
		return decisions;
	}

	public void setDecisions(HashMap<String, Decision> decisions) {
		this.decisions = decisions;
	}

	public void addDecision(Decision decision) {
		this.decisions.put(decision.getLabel(), decision);
		this.decisionsSorted.add(decision);
	}

	public Decision getDecision(String key) {
		return this.decisions.get(key);
	}
}
