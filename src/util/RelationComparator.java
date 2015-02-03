package util;

import cloudDSF.Relation;

import java.util.Comparator;



/**
 * Comparator to compare Relations by their source.
 * 
 * @author Metz
 *
 */
public class RelationComparator implements Comparator<Relation> {

  @Override
  public int compare(Relation r1, Relation r2) {
    int diff = r1.getSource() - r2.getSource();
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
