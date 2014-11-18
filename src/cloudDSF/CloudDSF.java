package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Metz Main class to represent the CloudDSF object with dps, ds and os.
 */

public class CloudDSF {
	private int id;
	private String type;
	private String label;

	private transient HashMap<String, DecisionPoint> decisionPoints = new HashMap<String, DecisionPoint>();

	private transient HashMap<String, Task> tasks = new HashMap<String, Task>();

	@SerializedName("children")
	private List<DecisionPoint> decisionPointsSorted = new ArrayList<DecisionPoint>();

	@SerializedName("linksArray")
	private transient List<Relation> influencingRelations = new ArrayList<Relation>();

	private transient List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();

	private transient List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();

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
		int source = 0;
		int target = 0;
		for (DecisionPoint dp : getDecisionPoints().values()) {

			for (Decision d : dp.getDecisions().values()) {
				if (d.getLabel().equals(startDecision)) {
					source = d.getId();
				}
				if (d.getLabel().equals(endDecision)) {
					target = d.getId();
				}
				if (source != 0 && target != 0) {
					break;
				}
			}
		}
		// label is rather type
		DecisionRelation di = new DecisionRelation(source, target, label);
		influencingRelations.add(di);
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
		int source = 0;
		int target = 0;
		for (DecisionPoint dp : getDecisionPoints().values()) {
			for (Decision d : dp.getDecisions().values()) {
				// could be implemented through the hashmap and direct access
				// however, performance shouldnt be an issue
				for (Outcome o : d.getOutcomes().values()) {
					if (o.getLabel().equals(startOutcome)) {
						source = o.getId();
					}
					if (o.getLabel().equals(endOutcome)) {
						target = o.getId();
					}
					if (source != 0 && target != 0) {
						break;
					}
				}
			}
		}
		// label is rather type
		OutcomeRelation or = new OutcomeRelation(source, target, label);
		influencingOutcomes.add(or);
	}

	/**
	 * Sort of all ArrayLists to produce sorted Output in id ascending order
	 */
	public void prepareSortedDPs() {
		Collections.sort(decisionPointsSorted, new Comparator<DecisionPoint>() {
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
		Collections.sort(influencingRelations,
				new Comparator<Relation>() {
					@Override
					public int compare(Relation dr1,
							Relation dr2) {
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
			for (Decision d : dp.getSortedDecisionList()) {
				damount++;
				System.out.println("Decision " + d.getLabel() + " ID "
						+ d.getId() + " parentId " + d.getParent());

				for (Outcome o : d.getOutcomesSorted()) {
					oamount++;
					System.out.println("Outcome " + o.getLabel() + " ID "
							+ o.getId() + " parentId " + o.getParent()
							+ " Weight " + o.getWeight());
				}
			}
		}
		for (Relation relation : influencingRelations) {
			System.out.println(relation.getSource() + " to "
					+ relation.getTarget() + " label " + relation.getLabel());
			relations++;
		}
		for (OutcomeRelation oRelation : influencingOutcomes) {
			System.out.println(oRelation.getSource() + " to "
					+ oRelation.getTarget() + " label " + oRelation.getLabel());
			oRelationsAmount++;
		}
		
		for (Task t : tasks.values()){
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

	public HashMap<String, Task> getTasks() {
		return tasks;
	}

	public void setTasks(HashMap<String, Task> tasks) {
		this.tasks = tasks;
	}

	public List<TaskRelation> getInfluencingTasks() {
		return influencingTasks;
	}

	public void setInfluencingTasks(List<TaskRelation> influencingTasks) {
		this.influencingTasks = influencingTasks;
	}

	public void addDecisionPoint(DecisionPoint dp) {
		decisionPoints.put(dp.getLabel(), dp);
		decisionPointsSorted.add(dp);
	}

	public List<OutcomeRelation> getInfluencingOutcomes() {
		return influencingOutcomes;
	}

	public void setInfluencingOutcomes(List<OutcomeRelation> influencingOutcomes) {
		this.influencingOutcomes = influencingOutcomes;
	}

	public List<Relation> getInfluencingRelations() {
		return influencingRelations;
	}

	public void setInfluencingRelations(
			List<Relation> influencingRelations) {
		this.influencingRelations = influencingRelations;
	}

	public List<DecisionPoint> getDecisionPointsSorted() {
		return decisionPointsSorted;
	}

	public void setDecisionPointsSorted(List<DecisionPoint> decisionPointsSorted) {
		this.decisionPointsSorted = decisionPointsSorted;
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

	public HashMap<String, DecisionPoint> getDecisionPoints() {
		return decisionPoints;
	}

	public void setDecisionPoints(HashMap<String, DecisionPoint> decisionPoints) {
		this.decisionPoints = decisionPoints;
	}

	public void setTaskRelation(String startTask, String targetDec, String dir,
			String label) {
		int source = 0;
		int target = 0;
		System.out.println(startTask);
		System.out.println(targetDec);
		if (tasks.get(startTask) != null) {
			source = tasks.get(startTask).getId();
			for (DecisionPoint dp : getDecisionPoints().values()) {
				for (Decision d : dp.getDecisions().values()) {
					if (d.getLabel().equals(targetDec)) {
						target = d.getId();
						break;
					}
					
				}
			}
		} else {
			target = tasks.get(targetDec).getId();
			for (DecisionPoint dp : getDecisionPoints().values()) {
				for (Decision d : dp.getDecisions().values()) {
					if (d.getLabel().equals(startTask)) {
						source = d.getId();
						break;
					}
				}
			}
		}

		TaskRelation tr = new TaskRelation(source, target, dir, label);
		influencingRelations.add(tr);
		influencingTasks.add(tr);
	}

	public void addTask(Task task) {
		tasks.put(task.getLabel(), task);
	}
}
