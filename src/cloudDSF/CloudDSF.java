package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Metz Main class to represent the CloudDSF object with dps, ds and os.
 */

public class CloudDSF {
	private int id;
	private String type;
	private String label;

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
		// int source = 0;
		// int target = 0;
		// for (DecisionPoint dp : decisionPoints) {
		//
		// for (Decision d : dp.getDecisions()) {
		// if (d.getLabel().equals(startDecision)) {
		// source = d.getId();
		// }
		// if (d.getLabel().equals(endDecision)) {
		// target = d.getId();
		// }
		// if (source != 0 && target != 0) {
		// break;
		// }
		// }
		// }
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
		Collections.sort(decisionPoints, new Comparator<DecisionPoint>() {
			@Override
			public int compare(DecisionPoint dp1, DecisionPoint dp2) {
				int i = dp1.getId() - dp2.getId();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});
		Collections.sort(influencingDecisions, new Comparator<Relation>() {
			@Override
			public int compare(Relation dr1, Relation dr2) {
				int i = dr1.getSource() - dr2.getSource();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}

		});
		Collections.sort(influencingOutcomes,
				new Comparator<OutcomeRelation>() {
					@Override
					public int compare(OutcomeRelation or1, OutcomeRelation or2) {
						int i = or1.getSource() - or2.getSource();
						if (i < 0)
							return -1;
						if (i > 0)
							return 1;
						else
							return 0;
					}
				});
		Collections.sort(influencingTasks, new Comparator<TaskRelation>() {
			@Override
			public int compare(TaskRelation tr1, TaskRelation tr2) {
				int i = tr1.getSource() - tr2.getSource();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});

		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				int i = t1.getId() - t2.getId();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});
	}

	public void sortInfluencingRelations() {
		Collections.sort(influencingRelations, new Comparator<Relation>() {
			@Override
			public int compare(Relation r1, Relation r2) {
				int i = r1.getSource() - r2.getSource();
				if (i < 0)
					return -1;
				if (i > 0)
					return 1;
				else
					return 0;
			}
		});
	}
}
