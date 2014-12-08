package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;
import util.RelationComparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the (legacy) cloudDSF object with dps, ds and os and their
 * relations.
 * 
 * @author Metz
 */

public class CloudDSF extends CloudDSFEntity {
	// instead of transient annotations possible
	private List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();

	private transient List<DecisionRelation> influencingDecisions = new ArrayList<DecisionRelation>();

	private transient List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();

	private transient List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();

	private transient List<Task> tasks = new ArrayList<Task>();

	private transient List<Relation> influencingRelations = new ArrayList<Relation>();

	public CloudDSF(int id, String type, String label) {
		super(id, type, label);
	}

	public void setDecisionRelation(String startDecision, String endDecision,
			String type, String explanation, String additionalInfo) {
		int source = getDecision(startDecision).getId();
		int target = getDecision(endDecision).getId();
		influencingDecisions.add(new DecisionRelation(source, target, type,
				explanation, additionalInfo));
	}

	/**
	 * Sets relation between two decisions by retrieving their id and putting it
	 * in influencingRelations.
	 * 
	 * @param startDecision
	 * @param endDecision
	 * @param label
	 *            Type of relation e.g. influencing, binding, affecting
	 */
	public void setLegacyDecisionRelation(String startDecision,
			String endDecision) {
		int source = getDecision(startDecision).getId();
		int target = getDecision(endDecision).getId();
		influencingDecisions.add(new DecisionRelation(source, target));
	}

	/**
	 * Sets relation between to outcomes by retrieving their id.
	 * 
	 * @param startOutcome
	 * @param endOutcome
	 * @param label
	 *            Type of outcome e.g. ex, in, a
	 */
	public void setOutcomeRelation(String startOutcome, String endOutcome,
			String type, String explanation, String additionalInfo) {
		int source = getOutcome(startOutcome).getId();
		int target = getOutcome(endOutcome).getId();
		influencingOutcomes.add(new OutcomeRelation(source, target, type,
				explanation, additionalInfo));
	}

	public void setTaskRelation(String sourceDesc, String targetDesc,
			String dir, String label) {
		int source = 0;
		int target = 0;
		switch (dir) {
		case "oneWay":
			source = getTask(sourceDesc).getId();
			target = getDecision(targetDesc).getId();
			dir = "auto";
			break;
		case "twoWay":
			source = getTask(sourceDesc).getId();
			target = getDecision(targetDesc).getId();
			dir = "both";
			break;
		case "backwards":
			source = getDecision(targetDesc).getId();
			target = getTask(sourceDesc).getId();
			dir = "auto";
			break;
		}
		TaskRelation tr = new TaskRelation(source, target, dir);
		influencingTasks.add(tr);
	}

	public DecisionPoint getDecisionPoint(String decisionPointName) {
		for (DecisionPoint decisionPoint : decisionPoints) {
			if (decisionPoint.getLabel().equals(decisionPointName)) {
				return decisionPoint;
			}
		}
		return null;
	}

	private Decision getDecision(String decisionName) {
		for (DecisionPoint dp : decisionPoints) {
			Decision d = dp.getDecision(decisionName);
			if (d != null) {
				return d;
			}
		}
		return null;
	}

	private Outcome getOutcome(String outcomeName) {
		for (DecisionPoint dp : decisionPoints) {
			for (Decision d : dp.getDecisions()) {
				Outcome o = d.getOutcome(outcomeName);
				if (o != null) {
					return o;
				}
			}
		}
		return null;
	}

	private Task getTask(String taskName) {
		for (Task t : tasks) {
			if (t.getLabel().equals(taskName)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Sort of all ArrayLists to produce sorted Output in id ascending order
	 */
	public void sortLists() {
		CloudDSFEntityComparator cec = new CloudDSFEntityComparator();
		RelationComparator rc = new RelationComparator();
		Collections.sort(decisionPoints, cec);
		Collections.sort(influencingDecisions, rc);
		Collections.sort(influencingOutcomes, rc);
		Collections.sort(influencingTasks, rc);

	}

	public void sortEntities() {
		CloudDSFEntityComparator cec = new CloudDSFEntityComparator();
		Collections.sort(decisionPoints, cec);
		Collections.sort(tasks, cec);
		for (DecisionPoint dp : decisionPoints) {
			for (Decision d : dp.getDecisions()) {
				d.sortOutcomes();
			}
			dp.sortDecisions();
		}
	}

	public void sortInfluencingRelations() {
		Collections.sort(influencingRelations, new RelationComparator());
	}

	public void addDecisionPoint(DecisionPoint dp) {
		decisionPoints.add(dp);
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	@JsonIgnore
	public List<TaskRelation> getInfluencingTasks() {
		return influencingTasks;
	}

	@JsonIgnore
	public List<OutcomeRelation> getInfluencingOutcomes() {
		return influencingOutcomes;
	}

	@JsonProperty("children")
	public List<DecisionPoint> getDecisionPoints() {
		return decisionPoints;
	}

	@JsonIgnore
	public List<DecisionRelation> getInfluencingDecisions() {
		return influencingDecisions;
	}

	@JsonIgnore
	public List<Task> getTasks() {
		return tasks;
	}

	@JsonIgnore
	public List<Relation> getInfluencingRelations() {
		return influencingRelations;
	}

	public void setInfluencingRelations() {
		influencingRelations.clear();
		influencingRelations.addAll(influencingDecisions);
		influencingRelations.addAll(influencingTasks);
		influencingRelations.addAll(influencingOutcomes);
		sortInfluencingRelations();
	}

	/**
	 * Helper Method to print out content of cloudDSF to check content.
	 */
	public void printCloudDSF() {
		int dpamount = 0;
		int damount = 0;
		int oamount = 0;
		int dRelations = 0;
		int tRelations = 0;
		int oRelationsAmount = 0;
		int tamount = 0;

		for (DecisionPoint dp : getDecisionPoints()) {
			dpamount++;
			System.out.println("Decision Point Name = " + dp.getLabel()
					+ " ID " + dp.getId());
			for (Decision d : dp.getDecisions()) {
				damount++;
				System.out.println("Decision " + d.getLabel() + " ID "
						+ d.getId() + " parentId ");

				for (Outcome o : d.getOutcomes()) {
					oamount++;
					System.out.println("Outcome " + o.getLabel() + " ID "
							+ o.getId() + " parentId " + " Weight ");
				}
			}
		}
		for (Task t : tasks) {
			System.out.println("Task " + t.getLabel() + " " + t.getId());
			tamount++;
		}

		for (DecisionRelation dRelation : influencingDecisions) {
			System.out.println("Decision Relationship from "
					+ dRelation.getSource() + " to " + dRelation.getTarget()
					+ " with 'type' ");
			dRelations++;
		}
		for (OutcomeRelation oRelation : influencingOutcomes) {
			System.out.println("Outcome Relationship from "
					+ oRelation.getSource() + " to " + oRelation.getTarget()
					+ " with 'type' ");
			oRelationsAmount++;
		}

		for (TaskRelation tRelation : influencingTasks) {
			System.out.println("Task Relationshipt from "
					+ tRelation.getSource() + " to " + tRelation.getTarget()
					+ " with type " + tRelation.getDir());
			tRelations++;
		}
		System.out.println("##################################");
		System.out.println("#Decision Points = " + dpamount);
		System.out.println("#Decision = " + damount);
		System.out.println("#Number Outcomes = " + oamount);
		System.out.println("#Number Tasks = " + tamount);
		System.out.println("#Relations between Decisions = " + dRelations);
		System.out.println("#Relations between Outcomes = " + oRelationsAmount);
		System.out.println("#Relations between Tasks and Decision = "
				+ tRelations);
	}
}
