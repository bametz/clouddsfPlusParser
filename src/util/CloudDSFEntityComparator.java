package util;

import cloudDSF.CloudDSFEntity;

import java.util.Comparator;

/**
 * Comparator to sort CloudDSFEntities by id.
 * 
 * @author Metz
 *
 */
public class CloudDSFEntityComparator implements Comparator<CloudDSFEntity> {

  @Override
  public int compare(CloudDSFEntity c1, CloudDSFEntity c2) {

    int diff = c1.getId() - c2.getId();
    if (diff < 0) {
      return -1;
    }
    if (diff > 0) {
      return 1;
    } else {
      return 0;
    }
  }
}
