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

/**
 * Represents an outcome of the cloudDSF(Plus).
 * 
 * @author Metz
 *
 */

public class Outcome extends CloudDSFEntity {
  /**
   * Outcome constructor for the cloudDSFPlus.
   * 
   * @param label
   * @param id
   * @param cluster
   * @param parent
   * @param description
   * @param additionalInfo
   * @param abbrev
   */
  public Outcome(String label, int id, int cluster, int parent, String description,
      String additionalInfo, String abbrev) {
    super(id, "out", label);
    this.setDescription(description);
    this.setCluster(cluster);
    this.setGroup("out" + cluster);
    this.setParent(parent);
    this.setAdditionalInfo(additionalInfo);
    this.setAbbrev(abbrev);
  }

  /**
   * Outcome constructor for the cloudDSF.
   * 
   * @param label
   * @param id
   * @param parent
   */
  public Outcome(String label, int id, int parent) {
    super(id, "outcome", label);
    this.setParent(parent);
  }
}
