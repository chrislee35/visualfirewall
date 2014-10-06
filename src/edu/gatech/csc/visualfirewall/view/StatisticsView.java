/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import edu.gatech.csc.visualfirewall.VisualFirewall;
import edu.gatech.csc.visualfirewall.data.*;
import edu.gatech.csc.visualfirewall.data.listener.IPTableResultListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;

/**
 * @author chris Mar 31, 2005 StatisticsView
 */
public class StatisticsView extends AbstractView implements
		IPTableResultListener {
	boolean DEBUG = false;
	static TimeSeriesCollection dataset;
	static TimeSeries[] series = new TimeSeries[3];
	static String category = "";
	Date currtimestamp = null;
	int throughput = 0, throughput_in = 0, throughput_out = 0;
	JPanel chartPanel;

	static final int UPDATE_PERIOD = 10;
	
	/**
	 * @param arg0
	 */
	public StatisticsView(Rectangle worldRect) {
		super(worldRect);
		setWorldWindowRect(worldWindowRect);
		dataset = createDataset();

		chart = createChart(dataset);
		chartPanel = (JPanel) new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 500));
		//((ChartPanel) chartPanel).setVerticalZoom(false);
		//((ChartPanel) chartPanel).setHorizontalZoom(false);

		canvas = new JPanel(new BorderLayout());
		((JPanel) canvas).add(chartPanel);
		Timer timer = new Timer();
		timer.schedule( new java.util.TimerTask() {
			public void run() {
				updateChart();
			}
		}, 10*1000, 10*1000 );
		
		setName("Statistics");
	}

	private static TimeSeriesCollection createDataset() {
		series[0] = new TimeSeries("Total Throughput (bytes/sec)", Second.class);
		series[1] = new TimeSeries("Incoming Throughput (bytes/sec)",
				Second.class);
		series[2] = new TimeSeries("Outgoing Throughput (bytes/sec)",
				Second.class);
		dataset = new TimeSeriesCollection();
		dataset.addSeries(series[0]);
		dataset.addSeries(series[1]);
		dataset.addSeries(series[2]);
		return dataset;
	}

	public void addMouseListener(VFW_MouseListener vfwML) {
		//System.out.println("Statistics: addMouseListener(VFW_MouseListener vfwML) called.");
		chartPanel.addMouseListener(vfwML);
		((ChartPanel) chartPanel).addChartMouseListener(vfwML);
	}

	private static JFreeChart createChart(TimeSeriesCollection dataset) {
		//		 create the chart...
		JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // chart
																	// title
				"Time (sec)", // domain axis label
				"Throughput bytes/sec", // range axis label
				dataset, // data
				false, // include legend
				false, // tooltips?
				false // URLs?
				);
		//		 NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		//		 set the background color for the chart...

		chart.setBackgroundPaint( VisualFirewall.BG_COLOR );
		chart.setBorderPaint( VisualFirewall.FG_COLOR );
		//		 OPTIONAL CUSTOMISATION COMPLETED.
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint( VisualFirewall.BG_COLOR );
		
		
		plot.getRangeAxis().setLabelPaint( VisualFirewall.FG_COLOR );
		plot.getRangeAxis().setTickLabelPaint( VisualFirewall.FG_COLOR );
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setLabelPaint( VisualFirewall.FG_COLOR );
		axis.setDateFormatOverride(new SimpleDateFormat("hh:mm:ss"));
		axis.setAutoRange(true);
		axis.setFixedAutoRange(3600000);
		axis.setTickLabelPaint( VisualFirewall.FG_COLOR );
		return chart;
	}

	public static JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}

	/**
	 * Returns a description of the demo.
	 * 
	 * @return A description.
	 */
	public static String getDemoDescription() {
		return "A time series chart.";
	}

	public void updateChart() {
		Second ts = new Second();
		series[0].add(ts, throughput/UPDATE_PERIOD);
		series[1].add(ts, throughput_in/UPDATE_PERIOD);
		series[2].add(ts, throughput_out/UPDATE_PERIOD);
		throughput = 0;
		throughput_in = 0;
		throughput_out = 0;		
	}
	
	public void dispatchResult(IPTableResult ipTableResult) {
		/*
		if (currtimestamp == null)
			currtimestamp = ipTableResult.timestamp;
		if (ipTableResult.timestamp.compareTo(currtimestamp) > 10) {
			Second ts = new Second(currtimestamp);
			series[0].add(ts, throughput);
			series[1].add(ts, throughput_in);
			series[2].add(ts, throughput_out);
			currtimestamp = ipTableResult.timestamp;
			throughput = 0;
			throughput_in = 0;
			throughput_out = 0;
		}
		*/
		throughput += ipTableResult.packet.length;
		if (ipTableResult.packet.srcip.equals(VisualFirewall.localInetAddress))
			throughput_out += ipTableResult.packet.length;
		else
			throughput_in += ipTableResult.packet.length;
	}

	public static void main(String[] args) {
	}
}
