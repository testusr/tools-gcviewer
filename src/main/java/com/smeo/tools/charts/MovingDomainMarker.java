package com.smeo.tools.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;

public class MovingDomainMarker {
	private long stepSizeInMs = 500;
	private long millis;
	private JFreeChart[] charts;

	public MovingDomainMarker(long startPositionInMs, JFreeChart... charts) {
		super();
		this.millis = startPositionInMs;
		this.charts = charts;
	}

	public void moveRight() {
		millis += stepSizeInMs;
		addMarker();
	}

	public void moveLeft() {
		millis -= stepSizeInMs;
		addMarker();
	}

	public void addMouseListener(ChartPanel chartPanel) {
		chartPanel.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				event.getChart().getXYPlot();
				XYPlot xyplot = (XYPlot) event.getChart().getPlot();
				long domainAxis = (long) xyplot.getDomainCrosshairValue();
				if (millis != domainAxis) {
					millis = domainAxis;
					addMarker();
				}
			}
		});
	}

	public void addKeyListeners(JComponent jcomponent) {
		jcomponent.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				System.err.println(e);
				int location = e.getKeyLocation();
				if (location == KeyEvent.KEY_LOCATION_STANDARD) {
				} else if (location == KeyEvent.KEY_LOCATION_LEFT) {
					moveLeft();
				} else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
					moveRight();
				} else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
				} else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				System.err.println(".keyReleased(e) - REMOVE ME - ");
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void addMarker() {
		Marker marker = new ValueMarker(millis, Color.blue,
				new BasicStroke(2.0f));
		for (JFreeChart currChart : charts) {
			if (currChart != null) {
				XYPlot xyPlot = (XYPlot) currChart.getPlot();
				xyPlot.clearDomainMarkers();
				xyPlot.addDomainMarker(marker);
			}
		}

	}

}
