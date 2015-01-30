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
	/**
	 * Contains all decision points of the clouddsf
	 */
	private List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();
	/**
	 * contains all relations between decisions (i.e. requiring, influencing,
	 * binding, affecting)
	 */
	private List<DecisionRelation> influencingDecisions = new ArrayList<DecisionRelation>();
	/**
	 * contain all relations between decisions (i.e. aff, eb, a, ex, in)
	 */
	private List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();
	/**
	 * contains all relations between tasks and decisions, null for cloudDSFPlus
	 */
	private List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();
	/**
	 * contains all tasks of the cloudDSF null in case of the cloudDSFPlus
	 */
	private List<Task> tasks = new ArrayList<Task>();
	/**
	 * Contains all influecing tasks and influencing decisions to enable link
	 * array for the cloudDSFVisualizations
	 */
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
	 * Sort of all relations to produce sorted output in id ascending order of
	 * the source
	 */
	public void sortLists() {
		RelationComparator rc = new RelationComparator();
		Collections.sort(influencingDecisions, rc);
		Collections.sort(influencingOutcomes, rc);
		Collections.sort(influencingTasks, rc);

	}

	/**
	 * Sort of all entities of the CloudDSF to produce sorted output depending
	 * on id in ascending order
	 */
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

	/**
	 * Executes all verification methods and returns the result
	 * 
	 * @return
	 */
	public boolean checkSanity() {
		return (checkAffBinDecRelations("affecting", "binding")
				&& checkAffBinDecRelations("binding", "affecting")
				&& checkAffBinOutRelations("aff", "eb")
				&& checkAffBinOutRelations("eb", "aff") && checkDecRelComb()
				&& checkInAOutRelations("in", "a", "in")
				&& checkInAOutRelations("a", "a", "in")
				&& checkOutRelAmountForDecRel() && checkOutRelTypeForDecRel()
				&& checkRelTypesDecisions() && checkRelTypesOutcomes()
				&& checkSingleOutcomeRel() && checkXOROutcomes());
	}

	/**
	 * Checks if only valid decision relationship types are present
	 * 
	 * @return
	 */
	public boolean checkRelTypesDecisions() {
		for (DecisionRelation decRel : influencingDecisions) {
			switch (decRel.getType()) {
			case "influencing":
				break;
			case "requiring":
				break;
			case "affecting":
				break;
			case "binding":
				break;
			default:
				System.out.println("Fail: Wrong decision relation type "
						+ decRel.getType() + " found from "
						+ decRel.getSource() + " to " + decRel.getTarget());
				return false;
			}
		}
		System.out
				.println("Success: All decision relationship types are valid");
		return true;
	}

	/**
	 * Checks if only valid outcome relationship types are present
	 * 
	 * @return
	 */
	public boolean checkRelTypesOutcomes() {
		for (OutcomeRelation outRel : influencingOutcomes) {
			switch (outRel.getType()) {
			case "eb":
				break;
			case "in":
				break;
			case "a":
				break;
			case "ex":
				break;
			case "aff":
				break;
			default:
				System.out.println("Fail: Wrong outcome relation type "
						+ outRel.getType() + " found from "
						+ outRel.getSource() + " to " + outRel.getTarget());
				return false;
			}
		}
		System.out.println("Success: All outcome relationship types are valid");
		return true;
	}

	/**
	 * Checks if two or more relations between the same decisions exists and if
	 * so if they are in the correct combinations
	 * 
	 * @return
	 */
	public boolean checkDecRelComb() {
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
						// System.out.println("same decision relations");
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
							System.out
									.println("Fail: Wrong decision relation combination between "
											+ decRel.getSource()
											+ " to "
											+ decRel.getTarget()
											+ " with relation type "
											+ relType
											+ " and " + relTypeComp);
							return false;
						}
					}
				} else {
					// relations is not between same decisions
				}
			}
		}
		System.out
				.println("Success: All decision relations are only existing in the only valid combination (requiring + any other)");
		return true;
	}

	/**
	 * Checks if the correct relationship types between outcomes are present
	 * according to the decision relationship type
	 * 
	 * @return
	 */
	public boolean checkOutRelTypeForDecRel() {
		for (DecisionRelation decRel : influencingDecisions) {
			if (decRel.getType().equals("requiring") == false) {
				// set source and target outcome to check
				Decision sourceDecision = getDecision(decRel.getSource());
				Decision targetDecision = getDecision(decRel.getTarget());
				// traverse starting outcomes
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
								if (decRel.getType().equals("affecting")) {
									if (outRel.getType().equals("aff") == false) {
										System.out
												.println("Fail: An outcome relation ("
														+ outRel.getType()
														+ ") does not match with its affecting decision relation");
										return false;
									}
								} else if (decRel.getType().equals("binding")) {
									if (outRel.getType().equals("eb") == false) {
										System.out
												.println("Fail: An outcome relation ("
														+ outRel.getType()
														+ ") does not match with its binding decision relation");
										return false;
									}
								} else if (decRel.getType().equals(
										"influencing")) {
									if (outRel.getType().equals("aff")
											|| outRel.getType().equals("eb")) {
										System.out
												.println("Fail: An outcome relation ("
														+ outRel.getType()
														+ ") does not match with its influecing decision relation");
										return false;
									}
								}
							}
						}
					}
				}
			}
		}
		System.out
				.println("Success: All outcome relations are valid corresponding to their decision relation");
		return true;
	}

	/**
	 * check if the exact amount of relations are present for a decision
	 * relation
	 * 
	 * @return
	 */
	public boolean checkOutRelAmountForDecRel() {
		for (DecisionRelation decRel : influencingDecisions) {
			if (decRel.getType().equals("requiring") == false) {
				// set source and target outcome to check
				Decision sourceDecision = getDecision(decRel.getSource());
				Decision targetDecision = getDecision(decRel.getTarget());
				// traverse starting outcomes
				int foundRelations = sourceDecision.getOutcomes().size()
						* targetDecision.getOutcomes().size();
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
							}
						}
					}
				}
				if (foundRelations != 0) {
					System.out.println("Fails: There are " + foundRelations
							+ " missing outcome relations");
					return false;
				}
			}
		}
		System.out
				.println("Success: There are the exactly necessary amount of relations between outcomes per decision relation");
		return true;
	}

	/**
	 * check if affecting rel exists into one direction that a binding exists
	 * into the other way around and vice versa for decisions
	 * 
	 * @param type1
	 *            relationship type to check against (e.g. aff)
	 * @param type2
	 *            second relationship type (e.g. eb)
	 * @return
	 */
	public boolean checkAffBinDecRelations(String type1, String type2) {
		int aff = 0;
		int bin = 0;
		for (DecisionRelation decRel : influencingDecisions) {
			// filter affecting relations only
			if (decRel.getType().equals(type1)) {
				aff++;
				for (DecisionRelation decRelComp : influencingDecisions) {
					// find relation for reverse case
					if (decRel.getSource() == decRelComp.getTarget()
							&& decRel.getTarget() == decRelComp.getSource()) {
						if (decRelComp.getType().equals(type2)) {
							bin++;
							break;
						}
					}
				}
			}
		}
		if (aff != bin) {
			System.out.println("Fail: There are not the same amount of "
					+ type1 + " to " + type2 + " decision relations");
			return false;
		}
		System.out.println("Success: There are the same amount of " + type1
				+ " to " + type2 + " decisions relations");
		return true;
	}

	/**
	 * check if affecting rel exists into one direction that a binding exists
	 * into the other way around and vice versa for outcomes
	 * 
	 * @param type1
	 *            relationship type to check against (e.g. aff)
	 * @param type2
	 *            second relationship type (e.g. eb)
	 * @return
	 */
	public boolean checkAffBinOutRelations(String type1, String type2) {
		int aff = 0;
		int bin = 0;
		for (OutcomeRelation outRel : influencingOutcomes) {
			// filter affecting relations only
			if (outRel.getType().equals(type1)) {
				aff++;
				for (OutcomeRelation outRelComp : influencingOutcomes) {
					// find relation for reverse case
					if (outRel.getSource() == outRelComp.getTarget()
							&& outRel.getTarget() == outRelComp.getSource()) {
						if (outRelComp.getType().equals(type2)) {
							bin++;
							break;
						}
					}
				}
			}
		}
		if (aff != bin) {
			System.out.println("Fail: There are not the same amount of "
					+ type1 + " to " + type2 + " outcome relations");
			return false;
		}
		System.out.println("Success: There are the same amount of " + type1
				+ " to " + type2 + " outcome relations");
		return true;
	}

	/**
	 * check if in or a outcome relation from a to b has an in or a relation in
	 * the reverse case as well.
	 * 
	 * @param type1
	 *            first relationship type that others are compared with
	 * @param type2
	 *            relationship type that is allowed with type1
	 * @param type3
	 *            relationship type that is allowed with type1
	 * @return
	 */
	public boolean checkInAOutRelations(String type1, String type2, String type3) {
		for (OutcomeRelation outRel : influencingOutcomes) {
			if (outRel.getType().equals(type1)) {
				for (OutcomeRelation outRelComp : influencingOutcomes) {
					// find relation for reverse case
					if (outRel.getSource() == outRelComp.getTarget()
							&& outRel.getTarget() == outRelComp.getSource()) {
						if (outRelComp.getType().equals(type2)
								|| outRelComp.getType().equals(type3)) {
							break;
						} else {
							System.out
									.println("Fail: There is a conflict between two (vice versa) outcome relationship types");
							return false;
						}
					}
				}
			}
		}
		System.out
				.println("Success: All outcome relations do not have an ex to in/a relation combination in the reverse case");
		return true;
	}

	/**
	 * check if outcomes have a relation to themselves or towards outcome of the
	 * same decision
	 * 
	 * @return
	 */
	public boolean checkXOROutcomes() {
		for (DecisionPoint decisionPoint : decisionPoints) {
			for (Decision decision : decisionPoint.getDecisions()) {
				for (Outcome outcome : decision.getOutcomes()) {
					for (OutcomeRelation outRel : influencingOutcomes) {
						if (outcome.getId() == outRel.getSource()) {
							if (outcome.getId() == outRel.getTarget()) {
								System.out
										.println("Error: Outcome a a relation to itself");
								return false;
							} else if (outcome.getParent() == getOutcome(
									outRel.getTarget()).getParent()) {
								System.out
										.println("Fail: Outcome has relations towards outcome of same decision");
								return false;
							}
						}
					}
				}
			}
		}
		System.out.println("Success: All outcomes satisfy the XOR rule");
		return true;
	}

	/**
	 * Checks if an outcome has multiple relations towards another outcome
	 * 
	 * @return
	 */
	public boolean checkSingleOutcomeRel() {
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
							System.out
									.println("An outcome has several relations towards another outcome");
							return false;
						}
					}
				}
			}
		}
		System.out
				.println("Success: All outcomes have only one relation towards other outcomes");
		return true;
	}
}
