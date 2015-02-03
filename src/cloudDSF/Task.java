package cloudDSF;

/**
 * Represents a task of the cloudDSF.
 * 
 * @author Metz
 *
 */
public class Task extends CloudDSFEntity {

  public Task(int id, String label) {
    super(id, "task", label);
  }
}
