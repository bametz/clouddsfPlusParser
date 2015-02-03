package cloudDSF;

import com.fasterxml.jackson.annotation.JsonProperty;

import util.CloudDSFEntityComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a decision of the cloudDSF(Plus).
 * 
 * @author Metz
 *
 */
public class Decision extends CloudDSFEntity {

  private List<Outcome> outcomes = new ArrayList<Outcome>();

  /**
   * Decision constructor for the cloudDSFPlus.
   * 
   * @param label
   * @param id
   * @param cluster
   * @param parent
   * @param classification
   * @param description
   * @param additionalInfo
   * @param abbrev
   */
  public Decision(String label, int id, int cluster, int parent, String classification,
      String description, String additionalInfo, String abbrev) {
    super(id, "dec", label);
    this.setClassification(classification);
    this.setDescription(description);
    this.setCluster(cluster);
    this.setGroup("dec" + cluster);
    this.setParent(parent);
    this.setAdditionalInfo(additionalInfo);
    this.setAbbrev(abbrev);

  }

  /**
   * Decision constructor for the cloudDSF.
   * 
   * @param label
   * @param classification
   * @param id
   * @param parent
   */
  public Decision(String label, String classification, int id, int parent) {
    super(id, "decision", label);
    this.setClassification(classification);
    this.setParent(parent);
  }

  /**
   * Sorts outcomes by ascending id for better readability in json.
   */
  public void sortOutcomes() {
    Collections.sort(outcomes, new CloudDSFEntityComparator());
  }

  public void addOutcome(Outcome outcome) {
    outcomes.add(outcome);
  }

  @JsonProperty("children")
  public List<Outcome> getOutcomes() {
    return outcomes;
  }
  /**
   * Get outcome via label.
   * @param label
   * @return
   */
  public Outcome getOutcome(String label) {
    for (Outcome outcome : outcomes) {
      if (outcome.getLabel().equals(label)) {
        return outcome;
      }
    }
    return null;
  }
/**
 * Gets outcome via id.
 * @param outcomeId
 * @return
 */
  public Outcome getOutcome(int outcomeId) {
    for (Outcome outcome : outcomes) {
      if (outcome.getId() == outcomeId) {
        return outcome;
      }
    }
    return null;
  }
}
