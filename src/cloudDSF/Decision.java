package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents one decision of the cloudDSF
 * 
 * @author Metz
 *
 */
public class Decision {

	private int id;
	private final String type = "decision";
	private int parent;
	private String classification;
	private String label;

	@SerializedName("children")
	private List<Outcome> outcomes = new ArrayList<Outcome>();

	public Decision(String label, String classification, int id, int parent) {
		this.label = label;
		this.classification = classification;
		this.id = id;
		this.parent = parent;
	}

	/**
	 * Sorts array list to offer outcomes sorted by ascending id for json
	 */
	public void prepareSortedOutcomes() {
		Collections.sort(outcomes, new Comparator<Outcome>() {
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

	public Outcome getOutcome(String key) {
		for (Outcome outcome : outcomes) {
			if (outcome.getLabel().equals(key)) {
				return outcome;
			}
		}
		return null;
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
