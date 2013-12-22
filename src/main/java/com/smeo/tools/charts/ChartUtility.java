package com.smeo.tools.charts;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang.Validate;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.data.Range;

public class ChartUtility {
	public static enum Axis {
		DOMAIN_AXIS,
		VALUE_AXIS
	}

	public static void showCharts(String frameTitle, JFreeChart... charts) {
		showCharts(frameTitle, true, charts);
	}

	public static void showCharts(String frameTitle, boolean linkCharts, JFreeChart... charts) {
		JPanel allTablesPanel = createChartsPanel(BoxLayout.X_AXIS, linkCharts, charts);

		JFrame frame = new JFrame(frameTitle);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(allTablesPanel);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

	private static JPanel createChartsPanel(int layout, boolean linkCharts, JFreeChart... charts) {
		JPanel allTablesPanel = new JPanel();
		allTablesPanel.setLayout(new BoxLayout(allTablesPanel, layout));

		JPanel currTablePairPanel = null;
		for (int i = 0; i < charts.length; i++) {
			if (currTablePairPanel == null) {
				currTablePairPanel = new JPanel();
				currTablePairPanel.setLayout(new BoxLayout(currTablePairPanel, BoxLayout.Y_AXIS));
			}
			currTablePairPanel.add(new ChartPanel(charts[i]));

			if (i % 2 == 0) {
				allTablesPanel.add(currTablePairPanel);
				currTablePairPanel = null;
			}
		}

		if (currTablePairPanel != null) {
			currTablePairPanel.add(new JPanel());
			allTablesPanel.add(currTablePairPanel);
		}
		return allTablesPanel;
	}

	public static void linkChartZoom(JFreeChart... charts) {
		for (JFreeChart currChartA : charts) {
			for (JFreeChart currChartB : charts) {
				if (currChartA != null && currChartB != null) {
					linkCharts(currChartA, Axis.DOMAIN_AXIS, currChartB, Axis.DOMAIN_AXIS);
				}
			}
		}

	}

	public static void linkCharts(final JFreeChart chartA, final Axis axisA, final JFreeChart chartB, final Axis axisB) {
		Validate.notNull(axisA, " must not be null");
		Validate.notNull(chartB, " must not be null");

		if (chartA != chartB) {

			chartA.addChangeListener(new ChartChangeListener() {

				@Override
				public void chartChanged(ChartChangeEvent event) {
					ValueAxis valueAxisA = null;
					ValueAxis valueAxisB = null;

					if (axisA == Axis.DOMAIN_AXIS) {
						valueAxisA = chartA.getXYPlot().getDomainAxis();
					} else {
						valueAxisA = chartA.getXYPlot().getRangeAxis();
					}

					if (axisB == Axis.DOMAIN_AXIS) {
						valueAxisB = chartB.getXYPlot().getDomainAxis();
					} else {
						valueAxisB = chartB.getXYPlot().getRangeAxis();
					}

					if (chartA != null) {
						Range srcRange = valueAxisA.getRange();
						Range dstRange = valueAxisB.getRange();
						if (srcRange.toString().compareTo(dstRange.toString()) != 0) {
							valueAxisB.setRange(srcRange);
						}
					}

				}
			});

			chartB.addChangeListener(new ChartChangeListener() {

				@Override
				public void chartChanged(ChartChangeEvent event) {
					ValueAxis valueAxisA = null;
					ValueAxis valueAxisB = null;

					if (axisB == Axis.DOMAIN_AXIS) {
						valueAxisB = chartB.getXYPlot().getDomainAxis();
					} else {
						valueAxisB = chartB.getXYPlot().getRangeAxis();
					}

					if (axisA == Axis.DOMAIN_AXIS) {
						valueAxisA = chartA.getXYPlot().getDomainAxis();
					} else {
						valueAxisA = chartA.getXYPlot().getRangeAxis();
					}

					if (chartB != null) {
						Range srcRange = valueAxisB.getRange();
						Range dstRange = valueAxisA.getRange();
						if (srcRange.toString().compareTo(dstRange.toString()) != 0) {
							valueAxisA.setRange(srcRange);
						}
					}

				}
			});
		}

	}
}
