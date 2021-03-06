package com.smeo.tools.gc.gui.charts;

import com.smeo.tools.charts.PlotChartFactory;

import com.smeo.tools.gc.domain.TenuringEvent;
import com.smeo.tools.gc.gui.charts.dataset.TenuringDataSetFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: truehl
 * Date: 12/23/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class TenuringDataSetPlotChartFactory extends PlotChartFactory {
    public static JFreeChart createChartAgeSettings(TenuringDataSetFactory.TenuringDataSet tenuringDataSet) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Tenuring(Age)",
                "Time of Day",
                "Primary Range Axis",
                null,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("max age plus selected new threshold"));
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        TimeSeriesCollection tenuringDataCollection = new TimeSeriesCollection();
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("newThreshold(age)", tenuringDataSet.newThreshold));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("max(age)", tenuringDataSet.maxAges));

        AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, tenuringDataCollection, "tenuringData", true, -1, true);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setSeriesPaint(2, Color.blue);

        return chart;
    }

    public static JFreeChart createTotalAgeDistributionChart(TenuringDataSetFactory.TenuringDataSet tenuringDataSet) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Tenuring (total/kB)",
                "Time of Day",
                "Primary Range Axis",
                null,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("Totally used survivor space till age <x>"));
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        TimeSeriesCollection tenuringDataCollection = new TimeSeriesCollection();
        for (int i=0; i < TenuringEvent.MAX_AGE; i++){
            tenuringDataCollection.addSeries(createBigDecimalTimeSeries("totalSize-Age"+i, tenuringDataSet.agesTotal[i]));
        }

        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("desiredSurvivorSize", tenuringDataSet.desiredSurvivorSize));

        AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, tenuringDataCollection, "tenuringData", false, -1, true);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setSeriesPaint(2, Color.blue);

        return chart;
    }

    public static JFreeChart createTotalAllocationDemographyChart(TenuringDataSetFactory.AllocationDemography allocationDemography) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Allocation Demography",
                "Time of Day",
                "Primary Range Axis",
                null,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("object livetime distribution"));
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        TimeSeriesCollection tenuringDataCollection = new TimeSeriesCollection();
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("shortLived", allocationDemography.shortLivedObjAllocationRate));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("midAged", allocationDemography.midAgedObjAllocationRate));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("longLived", allocationDemography.longLivedObjAllocationRate));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("totalAllocationRate", allocationDemography.totalAllocationRate));


        AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, tenuringDataCollection, "tenuringData", false, -1, true);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setSeriesPaint(2, Color.blue);
        renderer.setSeriesPaint(3, Color.orange);

        return chart;
    }

    public static JFreeChart createMemoryDimensionChart(TenuringDataSetFactory.MemoryDimensioning memoryDimensioning) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Memory Dimension",
                "Time of Day",
                "Primary Range Axis",
                null,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("size of different spaces"));
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        TimeSeriesCollection tenuringDataCollection = new TimeSeriesCollection();
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("oldGen", memoryDimensioning.oldGen));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("survivor", memoryDimensioning.survivorSize));
        tenuringDataCollection.addSeries(createBigDecimalTimeSeries("edenSize", memoryDimensioning.edenSize));


        AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, tenuringDataCollection, "space(K)", false, -1, true);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setSeriesPaint(2, Color.blue);

        return chart;
    }
}
