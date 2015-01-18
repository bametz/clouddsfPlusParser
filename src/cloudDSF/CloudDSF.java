package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.CloudDSFEntityComparator;
import util.RelationComparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the coudDSF(Plus) object with decision points, decisions, outcomes
 * and their relations.
 * 
 * @author Metz
 */

public class CloudDSF extends CloudDSFEntity {

	private List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();

	private List<DecisionRelation> influencingDecisions = new ArrayList<DecisionRelation>();

	private List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();

	private List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();

	private List<Task> tasks = new ArrayList<Task>();

	private List<Relation> influencingRelations = new ArrayList<Relation>();

	public CloudDSF(int id, String type, String label) {
		super(id, type, label);
		this.setGroup("root");
	}

	/**
	 * Searches decisions and sets relation between them
	 * 
	 * @param startDecision
	 * @param endDecision
	 * @param type
	 * @param explanation
	 * @param additionalInfo
	 */
	public void setDecisionRelation(String startDecision, String endDecision,
			String type, String explanation) {
		int source = getDecision(startDecision).getId();
		int target = getDecision(endDecision).getId();
		influencingDecisions.add(new DecisionRelation(source, target, type,
				explanation));
	}

	/**
	 * Sets relation between two decisions by retrieving their id and putting it
	 * into influencingRelations.
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
				explanation));
	}

	/**
	 * Sets task relation accordingly to the specified type
	 * 
	 * @param sourceDesc
	 * @param targetDesc
	 * @param dir
	 * @param label
	 */
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
		// switch of source and target
		case "backwards":
			source = getDecision(targetDesc).getId();
			target = getTask(sourceDesc).getId();
			dir = "auto";
			break;
		// no default always has to have a specified direction
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
	 * Helper Method to print out content of cloudDSF Object to check content.
	 */
	public void printCloudDSF() {
		int dpamount = 0;
		int damount = 0;
		int oamount = 0;
		int dRelations = 0;
		int tRelations = 0;
		int oRelationsAmount = 0;
		int tamount = 0;

		int aRelations = 0;
		int inRelations = 0;
		int exRelations = 0;
		int affRelations = 0;
		int binRelations = 0;
		int dpaRelations = 0;
		int dpinRelations = 0;
		int dpexRelations = 0;
		int dpaffRelations = 0;
		int dpbinRelations = 0;

		int dreqRelations = 0;
		int dinfRelations = 0;
		int daffRelations = 0;
		int dbinRelations = 0;
		int dpDecReqRelations = 0;
		int dpDecinfRelations = 0;
		int dpdecaffRelations = 0;
		int dpdecbinRelations = 0;

		for (DecisionPoint dp : getDecisionPoints()) {
			dpaRelations = 0;
			dpinRelations = 0;
			dpexRelations = 0;
			dpaffRelations = 0;
			dpbinRelations = 0;

			dpDecReqRelations = 0;
			dpDecinfRelations = 0;
			dpdecaffRelations = 0;
			dpdecbinRelations = 0;

			dpamount++;
			System.out.println("Decision Point Name = " + dp.getLabel()
					+ " ID " + dp.getId());
			for (Decision d : dp.getDecisions()) {
				aRelations = 0;
				inRelations = 0;
				exRelations = 0;
				affRelations = 0;
				binRelations = 0;

				dreqRelations = 0;
				dinfRelations = 0;
				daffRelations = 0;
				dbinRelations = 0;

				damount++;

				for (Outcome o : d.getOutcomes()) {
					oamount++;
					// System.out.println("Outcome " + o.getLabel() + " ID "
					// + o.getId() + " parentId " + " Weight ");
					for (OutcomeRelation oRelation : influencingOutcomes) {
						if (oRelation.getSource() == o.getId())
							switch (oRelation.getType()) {
							case "in":
								inRelations++;
								dpinRelations++;
								break;
							case "ex":
								exRelations++;
								dpexRelations++;
								break;
							case "eb":
								binRelations++;
								dpbinRelations++;
								break;
							case "aff":
								affRelations++;
								dpaffRelations++;
								break;
							case "a":
								aRelations++;
								dpaRelations++;
								break;
							default:
								break;
							}
					}

				}

				for (DecisionRelation dRelation : influencingDecisions) {
					if (dRelation.getSource() == d.getId())
						switch (dRelation.getType()) {
						case "Influencing":
							dinfRelations++;
							dpDecinfRelations++;
							break;
						case "Binding":
							dbinRelations++;
							dpdecbinRelations++;
							break;
						case "Affecting":
							daffRelations++;
							dpdecaffRelations++;
							break;
						case "Requiring":
							dreqRelations++;
							dpDecReqRelations++;
							break;
						default:
							break;
						}

				}

				System.out.println("Decision " + d.getLabel() + " has:");
				System.out.println("Affecting: " + daffRelations);
				System.out.println("Binding: " + dbinRelations);
				System.out.println("Influencing: " + dinfRelations);
				System.out.println("Requiring: " + dreqRelations);
				System.out.println("#### Corresponding Outcome Relations ####");

				System.out.println("Including: " + inRelations);
				System.out.println("Excluding: " + exRelations);
				System.out.println("Allowing: " + aRelations);
				System.out.println("Affecting: " + affRelations);
				System.out.println("Binding: " + binRelations);

			}
			System.out.println("Decision Point " + dp.getLabel() + " has:");
			System.out.println("Affecting: " + dpdecaffRelations);
			System.out.println("Binding: " + dpdecbinRelations);
			System.out.println("Influencing: " + dpDecinfRelations);
			System.out.println("Requiring: " + dpDecReqRelations);

			System.out.println("#### Corresponding Outcome Relations ####");

			System.out.println("Including: " + dpinRelations);
			System.out.println("Excluding: " + dpexRelations);
			System.out.println("Allowing: " + dpaRelations);
			System.out.println("Affecting: " + dpaffRelations);
			System.out.println("Binding: " + dpbinRelations);
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

		aRelations = 0;
		inRelations = 0;
		exRelations = 0;
		affRelations = 0;
		binRelations = 0;

		for (OutcomeRelation oRelation : influencingOutcomes) {
			switch (oRelation.getType()) {
			case "in":
				inRelations++;
				break;
			case "ex":
				exRelations++;
				break;
			case "eb":
				binRelations++;
				break;
			case "aff":
				affRelations++;
				break;
			case "a":
				aRelations++;
				break;
			default:
				break;
			}
			// System.out.println("Outcome Relationship from "
			// + oRelation.getSource() + " to " + oRelation.getTarget()
			// + " with type" + oRelation.getType());
			oRelationsAmount++;
		}
		System.out.println("Total outcome Relations " + aRelations
				+ "a Relations " + exRelations + "ex Relations " + inRelations
				+ "in Relations " + binRelations + "binding Relations "
				+ affRelations + "aff Relations");

		for (TaskRelation tRelation : influencingTasks) {
			System.out.println("Task Relationship from "
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
