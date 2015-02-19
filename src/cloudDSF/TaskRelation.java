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
 * Represents a relation between decision and task for the cloudDSF.
 * 
 * @author Metz
 *
 */
public class TaskRelation extends Relation {

  /**
   * Default Constructor for task relation.
   * 
   * @param source
   * @param target
   * @param dir
   */
  public TaskRelation(int source, int target, String dir) {
    super(source, target, "taskRel");
    this.setDir(dir);
  }
}
