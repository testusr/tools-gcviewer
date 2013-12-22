package com.smeo.tools.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

public class ValueRepresentationUtils {

	public static String createStandardStringRepresentation(List<String> header, List<List<Object>> values) {
		StringBuffer stringBuffer = new StringBuffer();

		for (List<Object> currValueRow : values) {
			int i = 0;
			for (Object currValue : currValueRow) {
				stringBuffer.append(header.get(i++) + "=" + currValue + ", ");
			}
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}

	public static String createCSVString(TableModel tableModel) {
		List<String> header = new ArrayList<String>();
		List<List<Object>> values = new ArrayList<List<Object>>();

		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			header.add(tableModel.getColumnName(i));
		}

		for (int j = 0; j < tableModel.getRowCount(); j++) {
			List<Object> newRow = new ArrayList<Object>();
			for (int i = 0; i < tableModel.getColumnCount(); i++) {
				newRow.add(tableModel.getValueAt(j, i));
			}
			values.add(newRow);
		}
		return createCSVString(header, values);
	}

	public static String createCSVString(List<String> header, List<List<Object>> values) {
		StringBuffer stringBuffer = new StringBuffer();
		boolean isFirst = true;
		for (String currAttribute : header) {
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuffer.append(",");
			}
			stringBuffer.append("\"" + currAttribute + "\"");
		}
		stringBuffer.append("\n,,\n");

		for (List<Object> currValueRow : values) {
			isFirst = true;
			for (Object currValue : currValueRow) {
				if (!isFirst) {
					stringBuffer.append(",");
				} else {
					isFirst = false;
				}
				if (currValue instanceof Number) {
					stringBuffer.append("" + currValue);
				} else {
					stringBuffer.append("\"" + currValue + "\"");
				}
			}
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}

	public static void writeToFile(String filename, String fileContent) {
		try {
			File file = new File(filename);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(fileContent);
			out.close();
			System.out.println("written file: " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
