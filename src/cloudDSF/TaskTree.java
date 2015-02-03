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
