package util;

import java.util.Comparator;

import cloudDSF.Relation;

/**
 * Comparator to compare Relations by their source
 * 
 * @author Metz
 *
 */
public class RelationComparator implements Comparator<Relation> {

	@Override
	public int compare(Relation r1, Relation r2) {
		int i = r1.getSource() - r2.getSource();
		if (i < 0)
			return -1;
		if (i > 0)
			return 1;
		else
			return 0;
	}
}
