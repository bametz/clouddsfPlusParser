package cloudDSF;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class CloudDSF {

	private transient HashMap<String, DecisionPoint> decisionPoints = new HashMap<String, DecisionPoint>();
	private SortedMap<Integer, DecisionPoint> decisionPointsSorted = new TreeMap<Integer, DecisionPoint>();

	public HashMap<String, DecisionPoint> getDecisionPoints() {
		return decisionPoints;
	}

	public void setDecisionPoints(HashMap<String, DecisionPoint> decisionPoints) {
		this.decisionPoints = decisionPoints;
	}

	public void prepareSortedDPs() {
		decisionPointsSorted.clear();
		for (DecisionPoint dp : decisionPoints.values()) {
			decisionPointsSorted.put(dp.getId(), dp);
		}
	}

	public SortedMap<Integer, DecisionPoint> getDecisionPointsSorted() {
		return decisionPointsSorted;
	}

	public void addDecisionPoint(DecisionPoint dp) {
		decisionPoints.put(dp.getLabel(), dp);
	}

	public void printCloudDSF() {
		int dpamount = 0;
		int damount = 0;
		int oamount = 0;
		
		for (DecisionPoint dp : getDecisionPointsSorted().values()) {
			dpamount++;
			System.out.println("Decision Point Name = " + dp.getLabel()
					+ " ID " + dp.getId());

			for (Decision d : dp.getDecisionsSorted().values()) {
				damount++;
				System.out.println("Decision " + d.getLabel() + " ID "
						+ d.getId() + " parentId " + d.getParent());

				for (Outcome o : d.getOutcomesSorted().values()) {
					oamount++;
					System.out.println("Outcome " + o.getLabel() + " ID "
							+ o.getId() + " parentId " + o.getParent()
							+ " Weight " + o.getWeight());
				}
			}
		}
		System.out.println("anzahl dp " + dpamount);
		System.out.println("anzahl d " + damount);
		System.out.println("anzahl o " + oamount);
	}
}
