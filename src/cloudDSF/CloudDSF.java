package cloudDSF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CloudDSF {
	private int id;
	private String type;
	private String label;

	private transient HashMap<String, DecisionPoint> decisionPoints = new HashMap<String, DecisionPoint>();
	@SerializedName("children")
	private List<DecisionPoint> decisionPointsSorted = new ArrayList<DecisionPoint>();

	@SerializedName("linksArray")
	private transient List<DecisionRelation> influencingRelations = new ArrayList<DecisionRelation>();
		
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
				new Comparator<DecisionRelation>() {
					@Override
					public int compare(DecisionRelation dr1,
							DecisionRelation dr2) {
						int i = dr1.getSource() - dr2.getSource();
						if (i < 0)
							return -1;
						if (i > 0)
							return 1;
						else
							return 0;
					}

				});
	}

	public void addDecisionPoint(DecisionPoint dp) {
		decisionPoints.put(dp.getLabel(), dp);
		decisionPointsSorted.add(dp);
	}

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
		DecisionRelation di = new DecisionRelation(source, target, "auto", 1,
				label, "DecRel");
		influencingRelations.add(di);
	}

	public List<DecisionRelation> getInfluencingRelations() {
		return influencingRelations;
	}

	public void setInfluencingRelations(
			List<DecisionRelation> influencingRelations) {
		this.influencingRelations = influencingRelations;
	}

	public void printCloudDSF() {
		int dpamount = 0;
		int damount = 0;
		int oamount = 0;
		int relations = 0;

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
		for (DecisionRelation relation : influencingRelations) {
			System.out.println(relation.getSource() + " to "
					+ relation.getTarget() + " label " + relation.getLabel());
			relations++;
		}

		System.out.println("anzahl dp " + dpamount);
		System.out.println("anzahl d " + damount);
		System.out.println("anzahl o " + oamount);
		System.out.println("anzahl rel " + relations);
	}
}
