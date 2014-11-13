package cloudDSF;

import java.util.HashMap;
import java.util.TreeMap;

import com.google.gson.annotations.SerializedName;

public class Decision {
	private int id;
	private final String type = "decision";
	private int parent;
	private String classification;
	private String label;

	private transient HashMap<String, Outcome> outcomes = new HashMap<String, Outcome>();
	@SerializedName("children")
	private TreeMap<Integer, Outcome> outcomesSorted = new TreeMap<Integer, Outcome>();


	public Decision(String label, String classification, int id, int parent) {
		this.label = label;
		this.classification = classification;
		this.id = id;
		this.parent = parent;
	}

	public TreeMap<Integer, Outcome> getOutcomesSorted() {
		return outcomesSorted;
	}

	public void prepareSortedOutcomes() {
		outcomesSorted.clear();
		for (Outcome o : outcomes.values()) {
			outcomesSorted.put(o.getId(), o);
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

	public HashMap<String, Outcome> getOutcomes() {
		return outcomes;
	}

	public void addOutcome(Outcome outcome) {
		outcomes.put(outcome.getLabel(), outcome);
	}

	public Outcome getOutcome(String key) {
		return outcomes.get(key);
	}
}
