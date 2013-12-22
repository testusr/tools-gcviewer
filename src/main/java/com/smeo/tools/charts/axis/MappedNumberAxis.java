package com.smeo.tools.charts.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.ui.RectangleEdge;

public class MappedNumberAxis extends NumberAxis {
	private Map<Double, String> valueLabelMapping;

	public MappedNumberAxis(Map<Double, String> valueLabelMapping) {
		super();
		this.valueLabelMapping = valueLabelMapping;
	}

	public MappedNumberAxis(String valueAxisLabel, Map<Double, String> valueLabelMapping) {
		super(valueAxisLabel);
		this.valueLabelMapping = valueLabelMapping;
	}

	@Override
	protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea,
			RectangleEdge edge) {
		// TODO Auto-generated method stub
		return getMappedTicks(super.refreshTicksHorizontal(g2, dataArea, edge));
	}

	@Override
	public List refreshTicks(Graphics2D g2, AxisState state,
			Rectangle2D dataArea, RectangleEdge edge) {
		return getMappedTicks(super.refreshTicks(g2, state, dataArea, edge));
	}

	private List getMappedTicks(List ticks) {
		List newTickList = new ArrayList();
		for (int i = 0; i < ticks.size(); i++) {
			NumberTick numberTick = (NumberTick) ticks.get(i);
			double tickValue = numberTick.getValue();
			if (valueLabelMapping.containsKey(tickValue)) {
				newTickList.add(new NumberTick(new Double(tickValue),
						valueLabelMapping.get(tickValue),
						numberTick.getTextAnchor(),
						numberTick.getRotationAnchor(),
						numberTick.getAngle()));
			}
			// else {
			// newTickList.add(new NumberTick(new Double(tickValue),
			// "",
			// numberTick.getTextAnchor(),
			// numberTick.getRotationAnchor(),
			// numberTick.getAngle()));
			// }
		}
		return newTickList;
	}
}
