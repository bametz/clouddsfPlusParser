package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Decision {
	private int id;
	private final String type = "decision";
	private int parent;
	private String classification;
	private String label;

	private transient HashMap<String, Outcome> outcomes = new HashMap<String, Outcome>();
	@SerializedName("children")
	private List<Outcome> outcomesSorted = new ArrayList<Outcome>();
	
	public List<Outcome> getOutcomesSorted() {
		return outcomesSorted;
	}

	public void setOutcomesSorted(List<Outcome> outcomesSorted) {
		this.outcomesSorted = outcomesSorted;
	}

	public Decision(String label, String classification, int id, int parent) {
		this.label = label;
		this.classification = classification;
		this.id = id;
		this.parent = parent;
	}

	public void prepareSortedOutcomes() {
//		outcomesSorted.clear();
//		for (Outcome outcome : outcomes.values()) {
//			outcomesSorted.add(outcome);
//		}
		Collections.sort(outcomesSorted, new Comparator<Outcome>() {
			public int compare(Outcome o1, Outcome o2) {
				int i = o1.getId() - o2.getId();
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

	public HashMap<String, Outcome> getOutcomes() {
		return outcomes;
	}

	public void addOutcome(Outcome outcome) {
		outcomes.put(outcome.getLabel(), outcome);
		outcomesSorted.add(outcome);
	}

	public Outcome getOutcome(String key) {
		return outcomes.get(key);
	}
}
