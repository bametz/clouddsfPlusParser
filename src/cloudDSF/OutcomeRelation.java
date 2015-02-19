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
 * Represents relation between two outcomes for the cloudDSFPlus.
 * 
 * @author Metz
 *
 */
public class OutcomeRelation extends Relation {
  /**
   * Constructor for an outcome relation.
   * 
   * @param source id of source outcome
   * @param target id of target outcome
   * @param type relationship type
   * @param explanation additional information why relation exists (optional)
   */
  public OutcomeRelation(int source, int target, String type, String explanation) {
    super(source, target, type);
    this.setRelationGroup("outRel");
    // yet, no explanation needed
    // this.setExplanation(explanation);
  }

  public OutcomeRelation(int source, int target, String type) {
    super(source, target, type);
  }
}
