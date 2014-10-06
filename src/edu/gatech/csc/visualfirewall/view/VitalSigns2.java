/*
 * Created on Apr 14, 2005
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.*;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
 
 
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
 
/**
 * @author chris Apr 14, 2005 VitalSigns
 */
public class VitalSigns2 extends JPanel {
	static DefaultCategoryDataset dataset;
	static String[] series = { "CPU", "Memory Used", "Net Util", "Alerts" };
	static String category = "";
	/**
	 * @param arg0
	 */
	public VitalSigns2(String title) 
	{
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		JPanel chartPanel = (JPanel)new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 500));
		
		setLayout(new BorderLayout());
		add(chartPanel);
		
		chart.getCategoryPlot().getRangeAxis().setAutoRange(false);
		new DataGenerator(100).start();
	}
	
	public VitalSigns2()
	{
		this("");
	}
 
	private static CategoryDataset createDataset() {
		dataset = new DefaultCategoryDataset();
		dataset.addValue(1, series[0], category);
		dataset.addValue(1, series[1], category);
		dataset.addValue(1, series[2], category);
		dataset.addValue(0, series[3], category);
		return dataset;
	}
 
	private static JFreeChart createChart(CategoryDataset dataset) {
		//		 create the chart...
		JFreeChart chart = ChartFactory.createBarChart("VitalSigns2", // chart
				// title
				"Vital Signs", // domain axis label
				"Health", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);
		//		 NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		//		 set the background color for the chart...
		chart.setBackgroundPaint(Color.white);
		//		 get a reference to the plot for further customisation...
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		//		 set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//		 disable bar outlines...
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		//		 set up gradient paints for series...
		GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f,
				0.0f, new Color(0, 0, 64));
		GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f,
				0.0f, new Color(0, 64, 0));
		GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f,
				0.0f, new Color(64, 0, 0));
		GradientPaint gp3 = new GradientPaint(0.0f, 0.0f, Color.yellow, 0.0f,
				0.0f, new Color(64, 0, 0));
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);
		renderer.setSeriesPaint(3, gp3);
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));
		//		 OPTIONAL CUSTOMISATION COMPLETED.
		return chart;
	}
	
	public static void updateMemory(double y) {
		dataset.setValue(y, series[1], category);
	}
 
	public static void updateBitrate(double bps) {
		dataset.setValue(bps, series[2], category);
	}
 
	public static void updateAlert(double alert) {
		dataset.setValue(alert, series[3], category);
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
		return "A bar chart.";
	}
}
 
/**
 * The data generator.
 */
 
class DataGenerator extends Timer implements ActionListener {
	double bps = 0;
	double alerts = 0;
	/**
	 * Constructor.
	 * 
	 * @param interval
	 *            the interval (in milliseconds)
	 */
	DataGenerator(int interval) {
		super(interval, null);
		addActionListener(this);
	}
 
	/**
	 * Adds a new free/total memory reading to the dataset.
	 * 
	 * @param event
	 *            the action event.
	 */
	public void actionPerformed(ActionEvent event) {
		long f = Runtime.getRuntime().freeMemory();
		long t = Runtime.getRuntime().totalMemory();
	    bps += ( Math.random() - bps )/5.0;
		VitalSigns2.updateMemory((double)f/t);
		VitalSigns2.updateBitrate( bps );
		if ( Math.random() <= 1E-3 ) {
			alerts += 1.0d/100.0d;
			VitalSigns2.updateAlert( alerts );
		}
	}
}
