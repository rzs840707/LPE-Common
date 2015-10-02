package org.lpe.common.utils.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.lpe.common.utils.numeric.NumericPair;
import org.lpe.common.utils.numeric.NumericPairList;

import au.com.bytecode.opencsv.CSVWriter;

public final class LpeCsvUtils {

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
	public static void exportAsCSV(final NumericPairList<? extends Number, ? extends Number> list, final String file,
			final String keyColumnName, final String valueColumnName) {
		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(file);
			csvWriter = new CSVWriter(fWriter, ';');
			final String[] line = { keyColumnName, valueColumnName };
			csvWriter.writeNext(line);
			for (final NumericPair<? extends Number, ? extends Number> pair : list) {
				line[0] = pair.getKey().toString();
				line[1] = pair.getValue().toString();
				csvWriter.writeNext(line);
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
	
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
	
			if (fWriter != null) {
				try {
					fWriter.close();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	
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
	public static void exportAsCSV(final String file, final List<? extends Number>... data) {
	
		boolean first = true;
		int prevSize = -1;
		for (final Collection<? extends Number> col : data) {
			if (col == null) {
				throw new RuntimeException("Null collection");
			}
	
			if (first) {
				first = false;
				prevSize = col.size();
				continue;
			}
			if (col.size() != prevSize) {
				throw new RuntimeException("Collections have unequal sizes!");
			}
			prevSize = col.size();
	
		}
		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(file);
			csvWriter = new CSVWriter(fWriter, ';');
			final String[] line = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				line[i] = "Col" + i;
			}
			csvWriter.writeNext(line);
	
			for (int index = 0; index < data[0].size(); index++) {
				for (int i = 0; i < data.length; i++) {
					line[i] = data[i].get(index).toString();
	
				}
				csvWriter.writeNext(line);
			}
	
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
	
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
	
			if (fWriter != null) {
				try {
					fWriter.close();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	
	}

}
