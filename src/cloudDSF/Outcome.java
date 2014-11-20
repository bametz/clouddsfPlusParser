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
public class Outcome {
	private int id;
	private final String type = "outcome";
	private int parent;
	private double weight;
	private String label;

	public Outcome(String label, int id, int parent) {
		this.label = label;
		this.setId(id);
		this.setParent(parent);
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public double getWeight() {
		return weight;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
