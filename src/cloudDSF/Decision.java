/*
 * Copyright 2015 Balduin Metz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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

  /**
   * List with all outcomes of the decision.
   */
  private List<Outcome> outcomes = new ArrayList<Outcome>();

  /**
   * Decision constructor for the cloudDSFPlus.
   * 
   * @param label name of decision
   * @param id id of decision
   * @param cluster id of decision point
   * @param parent id of decision point
   * @param classification classification of decision
   * @param description meaning of the decision
   * @param additionalInfo additional information for decision (optional)
   * @param abbrev abbreviation of the name of the decision
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
   * @param label name of decision
   * @param id id of decision
   * @param classification classification of decision
   * @param parent id of decision point
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
   * Get outcome via name.
   * 
   * @param label name of outcome
   * @return outcome or null if outcome does not exist
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
   * 
   * @param outcomeId Id of outcome
   * @return outcome or null if outcome does not exist
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
