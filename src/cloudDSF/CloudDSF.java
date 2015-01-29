package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private Decision getDecision(int decisionId) {
		for (DecisionPoint dp : decisionPoints) {
			Decision d = dp.getDecision(decisionId);
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

	private Outcome getOutcome(int outcomeId) {
		for (DecisionPoint dp : decisionPoints) {
			for (Decision d : dp.getDecisions()) {
				Outcome o = d.getOutcome(outcomeId);
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

	public void checkRelTypesDecisions() {
		for (DecisionRelation infDecisionRelation : influencingDecisions) {
			switch (infDecisionRelation.getType()) {
			case "influencing":
				System.out.println("everything fine");
				break;
			case "requiring":
				System.out.println("everything fine");
				break;
			case "affecting":
				System.out.println("everything fine");
				break;
			case "binding":
				System.out.println("everything fine");
				break;
			default:
				System.out.println("error found in "
						+ infDecisionRelation.getSource() + " to "
						+ infDecisionRelation.getTarget());
			}
		}
	}

	public void checkRelTypesOutcomes() {
		for (OutcomeRelation outRel : influencingOutcomes) {
			switch (outRel.getType()) {
			case "eb":
				System.out.println("everything fine");
				break;
			case "in":
				System.out.println("everything fine");
				break;
			case "a":
				System.out.println("everything fine");
				break;
			case "ex":
				System.out.println("everything fine");
				break;
			case "aff":
				System.out.println("everything fine");
				break;
			default:
				System.out.println("error found in " + outRel.getSource()
						+ " to " + outRel.getTarget());
			}
		}
	}

	public void checkDecRelComb() {
		for (DecisionRelation decRel : influencingDecisions) {
			// type of first decisions
			String relType = decRel.getType();
			for (DecisionRelation decRelComp : influencingDecisions) {
				// another realtions exists between the same two decisions
				if (decRelComp.getSource() == decRel.getSource()
						&& decRelComp.getTarget() == decRel.getTarget()) {
					String relTypeComp = decRelComp.getType();
					// both relations have the same type thus it is the same
					// relations (or a duplicate)
					if (relType.equals(relTypeComp)) {
						System.out.println("same decision relations");
						continue;
					} else {
						// initial relation is requiring and compared relations
						// is one of the other
						if (relType.equals("requiring")
								&& (relTypeComp.equals("influencing")
										|| relTypeComp.equals("affecting") || relTypeComp
											.equals("binding"))) {
							continue;
						}
						// compared relationship is requiring and the initial
						// rel is one of the other types
						else if (relTypeComp.equals("requiring")
								&& (relType.equals("influencing")
										|| relType.equals("affecting") || relType
											.equals("binding"))) {
							continue;
						}
						// both relations are not requiring and thus an error
						// exists
						else {
							System.out.println("error");
							continue;
						}
					}
				} else {
					// relations is not between same decisions
				}
			}
		}
	}

	// check if enough relations are present for a decision relation
	// todo check if type of relations is ok
	public void checkOutRelTypeForDecRel() {
		for (DecisionRelation decRel : influencingDecisions) {
			if (decRel.getType().equals("requiring") == false) {
				// set source and target outcome to check
				Decision sourceDecision = getDecision(decRel.getSource());
				Decision targetDecision = getDecision(decRel.getTarget());
				// traverse starting outcomes
				int foundRelations = sourceDecision.getOutcomes().size()
						* targetDecision.getOutcomes().size();
				boolean rightType = true;
				System.out.println(foundRelations);
				for (Outcome outSource : sourceDecision.getOutcomes()) {
					// traverse target outcomes
					for (Outcome outTarget : targetDecision.getOutcomes()) {
						// traverse outcomeRelations
						for (OutcomeRelation outRel : influencingOutcomes) {
							// if source and target are found than a
							// corresponding
							// relation exists
							if (outSource.getId() == outRel.getSource()
									&& outTarget.getId() == outRel.getTarget()) {
								foundRelations--;
								if (decRel.getType().equals("affecting")) {
									if (outRel.getType().equals("aff") == false) {
										rightType = false;
										break;
									}
								} else if (decRel.getType().equals("binding")) {
									if (outRel.getType().equals("eb") == false) {
										rightType = false;
										break;
									}
								}
								break;
							}
						}
					}
				}
				if (foundRelations != 0) {
					// System.out.println(foundRelations);
					System.out.println("error not enough outcome relations");
				}
				if (rightType == false) {
					System.out
							.println("error an outcome relation does not match with its decision relation");
				} else {
					// everything is fine
					// System.out.println(foundRelations);
				}
			}
		}
	}

	// check if affecting rel exists into one direction that a binding exists
	// into the other way around and vice versa for decisions
	public void checkAffBinDecRelations(String affecting, String binding) {
		int aff = 0;
		int bin = 0;
		for (DecisionRelation decRel : influencingDecisions) {
			// filter affecting relations only
			if (decRel.getType().equals(affecting)) {
				aff++;
				for (DecisionRelation decRelComp : influencingDecisions) {
					// find relation for reverse case
					if (decRel.getSource() == decRelComp.getTarget()
							&& decRel.getTarget() == decRelComp.getSource()) {
						if (decRelComp.getType().equals(binding)) {
							bin++;
							break;
						}
					}
				}
			}
		}
		if (aff != bin) {
			System.out
					.println("error not the same amount of binding and affecting relations");
		} else {
			System.out.println(aff + " " + bin + " relations found");
		}
	}

	// check if affecting rel exists into one direction that a binding exists
	// into the other way around and vice versa for outcomes
	public void checkAffBinOutRelations(String affecting, String binding) {
		int aff = 0;
		int bin = 0;
		for (OutcomeRelation outRel : influencingOutcomes) {
			// filter affecting relations only
			if (outRel.getType().equals(affecting)) {
				aff++;
				for (OutcomeRelation outRelComp : influencingOutcomes) {
					// find relation for reverse case
					if (outRel.getSource() == outRelComp.getTarget()
							&& outRel.getTarget() == outRelComp.getSource()) {
						if (outRelComp.getType().equals(binding)) {
							bin++;
							break;
						}
					}
				}
			}
		}
		if (aff != bin) {
			System.out
					.println("error not the same amount of binding and affecting relations");
		} else {
			System.out.println(aff + " " + bin + " relations found");
		}
	}

	public void checkInAOutRelations(String type1, String type2, String type3) {
		boolean rightType = true;
		for (OutcomeRelation outRel : influencingOutcomes) {
			if (outRel.getType().equals(type1)) {
				for (OutcomeRelation outRelComp : influencingOutcomes) {
					// find relation for reverse case
					if (outRel.getSource() == outRelComp.getTarget()
							&& outRel.getTarget() == outRelComp.getSource()) {
						if (outRelComp.getType().equals(type2)
								|| outRelComp.getType().equals(type3)) {
							System.out.println("correct");
							break;
						} else {
							rightType = false;
							break;
						}
					}
				}
			}
		}
		if (rightType == false) {
			System.out.println("error");
		}
	}

	public void checkXOROutcomes() {
		for (DecisionPoint decisionPoint : decisionPoints) {
			for (Decision decision : decisionPoint.getDecisions()) {
				for (Outcome outcome : decision.getOutcomes()) {
					for (OutcomeRelation outRel : influencingOutcomes) {
						if (outcome.getId() == outRel.getSource()) {
							if (outcome.getId() == outRel.getTarget()) {
								System.out
										.println("self referencing outcomerelations error");
								break;
							} else if (outcome.getParent() == getOutcome(
									outRel.getTarget()).getParent()) {
								System.out
										.println("relation between outcomes withing decision error");
								break;
							}
						}
					}
				}
			}
		}
	}

	public void checkSingleOutcomeRel() {
		for (DecisionPoint decisionPoint : decisionPoints) {
			for (Decision decision : decisionPoint.getDecisions()) {
				for (Outcome outcome : decision.getOutcomes()) {
					ArrayList<Integer> targets = new ArrayList<Integer>();
					for (OutcomeRelation outRel : influencingOutcomes) {
						if (outcome.getId() == outRel.getSource()) {
							targets.add(outRel.getTarget());
						}
					}

					Set<Integer> uniqueTargets = new HashSet<Integer>();
					for (Integer target : targets) {
						// if adding returns false value is already in set thus
						// a target has been in the target list of the outcome.
						if (uniqueTargets.add(target) == false) {
							System.out.println("error");
						}
					}
				}
			}
		}
	}
}
