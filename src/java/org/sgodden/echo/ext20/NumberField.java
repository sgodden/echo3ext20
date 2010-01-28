package org.sgodden.echo.ext20;

/**
 * A number field
 * 
 * @author icumberland
 *
 */
@SuppressWarnings( { "serial" })
public class NumberField extends TextField {
	
    public static final String DECIMAL_PRECISION = "decimalPrecision";
	
    public static final String MIN_VALUE = "minValue";
	
    public static final String MIN_TEXT = "minText";
	
    public static final String MAX_VALUE = "maxValue";
	
    public static final String MAX_TEXT = "maxText";
	
	public NumberField() {
		super();
	}
	
	public NumberField(String text) {
		super(text);
	}
	
	public NumberField(String text, String fieldLabel) {
		super(text, fieldLabel);
	}
	
    /**
     * Sets the maximum precision to display after the decimal separator (defaults to 2)
     * 
     * @param decimalPrecision
     *            the maximum precision to display after the decimal separator.
     */
    public void setDecimalPrecision(int decimalPrecision) {
        set(DECIMAL_PRECISION, decimalPrecision);
    }
	
    /**
     * Sets the minimum value that is valid for this number
     * 
     * @param minValue the minimum value that is valid for this number.
     */
    public void setMinValue(int minValue) {
        set(MIN_VALUE, minValue);
    }
	
    /**
     * Sets the error text to be displayed if the minimum value validation fails
     * 
     * @param minText the error text to be displayed if the minimum value validation fails.
     */
    public void setMinText(String minText) {
        set(MIN_TEXT, minText);
    }
	
    /**
     * Sets the maximum value that is valid for this number
     * 
     * @param maxValue the maximum value that is valid for this number.
     */
    public void setMaxValue(int maxValue) {
        set(MAX_VALUE, maxValue);
    }
	
    /**
     * Sets the error text to be displayed if the maximum value validation fails
     * 
     * @param maxText the error text to be displayed if the maximum value validation fails.
     */
    public void setMaxText(String maxText) {
        set(MAX_TEXT, maxText);
    }
}
