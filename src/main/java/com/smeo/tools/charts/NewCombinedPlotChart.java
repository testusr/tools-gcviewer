package com.smeo.tools.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import com.smeo.tools.common.TimeDataSetSeries;

public class NewCombinedPlotChart {
	private List<TimeDataSetSeries> dataSeries;

	public NewCombinedPlotChart() {
		dataSeries = new ArrayList<TimeDataSetSeries>();
	}

	public void addDataSetSerie(TimeDataSetSeries dataSerie) {
		dataSeries.add(dataSerie);
	}

	public JFreeChart createChart() {
		final NumberAxis valueAxis = new NumberAxis("Value");
		valueAxis.setAutoRangeIncludesZero(false); // override default

		// make a horizontally combined plot
		final CombinedRangeXYPlot parent = new CombinedRangeXYPlot(valueAxis);

		for (TimeDataSetSeries currDataSeries : dataSeries) {
			TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
			timeSeriesCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(currDataSeries));
			XYPlot subplot1 = new XYPlot(timeSeriesCollection, new DateAxis("LogTime"), null,
					new StandardXYItemRenderer());
			parent.add(subplot1, 1);
		}

		// now make the top level JFreeChart
		JFreeChart chart = new JFreeChart("CombinedPlotChart TEST", JFreeChart.DEFAULT_TITLE_FONT, parent, true);

		// then customise it a little...
		final TextTitle subtitle = new TextTitle("This is a subtitle",
											new Font("SansSerif", Font.BOLD, 12));
		chart.addSubtitle(subtitle);
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
		return chart;
	}

	public static void main(String[] args) {
		NewCombinedPlotChart combinedPlotChart = new NewCombinedPlotChart();
		TimeDataSetSeries timeDataSetSeries1 = new TimeDataSetSeries("series1");
		TimeDataSetSeries timeDataSetSeries2 = new TimeDataSetSeries("series2");
		TimeDataSetSeries timeDataSetSeries3 = new TimeDataSetSeries("series3");
		Date date = new Date();
		Random random = new Random();

		for (int i = 0; i < 1000; i++) {
			date.setTime(System.currentTimeMillis() + i * 100000);
			timeDataSetSeries1.addDataSet(date.getTime() * 1000000, new BigDecimal(i));
			timeDataSetSeries2.addDataSet(date.getTime() * 1000000, new BigDecimal(i + (random.nextDouble() * 100)));
			timeDataSetSeries3.addDataSet(date.getTime() * 1000000, new BigDecimal(i + (random.nextDouble() * 100)));
			System.out.println(date);
		}

		combinedPlotChart.addDataSetSerie(timeDataSetSeries1);
		combinedPlotChart.addDataSetSerie(timeDataSetSeries2);
		combinedPlotChart.addDataSetSerie(timeDataSetSeries3);
		JFrame frame = new JFrame("LogMessageQuickFilterTableView");
		final ChartPanel panel = new ChartPanel(combinedPlotChart.createChart(), true, true, true, false, true);
		JScrollPane scrollPane = new JScrollPane(panel);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(300, 150);
		frame.setVisible(true);

	}
}
