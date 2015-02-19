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

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents object with all tasks of the cloudDSF.
 * 
 * @author Metz
 *
 */
public class TaskTree extends CloudDSFEntity {
  /**
   * List with all tasks.
   */
  private List<Task> tasks = new ArrayList<Task>();

  /**
   * Constructor for cloudDSf Task Tree.
   */
  public TaskTree() {
    super(9, "root", "Tasks");
  }

  @JsonProperty("children")
  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }
}
