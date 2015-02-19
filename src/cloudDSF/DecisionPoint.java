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
 * Represents a decision point of the cloudDSF(Plus).
 * 
 * @author Metz
 *
 */
public class DecisionPoint extends CloudDSFEntity {

  /**
   * List with all decisions of the decision point.
   */
  private List<Decision> decisions = new ArrayList<Decision>();

  /**
   * Decision point constructor for the cloudDSFPlus.
   * 
   * @param label
   * @param id
   * @param cluster
   * @param classification
   * @param description
   * @param additionalInfo
   * @param abbrev
   */
  public DecisionPoint(String label, int id, int cluster, String classification,
      String description, String additionalInfo, String abbrev) {
    super(id, "dp", label);
    this.setClassification(classification);
    this.setDescription(description);
    this.setCluster(cluster);
    this.setGroup("dp" + cluster);
    this.setAdditionalInfo(additionalInfo);
    this.setAbbrev(abbrev);
    // parent stays null
  }

  /**
   * Decision point constructor for the cloudDSF.
   * 
   * @param label
   * @param id
   * @param classification
   */
  public DecisionPoint(String label, int id, String classification) {
    super(id, "decisionPoint", label);
    this.setClassification(classification);
    // parent stays null
  }

  /**
   * Sorts decisions by ascending id for better readability in json.
   */
  public void sortDecisions() {
    Collections.sort(decisions, new CloudDSFEntityComparator());
  }

  @JsonProperty("children")
  public List<Decision> getDecisions() {
    return decisions;
  }

  public void addDecision(Decision decision) {
    this.decisions.add(decision);
  }

  /**
   * Gets decision via name.
   * 
   * @param name
   * @return
   */
  public Decision getDecision(String name) {
    for (Decision decision : decisions) {
      if (decision.getLabel().equals(name)) {
        return decision;
      }
    }
    return null;
  }

  /**
   * Gets decision via Id.
   * 
   * @param decisionId
   * @return
   */
  public Decision getDecision(int decisionId) {
    for (Decision decision : decisions) {
      if (decision.getId() == decisionId) {
        return decision;
      }
    }
    return null;
  }
}
