package util;

import java.util.Comparator;

import cloudDSF.CloudDSFEntity;

public class CloudDSFEntityComparator implements Comparator<CloudDSFEntity> {

	@Override
	public int compare(CloudDSFEntity c1, CloudDSFEntity c2) {

		int i = c1.getId() - c2.getId();
		if (i < 0)
			return -1;
		if (i > 0)
			return 1;
		else
			return 0;
	}
}
