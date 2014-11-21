package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import util.CloudDSFEntityComparator;
import util.RelationComparator;

import com.google.gson.annotations.SerializedName;

/**
 * @author Metz Main class to represent the CloudDSF object with dps, ds and os.
 */

public class CloudDSF extends CloudDSFEntity {

	@SerializedName("children")
	private List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();

	private transient List<DecisionRelation> influencingDecisions = new ArrayList<DecisionRelation>();

	private transient List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();

	private transient List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();

	private transient List<Task> tasks = new ArrayList<Task>();

	@SerializedName("linksArray")
	private List<Relation> influencingRelations = new ArrayList<Relation>();

	public DecisionPoint getDecisionPoint(String decisionPointName) {
		for (DecisionPoint decisionPoint : decisionPoints) {
			if (decisionPoint.getLabel().equals(decisionPointName)) {
				return decisionPoint;
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
				return d.getOutcome(outcomeName);
			}
		}
		return null;
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
	public void setDecisionRelation(String startDecision, String endDecision,
			String label) {
		int source = getDecision(startDecision).getId();
		int target = getDecision(endDecision).getId();
		// label is rather type
		DecisionRelation di = new DecisionRelation(source, target, label);
		influencingDecisions.add(di);
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
			String label) {
		// int source = 0;
		// int target = 0;
		// for (DecisionPoint dp : decisionPoints) {
		// for (Decision d : dp.getDecisions()) {
		// for (Outcome o : d.getOutcomes()) {
		// if (o.getLabel().equals(startOutcome)) {
		// source = o.getId();
		// }
		// if (o.getLabel().equals(endOutcome)) {
		// target = o.getId();
		// }
		// if (source != 0 && target != 0) {
		// break;
		// }
		// }
		// }
		// }
		// int source =
		// label is rather type

		int source = getOutcome(startOutcome).getId();
		int target = getOutcome(endOutcome).getId();
		OutcomeRelation or = new OutcomeRelation(source, target, label);
		influencingOutcomes.add(or);
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
			source = getDecision(sourceDesc).getId();
			target = getTask(targetDesc).getId();
			dir = "auto";
			break;
		}
		TaskRelation tr = new TaskRelation(source, target, dir, label);
		influencingTasks.add(tr);
	}

	/**
	 * Helper Method to print out content of cloudDSF to check content.
	 */
	public void printCloudDSF() {
		int dpamount = 0;
		int damount = 0;
		int oamount = 0;
		int relations = 0;
		int oRelationsAmount = 0;
		int tamount = 0;

		for (DecisionPoint dp : getDecisionPointsSorted()) {
			dpamount++;
			System.out.println("Decision Point Name = " + dp.getLabel()
					+ " ID " + dp.getId());
			for (Decision d : dp.getDecisions()) {
				damount++;
				System.out.println("Decision " + d.getLabel() + " ID "
						+ d.getId() + " parentId " + d.getParent());

				for (Outcome o : d.getOutcomes()) {
					oamount++;
					System.out.println("Outcome " + o.getLabel() + " ID "
							+ o.getId() + " parentId " + o.getParent()
							+ " Weight " + o.getWeight());
				}
			}
		}
		for (Relation relation : influencingDecisions) {
			System.out.println(relation.getSource() + " to "
					+ relation.getTarget() + " label " + relation.getLabel());
			relations++;
		}
		for (OutcomeRelation oRelation : influencingOutcomes) {
			System.out.println(oRelation.getSource() + " to "
					+ oRelation.getTarget() + " label " + oRelation.getLabel());
			oRelationsAmount++;
		}

		for (Task t : tasks) {
			System.out.println("task label " + t.getLabel() + " " + t.getId());
			tamount++;
		}

		System.out.println("anzahl dp " + dpamount);
		System.out.println("anzahl d " + damount);
		System.out.println("anzahl o " + oamount);
		System.out.println("anzahl rel " + relations);
		System.out.println("anzahl out rel " + oRelationsAmount);
		System.out.println(tamount);
	}

	public List<TaskRelation> getInfluencingTasks() {
		return influencingTasks;
	}

	public void setInfluencingTasks(List<TaskRelation> influencingTasks) {
		this.influencingTasks = influencingTasks;
	}

	public void addDecisionPoint(DecisionPoint dp) {
		decisionPoints.add(dp);
	}

	public List<OutcomeRelation> getInfluencingOutcomes() {
		return influencingOutcomes;
	}

	public void setInfluencingOutcomes(List<OutcomeRelation> influencingOutcomes) {
		this.influencingOutcomes = influencingOutcomes;
	}

	public List<DecisionPoint> getDecisionPointsSorted() {
		return decisionPoints;
	}

	public void setDecisionPointsSorted(List<DecisionPoint> decisionPointsSorted) {
		this.decisionPoints = decisionPointsSorted;
	}

	public List<DecisionPoint> getDecisionPoints() {
		return decisionPoints;
	}

	public void setDecisionPoints(List<DecisionPoint> decisionPoints) {
		this.decisionPoints = decisionPoints;
	}

	public List<DecisionRelation> getInfluencingDecisions() {
		return influencingDecisions;
	}

	public void setInfluencingDecisions(
			List<DecisionRelation> influencingDecisions) {
		this.influencingDecisions = influencingDecisions;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Relation> getInfluencingRelations() {
		return influencingRelations;
	}

	public void setInfluencingRelations(List<Relation> influencingRelations) {
		this.influencingRelations = influencingRelations;
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	/**
	 * Sort of all ArrayLists to produce sorted Output in id ascending order
	 */
	public void sortAllLists() {
		CloudDSFEntityComparator cec = new CloudDSFEntityComparator();
		RelationComparator rc = new RelationComparator();
		Collections.sort(decisionPoints, cec);
		Collections.sort(influencingDecisions, rc);
		Collections.sort(influencingOutcomes, rc);
		Collections.sort(influencingTasks, rc);
		Collections.sort(tasks, cec);
	}

	public void sortInfluencingRelations() {
		Collections.sort(influencingRelations, new RelationComparator());
	}
}
