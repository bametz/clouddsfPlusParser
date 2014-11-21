package cloudDSF;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Represents one outcome of the cloudDSF
 * 
 * @author Metz
 *
 */
public class Outcome extends CloudDSFEntity {

	private double weight;

	public Outcome(String label, int id, int parent) {
		this.setLabel(label);
		this.setId(id);
		this.setParent(parent);
		this.setType("outcome");
	}

	/**
	 * formats the calculated weight to a two digit number separated with .
	 * 
	 * @param weight
	 */
	public void setWeight(double weight) {
		Locale locale = new Locale("en", "UK");
		String pattern = "#.###";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
				.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		this.weight = Double.valueOf(decimalFormat.format(weight));
	}

	public double getWeight() {
		return weight;
	}
}
