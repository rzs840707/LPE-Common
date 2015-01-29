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
package org.lpe.common.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.lpe.common.util.stats.IQROutlierDetector;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Utility class for numeric operations.
 * 
 * @author Alexander Wert, Roozbeh Farahbod
 * 
 */
public final class LpeNumericUtils {
	private static final int NUM_ITEMS_IN_SUM = 6;
	private static final int kilo = 1000;
	private static final int SEC_IN_MIN = 60;
	private static final int MIN_IN_H = 60;

	protected static IQROutlierDetector iqrOutlierDetector = null;

	/**
	 * private constructor due to utility class.
	 */
	private LpeNumericUtils() {
	}

	/**
	 * Formats a <code>double</code> value into a <code>String</code> with
	 * <i>d</i> digits after decimal point.
	 * 
	 * @param v
	 *            value to format
	 * @param d
	 *            number of digits
	 * @return formatted String
	 */
	public static String dFormat(double v, int d) {
		String pattern = "0";

		for (int i = 0; i < d; i++) {
			if (i == 0) {
				pattern += ".";
			}

			pattern += "0";
		}

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		DecimalFormat format = new DecimalFormat(pattern, otherSymbols);
		return format.format(v);
	}

	/**
	 * Converts the passed duration in millisecands to a hour, min, sec,
	 * millisec string.
	 * 
	 * @param timeMillis
	 *            duration to convert
	 * @return formatted string
	 */
	public static String formatTimeMillis(long timeMillis) {
		long milliSec = timeMillis % kilo;
		long secAll = timeMillis / kilo;

		long sec = secAll % SEC_IN_MIN;
		long minAll = secAll / SEC_IN_MIN;

		long min = minAll % MIN_IN_H;
		long h = minAll / MIN_IN_H;

		return h + " h - " + min + " min - " + sec + " s - " + milliSec + " ms";
	}

	/**
	 * Returns the average (mean) of the given values.
	 * 
	 * @param values
	 *            a collection of values
	 * @return the average as a double value
	 */
	public static double average(Collection<? extends Number> values) {
		double result = 0;

		if (values == null) {
			throw new IllegalArgumentException("Cannot calculate average on a null object.");
		}

		if (values.size() == 0) {
			return 0;
		} else {
			for (Number n : values) {
				result += n.doubleValue();
			}
		}

		return result / values.size();
	}

	/**
	 * Returns the maximum of the given values.
	 * 
	 * @param values
	 *            a collection of values
	 * @param <T>
	 *            type of values
	 * @return the maximum
	 */
	public static <T extends Number> T max(Collection<T> values) {
		T max = null;

		for (T value : values) {
			if (max == null) {
				max = value;
				continue;
			}
			@SuppressWarnings("unchecked")
			Comparable<T> comparable = (Comparable<T>) value;
			if (comparable.compareTo(max) > 0) {
				max = value;
			}
		}

		return max;
	}

	/**
	 * Returns the minimum of the given values.
	 * 
	 * @param values
	 *            a collection of values
	 * @param <T>
	 *            type of values
	 * @return the minimum
	 */
	public static <T extends Number> T min(Collection<T> values) {
		T min = null;

		for (T value : values) {
			if (min == null) {
				min = value;
				continue;
			}
			@SuppressWarnings("unchecked")
			Comparable<T> comparable = (Comparable<T>) value;
			if (comparable.compareTo(min) < 0) {
				min = value;
			}
		}

		return min;
	}

	/**
	 * Returns the average (mean) of the given values.
	 * 
	 * @param values
	 *            an array of values
	 * @return the average as a double value
	 */
	public static double average(double[] values) {
		double result = 0;

		if (values == null) {
			throw new IllegalArgumentException("Cannot calculate average on a null object.");
		}

		if (values.length == 0) {
			return 0;
		} else {
			for (Number n : values) {
				result += n.doubleValue();
			}
		}

		return result / values.length;

	}

	/**
	 * Returns the population standard deviation of the given values.
	 * 
	 * @param values
	 *            input values
	 * @return the population standard deviation
	 */
	public static double stdDev(double[] values) {
		double result = 0;

		if (values == null) {
			throw new IllegalArgumentException("Cannot calculate standard deviation on a null object.");
		}

		if (values.length < 1) {
			throw new IllegalArgumentException("Cannot calculated standard deviation on an empty set.");
		}

		final double mean = average(values);

		for (double v : values) {
			result += (v - mean) * (v - mean);
		}

		result = Math.sqrt(result / values.length);

		return result;
	}

	/**
	 * Returns the population standard deviation of the given values.
	 * 
	 * @param array
	 *            input values
	 * @param <T>
	 *            number type
	 * @return the population standard deviation
	 * 
	 */
	public static <T extends Number> double stdDev(T[] array) {
		double[] temp = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			temp[i] = array[i].doubleValue();
		}
		return stdDev(temp);
	}

	/**
	 * Returns the population standard deviation of the given values.
	 * 
	 * @param values
	 *            input values
	 * @param <T>
	 *            number type
	 * @return the population standard deviation
	 */
	public static <T extends Number> double stdDev(Collection<T> values) {
		return stdDev(values.toArray(new Number[] {}));
	}

	/**
	 * Calculates confidence interval width for the given SummaryStatistics and
	 * the significance level.
	 * 
	 * @param summaryStatistics
	 *            the data
	 * @param significance
	 *            desired significance level
	 * @return the width of the confidence interval around the mean with the
	 *         given significance level
	 */
	public static double getConfidenceIntervalWidth(SummaryStatistics summaryStatistics, double significance) {
		return getConfidenceIntervalWidth(summaryStatistics.getN(), summaryStatistics.getStandardDeviation(),
				significance);
	}

	/**
	 * Calculates confidence interval width for the given data and the
	 * significance level.
	 * 
	 * @param sampleSize
	 *            number of values
	 * @param stdDev
	 *            standard deviation of the values
	 * @param significance
	 *            desired significance level
	 * @return the width of the confidence interval around the mean with the
	 *         given significance level
	 */
	public static double getConfidenceIntervalWidth(long sampleSize, double stdDev, double significance) {
		TDistribution tDist = new TDistribution(sampleSize - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * stdDev / Math.sqrt(sampleSize) * 2;
	}

	/**
	 * Filters outliers from the given set of values using the 1.5*IQR method.
	 * 
	 * @param values
	 *            a list of numeric values
	 * @return a filtered set of the input values without the outliers
	 * 
	 * @see IQROutlierDetector#filterOutliers(List)
	 */
	public static <T extends Number> List<T> filterOutliersUsingIQR(List<T> values) {
		return getDefaultIQROutlierDetector().filterOutliers(values);
	}

	/**
	 * Filters outliers from the keys of the given set of values using the
	 * 1.5*IQR method.
	 * 
	 * @param list
	 *            a list of numeric pairs values
	 * @param <T>
	 *            type
	 * @return a filtered set of the input pairs without the outliers
	 * 
	 * 
	 * @see IQROutlierDetector#filterOutliers(List)
	 */
	public static <T extends Number> NumericPairList<Double, T> filterOutliersInKeysUsingIQR(
			NumericPairList<Double, T> list) {
		return getDefaultIQROutlierDetector().filterOutliersInKeys(list);
	}

	/**
	 * Filters outliers from the values of the given set of values using the
	 * 1.5*IQR method.
	 * 
	 * @param list
	 *            a list of numeric pairs values
	 * @param <T>
	 *            type
	 * @return a filtered set of the input pairs without the outliers
	 * 
	 * @see IQROutlierDetector#filterOutliers(List)
	 */
	public static <T extends Number> NumericPairList<T, Double> filterOutliersInValuesUsingIQR(
			NumericPairList<T, Double> list) {
		return getDefaultIQROutlierDetector().filterOutliersInValues(list);
	}

	/**
	 * Remove outliers in keys by calculating the distance to the neighbor
	 * points. If the distance of a point to its neighbors is quite high, the
	 * point is considered as an outlier.
	 * 
	 * @param list
	 *            list of pairs
	 * @param noiseThreshold
	 *            noise threshold
	 * @param windowSize
	 *            window size to include neigbors for consideration
	 * @param <T>
	 *            value type
	 * @return filtered list of pairs
	 */
	public static <T extends Number> NumericPairList<Double, T> removeNoiseInKeys(NumericPairList<Double, T> list,
			double noiseThreshold, int windowSize) {
		NumericPairList<Double, T> result = new NumericPairList<>();

		double[] noiseMetrics = new double[list.size()];
		double maxNoise = Double.MIN_VALUE;
		for (int i = 0; i < list.size(); i++) {
			double sum = 0;
			double count = 0;
			for (int j = i - windowSize / 2; j <= i + windowSize / 2; j++) {
				if (j < 0 || j >= list.size() || i == j) {
					continue;
				}
				sum += Math.abs(list.get(i).getKey() - list.get(j).getKey());
				count += 1.0;
			}

			noiseMetrics[i] = sum / count;
			if (noiseMetrics[i] > maxNoise) {
				maxNoise = noiseMetrics[i];
			}
		}

		for (int i = 0; i < noiseMetrics.length; i++) {
			double relativeNoise = noiseMetrics[i] / maxNoise;
			if (relativeNoise < noiseThreshold) {
				result.add(new NumericPair<Double, T>(list.get(i).getKey(), list.get(i).getValue()));
			}
		}

		return result;
	}

	/**
	 * Remove outliers in values by calculating the distance to the neighbor
	 * points. If the distance of a point to its neighbors is quite high, the
	 * point is considered as an outlier.
	 * 
	 * @param list
	 *            list of pairs
	 * @param noiseThreshold
	 *            noise threshold
	 * @param percentile
	 *            if noise threshold is not used, percentile of the noise is
	 *            used as threshold
	 * @param windowSize
	 *            window size to include neighbors for consideration
	 * 
	 * @param <T>
	 *            value type
	 * @return filtered list of pairs
	 */
	public static <T extends Number> NumericPairList<T, Double> removeNoiseInValues(NumericPairList<T, Double> list,
			double noiseThreshold, double percentile, int windowSize) {
		NumericPairList<T, Double> result = new NumericPairList<>();
		if (noiseThreshold > 0) {
			double[] noiseMetrics = new double[list.size()];
			double maxNoise = Double.MIN_VALUE;
			for (int i = 0; i < list.size(); i++) {
				double sum = 0;
				double count = 0;
				for (int j = i - windowSize / 2; j <= i + windowSize / 2; j++) {
					if (j < 0 || j >= list.size() || i == j) {
						continue;
					}
					sum += Math.abs(list.get(i).getValue() - list.get(j).getValue());
					count += 1.0;
				}

				noiseMetrics[i] = sum / count;
				if (noiseMetrics[i] > maxNoise) {
					maxNoise = noiseMetrics[i];
				}
			}

			for (int i = 0; i < noiseMetrics.length; i++) {
				double relativeNoise = noiseMetrics[i] / maxNoise;
				if (relativeNoise < noiseThreshold) {
					result.add(new NumericPair<T, Double>(list.get(i).getKey(), list.get(i).getValue()));
				}
			}
		} else {
			double[] noiseMetrics = new double[list.size()];
			double maxNoise = Double.MIN_VALUE;
			for (int i = 0; i < list.size(); i++) {
				double sum = 0;
				double count = 0;
				for (int j = i - windowSize / 2; j <= i + windowSize / 2; j++) {
					if (j < 0 || j >= list.size() || i == j) {
						continue;
					}
					sum += Math.abs(list.get(i).getValue() - list.get(j).getValue());
					count += 1.0;
				}

				noiseMetrics[i] = sum / count;
				if (noiseMetrics[i] > maxNoise) {
					maxNoise = noiseMetrics[i];
				}
			}

			List<Double> noisemetricsList = new ArrayList<>();
			for (int i = 0; i < noiseMetrics.length; i++) {
				noiseMetrics[i] = noiseMetrics[i] / maxNoise;
				noisemetricsList.add(noiseMetrics[i]);
			}

			Collections.sort(noisemetricsList);

			int percentileIx = (int) (((double) noisemetricsList.size()) * percentile);
			noiseThreshold = noisemetricsList.get(percentileIx);

			for (int i = 0; i < noiseMetrics.length; i++) {
				if (noiseMetrics[i] < noiseThreshold) {
					result.add(new NumericPair<T, Double>(list.get(i).getKey(), list.get(i).getValue()));
				}
			}
		}

		return result;
	}

	/**
	 * Exports a pair list as CSV.
	 * 
	 * @param list
	 *            pair list to export
	 * @param file
	 *            target CSV file
	 * @param keyColumnName
	 *            name of the key column
	 * @param valueColumnName
	 *            name of the value column
	 */
	public static void exportAsCSV(NumericPairList<? extends Number, ? extends Number> list, String file,
			String keyColumnName, String valueColumnName) {
		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(file);
			csvWriter = new CSVWriter(fWriter, ';');
			String[] line = { keyColumnName, valueColumnName };
			csvWriter.writeNext(line);
			for (NumericPair<? extends Number, ? extends Number> pair : list) {
				line[0] = pair.getKey().toString();
				line[1] = pair.getValue().toString();
				csvWriter.writeNext(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {

			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			if (fWriter != null) {
				try {
					fWriter.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * Marks the outliers in the given list of values using the 1.5*IQR method.
	 * 
	 * @param values
	 *            a list of numeric data
	 * @return a map from values to a Boolean flag which indicates if a value is
	 *         an outlier (<code>true</code>) or not (<code>false</code>).
	 * 
	 * @see IQROutlierDetector#markOutliersUsingIQR(List)
	 */
	public static Map<Double, Boolean> markOutliersUsingIQR(List<Double> values) {
		return getDefaultIQROutlierDetector().markOutliersUsingIQR(values);
	}

	/**
	 * @return the default IQR outlier detector for this class
	 */
	public static IQROutlierDetector getDefaultIQROutlierDetector() {
		if (iqrOutlierDetector == null) {
			iqrOutlierDetector = new IQROutlierDetector();
		}
		return iqrOutlierDetector;
	}

	/**
	 * Calculates the p-Value by executing an unpaired t-test for the given two
	 * non-normally distributed samples.<br />
	 * <b>Be aware</b>: The method requires at least two values for each value
	 * list to run a t-test.
	 * 
	 * @param values1
	 *            sample one
	 * @param values2
	 *            sample two
	 * @return p-value the t-test p-value in range [0-1]. Can be -1, if at least
	 *         one of the lists has below 2 data entries.
	 */
	public static double tTestOnNonNormalDistributedSample(List<? extends Number> values1,
			List<? extends Number> values2) {

		List<Double> sums1 = new ArrayList<>();
		List<Double> sums2 = new ArrayList<>();
		createNormalDistributionByBootstrapping(values1, values2, sums1, sums2);
		return tTest(sums1, sums2);

	}

	/**
	 * Creates normal distribution by bootstrapping the given samples.
	 * 
	 * @param values1
	 *            input sample 1
	 * @param values2
	 *            input sample 2
	 * @param sums1
	 *            output for sample 1
	 * @param sums2
	 *            output for sample 2
	 */
	public static void createNormalDistributionByBootstrapping(List<? extends Number> values1,
			List<? extends Number> values2, final List<Double> sums1, final List<Double> sums2) {
		if (values1.size() < 3 * NUM_ITEMS_IN_SUM || values2.size() < 3 * NUM_ITEMS_IN_SUM) {
			throw new RuntimeException(
					"Cannot conduct t-test on non normally distributed sample sets. Not enough data points!");
		}
		double sum = 0;
		int counter = 0;
		for (Number num : values1) {
			if (counter % NUM_ITEMS_IN_SUM == 0 && counter != 0) {
				sums1.add(sum / (double) NUM_ITEMS_IN_SUM);
				sum = 0;
			}
			sum += num.doubleValue();
			counter++;
		}

		sum = 0;
		counter = 0;
		for (Number num : values2) {
			if (counter % NUM_ITEMS_IN_SUM == 0 && counter != 0) {
				sums2.add(sum / (double) NUM_ITEMS_IN_SUM);
				sum = 0;
			}
			sum += num.doubleValue();
			counter++;
		}
	}

	/**
	 * Calculates the p-Value by executing an unpaired t-test for the given to
	 * samples.<br />
	 * <b>Be aware</b>: The method requires at least two values for each value
	 * list to run a t-test.
	 * 
	 * @param values1
	 *            sample one
	 * @param values2
	 *            sample two
	 * @return p-value the t-test p-value in range [0-1]. Can be -1, if at least
	 *         one of the lists has below 2 data entries.
	 */
	public static double tTest(List<? extends Number> values1, List<? extends Number> values2) {
		// Apache commons requires at least 2 values to do a t-test
		if (values1.size() < 2 || values2.size() < 2) {
			return -1;
		}

		double[] sample1 = new double[values1.size()];
		double[] sample2 = new double[values2.size()];

		int i = 0;
		for (Number value : values1) {
			sample1[i] = value.doubleValue();
			i++;
		}

		i = 0;
		for (Number value : values2) {
			sample2[i] = value.doubleValue();
			i++;
		}

		return TestUtils.tTest(sample1, sample2);
	}

	/**
	 * Converts a list of a specific Number type A to a list of a number type B.
	 * 
	 * @param values
	 *            list to convert
	 * @param targetType
	 *            type of the target list
	 * @return list of converted values
	 * @param <A>
	 *            source type
	 * @param <B>
	 *            target type
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Number, B extends Number> List<B> convertList(List<A> values, Class<B> targetType) {
		LpeSupportedTypes lpeType = LpeSupportedTypes.get(targetType);
		List<B> bList = new ArrayList<>();
		switch (lpeType) {
		case Double:
			for (A a : values) {
				bList.add((B) new Double(a.doubleValue()));
			}
			break;
		case Float:
			for (A a : values) {
				bList.add((B) new Float(a.floatValue()));
			}
			break;
		case Integer:
			for (A a : values) {
				bList.add((B) new Integer(a.intValue()));
			}
			break;
		case Long:
			for (A a : values) {
				bList.add((B) new Long(a.longValue()));
			}
			break;
		case Short:
			for (A a : values) {
				bList.add((B) new Short(a.shortValue()));
			}
			break;

		default:
			throw new IllegalArgumentException(targetType + " is not a Number!");
		}

		return bList;

	}

	/**
	 * Returns a fraction closest to the specified double with a power of the
	 * given basis as denominator. The fraction is returned as array of integer
	 * with the numerator as zeroth and the denominator as first element.
	 * 
	 * @param dec
	 *            double to be converted into a fraction
	 * @param basis
	 *            basis of the denominator
	 * @param precision
	 *            denotes how close to the decimal value the fraction may be<br>
	 *            <b>Note:</b> 0 < {@code precision} and {@code basis}<sup>
	 *            {@code precision}</sup> * {@code dec} <=
	 *            {@code Integer.MAX_VALUE}
	 * @return the closest fraction to {@code dec} with {@code basis}
	 *         <sup>n</sup> as denominator
	 */
	public static int[] getFractionFromDouble(double dec, int basis, int precision) {
		int denom = (int) Math.pow(basis, precision);
		int num = (int) Math.round(dec * denom);

		while (num % 2 == 0) {
			num /= 2;
			denom /= 2;
		}

		return new int[] { num, denom };
	}

	/**
	 * Returns a fraction closest to the specified double. The fraction is
	 * returned as array of integer with the numerator as zeroth and the
	 * denominator as first element.
	 * 
	 * @param dec
	 *            double to be converted into a fraction
	 * @return the closest fraction to {@code dec}
	 */
	public static int[] getFractionFromDouble(double dec) {
		if (dec < 0.005) {
			return new int[] { 0, 1 };
		}

		double tmp = 1 / dec;
		int integer = (int) tmp;
		int[] rest = getFractionFromDouble(tmp - integer);

		return new int[] { rest[1], integer * rest[1] + rest[0] };
	}

	/**
	 * Creates a linear regression object.
	 * 
	 * @param dataPoints
	 *            data points to add
	 * @return SimpleRegression
	 */
	public static SimpleRegression linearRegression(NumericPairList<? extends Number, ? extends Number> dataPoints) {
		SimpleRegression regression = new SimpleRegression(true);
		for (NumericPair<? extends Number, ? extends Number> point : dataPoints) {
			regression.addData(point.getKey().doubleValue(), point.getValue().doubleValue());
		}
		return regression;
	}

	/**
	 * calculates Erlangs C formula for further calculation of response times in
	 * a multi-server queue (cf. queueing theory).
	 * 
	 * @param numServers
	 *            number of servers
	 * @param utilization
	 *            overall server utilization
	 * @return the calculated value for the given parameters
	 */
	public static double calculateErlangsCFormula(int numServers, double utilization) {
		double c = (double) numServers;
		double p = utilization;
		double cp = c * p;
		double cpC = Math.pow(cp, c);
		double cFac = factorial(numServers);
		double termA = (cpC / cFac) * (1 / (1 - p));

		double termB = 0;
		for (int i = 0; i < numServers; i++) {
			double cpI = Math.pow(cp, i);
			double iFac = factorial(i);
			termB += cpI / iFac;
		}

		return termA / (termB + termA);
	}

	/**
	 * Calculates the factorial of x.
	 * 
	 * @param x
	 *            the value for which to calculate the factorial
	 * @return fac(x)
	 */
	public static long factorial(int x) {
		long result = 1;
		while (x >= 1) {
			result *= x;
			x--;
		}
		return result;
	}

	public static <T extends Number, S extends Number> double meanNormalizedDistance(NumericPairList<T, S> points,
			double keyRange, double valueRange) {
		double keyFactor = 1.0 / keyRange;
		double valueFactor = 1.0 / valueRange;
		double sumDistance = 0;
		NumericPair<T, S> prevPoint = null;
		for (NumericPair<T, S> point : points) {
			if (prevPoint != null) {
				sumDistance += distance(prevPoint, point, keyFactor, valueFactor);
			}
			prevPoint = point;
		}
		return sumDistance / (double) (points.size() - 1);
	}

	public static <T extends Number, S extends Number> List<NumericPairList<T, S>> dbscanNormalized(
			NumericPairList<T, S> points, double epsilon, int minNumPoints, double keyRange, double valueRange) {
		NormalizedDistanceMeasure distanceMeasure = new NormalizedDistanceMeasure(1.0 / keyRange, 1.0 / valueRange);
		DBSCANClusterer<NumericPair<T, S>> clusterer = new DBSCANClusterer<NumericPair<T, S>>(epsilon, minNumPoints,
				distanceMeasure);
		List<Cluster<NumericPair<T, S>>> clusters = clusterer.cluster(points.getPairs());
		List<NumericPairList<T, S>> result = new ArrayList<>();
		for (Cluster<NumericPair<T, S>> c : clusters) {
			NumericPairList<T, S> pairList = new NumericPairList<>();
			for (NumericPair<T, S> pair : c.getPoints()) {
				pairList.add(pair);
			}
			result.add(pairList);

		}
		return result;
	}

	public static <T extends Number, S extends Number> List<NumericPairList<T, S>> dbscan(NumericPairList<T, S> points,
			double epsilon, int minNumPoints) {
		DBSCANClusterer<NumericPair<T, S>> clusterer = new DBSCANClusterer<NumericPair<T, S>>(epsilon, minNumPoints);
		List<Cluster<NumericPair<T, S>>> clusters = clusterer.cluster(points.getPairs());
		List<NumericPairList<T, S>> result = new ArrayList<>();
		for (Cluster<NumericPair<T, S>> c : clusters) {
			NumericPairList<T, S> pairList = new NumericPairList<>();
			for (NumericPair<T, S> pair : c.getPoints()) {
				pairList.add(pair);
			}
			result.add(pairList);

		}
		return result;
	}

	private static <T extends Number, S extends Number> double distance(NumericPair<T, S> point_1,
			NumericPair<T, S> point_2, double keyFactor, double valueFactor) {
		return Math.sqrt(Math.pow((point_1.getKey().doubleValue() - point_2.getKey().doubleValue()) * keyFactor, 2)
				+ Math.pow((point_1.getValue().doubleValue() - point_2.getValue().doubleValue()) * valueFactor, 2));
	}

}
