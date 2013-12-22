package com.smeo.tools.gc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.smeo.tools.charts.ChartUtility;
import com.smeo.tools.charts.MovingDomainMarker;
import com.smeo.tools.charts.PlotChartFactory;
import com.smeo.tools.gc.dataset.GarbageCollectionDataSetFactory;
import com.smeo.tools.gc.dataset.MemoryDataSetFactory;
import com.smeo.tools.gc.dataset.StopTimeDataSetFactory;
import com.smeo.tools.gc.dataset.SurvivorInputOutputDataSetFactory;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.gui.GarbaceCollectionCountChartFactory;
import com.smeo.tools.gc.gui.MemoryInfoDataSetPlotChartFactory;
import com.smeo.tools.gc.gui.SurvivorIncomingOutgoingChartFactory;
import com.smeo.tools.gc.parser.GcLogParser;

/**
 * Graphical tool to display GC behavior supporting gc.logs for CMS running with the following options
 * -Xloggc:../../logs/gc.log
 * -XX:+PrintGCApplicationStoppedTime
 * -XX:+PrintGCDetails
 * -XX:+PrintGCTimeStamps
 * -XX:+PrintGCDateStamps
 * -XX:+PrintTenuringDistribution
 * 
 * @author smeo
 * 
 */
public class NewGcParser {
	JPanel allTablesPanel = new JPanel();
	JFreeChart totalMemoryChart;
	JFreeChart edenSpaceChart;
	JFreeChart survivorSpaceChart;
	JFreeChart oldGenSpaceChart;
	JFreeChart permGenSpaceChart;
	JFreeChart permGenSpaceChartTotal;
	JFreeChart survivorSpaceFlowChart;
	JFreeChart applicationStopTimeChart;
	JFreeChart garbageCollectionCountChart;

	MovingDomainMarker movingDomainMarker;
	GcLogParser gcLogParser;
	MemoryDataSetFactory dataSetFactory = new MemoryDataSetFactory();
	SurvivorInputOutputDataSetFactory survivorInputOutputDataSetFactory = new SurvivorInputOutputDataSetFactory();

	List<JFreeChart> charts = new ArrayList<JFreeChart>();

	public NewGcParser(GcLogParser gcLogParser) {
		super();
		this.gcLogParser = gcLogParser;
	}

	public void createAndShowCharts() {
		MemoryInfoDataSetPlotChartFactory memoryInfoDataSetPlotChart = new MemoryInfoDataSetPlotChartFactory();

		List<GarbageCollectionEvent> allGarbageCollectionEvents = gcLogParser.getAllGarbageCollectionEvents();
		int intervalInMs = 500;
		try {
			totalMemoryChart = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createTotalMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "TotalSpace", false, false, true);
			addChart(totalMemoryChart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			edenSpaceChart = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createEdenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "EdenSpace", false, false, true);
			addChart(edenSpaceChart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			survivorSpaceChart = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createUsedSurvivorMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "SurvivorSpace", true, false, false);
			addChart(survivorSpaceChart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			oldGenSpaceChart = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createOldGenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "OldGenSpace", false, false, true);
			addChart(oldGenSpaceChart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			permGenSpaceChart = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createPermGenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "PermGenSpace", true, false, false);
			addChart(permGenSpaceChart);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			permGenSpaceChartTotal = memoryInfoDataSetPlotChart.createChart(
					dataSetFactory.createPermGenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "PermGenSpace(TotalSpace)", false, false, true);
			addChart(permGenSpaceChartTotal);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			survivorSpaceFlowChart = SurvivorIncomingOutgoingChartFactory.createChart(survivorInputOutputDataSetFactory
					.createDataSet(allGarbageCollectionEvents));
			addChart(survivorSpaceChart);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (allGarbageCollectionEvents.size() > 0) {
				applicationStopTimeChart = PlotChartFactory.createChart(
						StopTimeDataSetFactory.createApplicationStopTimeDataSet(allGarbageCollectionEvents, intervalInMs),
						"ApplicationStopTime", "ApplicationStopTime(%)", Color.black);
				addChart(applicationStopTimeChart);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			garbageCollectionCountChart = GarbaceCollectionCountChartFactory.createChart(GarbageCollectionDataSetFactory
					.createGarbageCollectionDataSets(allGarbageCollectionEvents));
			addChart(garbageCollectionCountChart);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFreeChart[] chartsArray = charts.toArray(new JFreeChart[0]);
		ChartUtility.linkChartZoom(chartsArray);
		initMovingDomainMarker(allGarbageCollectionEvents, chartsArray);

		showChart(chartsArray);

	}

	public static NewGcParser createGeneralLogFileParser() {
		return new NewGcParser(GcLogParser.createGenericParser());
	}

	public static NewGcParser createCmsLogFileParser() {
		return new NewGcParser(GcLogParser.createCmsParser());
	}

	private void addChart(JFreeChart chart) {
		if (chart != null) {
			charts.add(chart);
		}
	}

	private void initMovingDomainMarker(List<GarbageCollectionEvent> allGarbageCollectionEvents, JFreeChart... charts) {
		long halfRangeTimeInMs = 0;
		if (allGarbageCollectionEvents.size() > 0) {
			halfRangeTimeInMs = allGarbageCollectionEvents.get(allGarbageCollectionEvents.size() / 2).time.getTime();
		}
		movingDomainMarker = new MovingDomainMarker(halfRangeTimeInMs, charts);
	}

	private void showChart(JFreeChart... charts) {
		allTablesPanel.setLayout(new BoxLayout(allTablesPanel, BoxLayout.X_AXIS));

		JPanel currTablePairPanel = null;
		for (int i = 0; i < charts.length; i++) {
			if (currTablePairPanel == null) {
				currTablePairPanel = new JPanel();
				currTablePairPanel.setLayout(new BoxLayout(currTablePairPanel, BoxLayout.Y_AXIS));
			}
			ChartPanel currChartPanel = new ChartPanel(charts[i]);
			currTablePairPanel.add(currChartPanel);
			if (movingDomainMarker != null) {
				movingDomainMarker.addMouseListener(currChartPanel);
			}

			if (i % 2 == 0) {
				allTablesPanel.add(currTablePairPanel);
				currTablePairPanel = null;
			}
		}

		if (currTablePairPanel != null) {
			currTablePairPanel.add(new JPanel());
			allTablesPanel.add(currTablePairPanel);
		}

		JFrame frame = new JFrame("GC - log graphical");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(allTablesPanel);

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

	private void parseLine(String line) {
		gcLogParser.parseLine(line);
	}

	private static class GcParserCommandLineStarter {
		private Options commandLineOptions;

		public static String OPT_GCLOGTYPE_CMS = "cms";
		public static String OPT_FILENAME = "file";
		private String filename = "";
		private boolean isCmsLogFile = false;

		public GcParserCommandLineStarter() {
			initOptions();
		}

		public boolean readCommandLine(String[] args) {
			if (args.length == 0) {
				printHelp();
				return false;
			} else {
				CommandLineParser parser = new GnuParser();
				try {
					CommandLine commandLine = parser.parse(commandLineOptions, args);
					for (Option currOption : commandLine.getOptions()) {

						System.err.println(currOption.getOpt() + ": " + currOption.getArgName() + "[" + currOption.getArgs() + "] ,"
									+ currOption.getValuesList());
						if (currOption.getOpt().startsWith(OPT_FILENAME)) {
							filename = currOption.getValue();
						}
						if (currOption.getOpt().startsWith(OPT_GCLOGTYPE_CMS)) {
							isCmsLogFile = true;
						}
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
				return true;
			}
		}

		public void startGcParser() {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(
							new FileInputStream(filename)));

				NewGcParser newParser = isCmsLogFile ? NewGcParser.createCmsLogFileParser() : NewGcParser.createGeneralLogFileParser();
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						newParser.parseLine(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				newParser.createAndShowCharts();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void printHelp() {
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp("command line syntax", commandLineOptions);
		}

		private void initOptions() {
			OptionBuilder.withArgName("cms");
			Option eventName = OptionBuilder
						.withDescription("showing more cms specific details")
						.create(OPT_GCLOGTYPE_CMS);

			Option logFileName = OptionBuilder.withArgName("path")
						.hasArgs()
						.withDescription("log file to read from")
						.create(OPT_FILENAME);

			commandLineOptions = new Options();
			commandLineOptions.addOption(eventName);
			commandLineOptions.addOption(logFileName);

		}

	}

	public static void main(String[] args) throws IOException {
		GcParserCommandLineStarter gcCommandLineParser = new GcParserCommandLineStarter();
		if (gcCommandLineParser.readCommandLine(args)) {
			gcCommandLineParser.startGcParser();
		}
	}

}
