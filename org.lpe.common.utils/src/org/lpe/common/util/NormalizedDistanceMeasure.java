package org.lpe.common.util;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class NormalizedDistanceMeasure implements DistanceMeasure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double xScale;
	private double yScale;

	protected NormalizedDistanceMeasure(double xScale, double yScale) {
		this.xScale = xScale;
		this.yScale = yScale;
	}

	@Override
	public double compute(double[] a, double[] b) {
		return Math.sqrt(Math.pow((a[0] - b[0]) * xScale, 2) + Math.pow((a[1] - b[1]) * yScale, 2));
	}

}
