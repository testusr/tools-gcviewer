package com.smeo.tools.common;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableUtils {

	/**
	 * Set columns minimal size to a size that always shows the complete header
	 * and content values. HEAVY OPERATION !! Use the maxColumns field !!
	 * 
	 * @param table
	 * @param maxRows
	 *            the number of columns (starting at 0) that are used for
	 *            width calculation. if <code><0</code> all columns will be used.
	 * @param addToMinWidth
	 *            this value will be added to the calculated minWidth
	 * @param maxSize
	 *            will be ignored if -1
	 * 
	 */
	public static void adjustColumnMinWidths(JTable table, int maxRows, int addToMinWidth, int maxSize) {
		TableColumnModel columnModel = table.getColumnModel();
		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxwidth = 0;
			int lastRow;
			if ((maxRows > 0) && (maxRows < table.getRowCount())) {
				lastRow = maxRows;
			} else {
				lastRow = table.getRowCount();
			}

			for (int row = 0; row < lastRow; row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = null;
				try {
					value = table.getValueAt(row, col);
				} catch (Exception e) {
					e.printStackTrace();
					table.getModel().getRowCount();
					table.getValueAt(row, col);
				}
				Component comp = rend.getTableCellRendererComponent(table,
						value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
				String renderer = rend.getClass().getSimpleName();
			} // for row
			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)

				headerRenderer = table.getTableHeader().getDefaultRenderer();
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(
					table, headerValue, false, false, 0, col);
			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width + 10);
			if (maxSize > 0 && maxwidth > maxSize) {
				maxwidth = maxSize;
			}
			column.setPreferredWidth(maxwidth + addToMinWidth);
		} // for col
	}
}
