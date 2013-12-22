package com.smeo.tools.charts;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

public class MultiChartFrame extends JPanel {
	List<ChartPanel> chartFrames = new ArrayList<ChartPanel>();

	public MultiChartFrame() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public void addCharFrame(ChartPanel chartFrame) {
		chartFrames.add(chartFrame);

		this.add(chartFrame);
	}

}
