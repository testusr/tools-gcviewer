package com.smeo.tools.common;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Container for a bunch of attribute values. Ensures uniqueness of every value.
 */
public class AttributeValueSet {
	private final String DELIMITER = "/!/";
	private SortedSet<String> valueSet = new TreeSet<String>();
	private String stringRepresentation = null;

	public AttributeValueSet(String valueA, String valueB) {
		addValue(valueA);
		addValue(valueB);
	}

	public void addValue(String value) {
		stringRepresentation = null;
		valueSet.add(value);
	}

	public Set<String> getValues() {
		return valueSet;
	}

	public boolean containsValue(String value) {
		return (valueSet.contains(value));
	}

	public String toString() {
		if (stringRepresentation == null) {
			StringBuffer stringBuffer = new StringBuffer();
			for (String currValue : valueSet) {
				stringBuffer.append(currValue + DELIMITER);
			}
			stringRepresentation = stringBuffer.toString();
		}
		return stringRepresentation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valueSet == null) ? 0 : valueSet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this.toString().equals(obj.toString()))
			return true;
		return false;
	}
}
