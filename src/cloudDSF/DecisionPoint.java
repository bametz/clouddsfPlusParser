package cloudDSF;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.annotations.SerializedName;

public class DecisionPoint {
	private int id;
	private final String type = "decisionPoint";
	private int parent;
	private String classification;
	private String label;
	
	private transient HashMap<String, Decision> decisions = new HashMap<String, Decision>();
	@SerializedName("children")
	private SortedMap<Integer, Decision> decisionsSorted = new TreeMap<Integer, Decision>();

	public DecisionPoint(String label, int id, String classification) {
		this.label = label;
		this.classification = classification;
		this.id = id;
	}

	public SortedMap<Integer, Decision> getDecisionsSorted() {
		return decisionsSorted;
	}

	public void prepareSortedDecisions() {
		decisionsSorted.clear();
		for (Decision d : decisions.values()) {
			decisionsSorted.put(d.getId(), d);
		}
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
		// should return null
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
	}

	public Decision getDecision(String key) {
		return this.decisions.get(key);
	}
}
