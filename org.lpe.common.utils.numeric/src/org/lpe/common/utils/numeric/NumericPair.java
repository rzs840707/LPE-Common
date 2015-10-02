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
package org.lpe.common.utils.numeric;

import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Wrapps a pair of two numeric values.
 * 
 * @author Alexander Wert
 * 
 * @param <T>
 *            key type
 * @param <S>
 *            value type
 */
public class NumericPair<T extends Number, S extends Number> implements Comparable<NumericPair<T, S>>, Clusterable {
	private T key;
	private S value;

	/**
	 * @param key
	 *            numeric key
	 * @param value
	 *            numeric value
	 */
	public NumericPair(T key, S value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public T getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(T key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public S getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(S value) {
		this.value = value;
	}

	@Override
	public int compareTo(NumericPair<T, S> o) {
		if (getKey().equals(o.getKey())) {
			if (getValue().doubleValue() < o.getValue().doubleValue()) {
				return -1;
			} else if (getValue().doubleValue() > o.getValue().doubleValue()) {
				return 1;
			}
		} else {
			if (getKey().doubleValue() < o.getKey().doubleValue()) {
				return -1;
			} else if (getKey().doubleValue() > o.getKey().doubleValue()) {
				return 1;
			}
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NumericPair<?, ?> other = (NumericPair<?, ?>) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public double[] getPoint() {
		double [] point = new double[2];
		point[0]=this.getKey().doubleValue();
		point[1]=this.getValue().doubleValue();
		return point;
	}

}
