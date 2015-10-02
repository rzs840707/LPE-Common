/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.lpe.common.utils.numeric.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract class of outlier detectors.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public abstract class AbstractOutlierDetector {

	/**
	 * Filters outliers from the given set of values.
	 * 
	 * @param values
	 *            a list of numeric values
	 * @return a filtered set of the input values without the outliers
	 */
	public abstract List<Double> filterOutliers(double[] values);

	/**
	 * Filters outliers from the given set of values.
	 * 
	 * @param values
	 *            a list of numeric values
	 * @return a filtered set of the input values without the outliers
	 * 
	 * @see #filterOutliers(double[])
	 * 
	 */
	public <T extends Number> List<T> filterOutliers(List<T> values) {
		double[] dValues = new double[values.size()];
		String type = "";
		int i = 0;
		for (T value : values) {
			if (i == 0) {
				if (value instanceof Integer) {
					type = "Integer";
				} else if (value instanceof Long) {
					type = "Long";
				} else if (value instanceof Double) {
					type = "Double";
				} else if (value instanceof Float) {
					type = "Float";
				}
			}
			dValues[i] = value.doubleValue();
			i++;
		}

		List<Double> resultDoubleList = filterOutliers(dValues);
		List<T> resultList = new ArrayList<>();
		switch (type) {
		case "Integer":
			for (Double d : resultDoubleList) {
				resultList.add((T) new Integer(d.intValue()));
			}
			break;
		case "Long":
			for (Double d : resultDoubleList) {
				resultList.add((T) new Long(d.longValue()));
			}
			break;
		case "Double":
			return (List<T>) resultDoubleList;
		case "Float":
			for (Double d : resultDoubleList) {
				resultList.add((T) new Float(d.floatValue()));
			}
			break;
		default:
			break;
		}

		return resultList;

	}

	/**
	 * Marks the outliers in the given list of values.
	 * 
	 * @param values
	 *            a list of numeric data
	 * @return a map from values to a Boolean flag which indicates if a value is
	 *         an outlier (<code>true</code>) or not (<code>false</code>).
	 */
	public Map<Double, Boolean> markOutliersUsingIQR(double[] values) {

		List<Double> filtered = filterOutliers(values);

		Map<Double, Boolean> resultMap = new HashMap<Double, Boolean>();
		// first mark all as outliers
		for (double value : values) {
			resultMap.put(value, true);
		}
		// unmark the surviving ones as non-outlier
		for (Double nonOutlier : filtered) {
			resultMap.put(nonOutlier, false);
		}

		return resultMap;
	}

	/**
	 * Marks the outliers in the given list of values.
	 * 
	 * @param values
	 *            a list of numeric data
	 * @return a map from values to a Boolean flag which indicates if a value is
	 *         an outlier (<code>true</code>) or not (<code>false</code>).
	 * 
	 * @see #markOutliersUsingIQR(double[])
	 * 
	 */
	public Map<Double, Boolean> markOutliersUsingIQR(List<Double> values) {
		return markOutliersUsingIQR(convertListToArray(values));
	}

	/**
	 * Converts a list of values to an array.
	 * 
	 * @param values
	 *            list of values
	 * @return an array of values
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the values are null.
	 */
	protected double[] convertListToArray(List<Double> values) {
		double[] dValues = new double[values.size()];
		int i = 0;
		for (Double value : values) {
			if (value == null) {
				throw new IllegalArgumentException("Values in the array cannot be null.");
			} else {
				dValues[i] = value;
			}
			i++;
		}
		return dValues;
	}
}
