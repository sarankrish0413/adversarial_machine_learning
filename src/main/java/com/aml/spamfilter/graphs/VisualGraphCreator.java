package com.aml.spamfilter.graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Created by saranyakrishnan on 5/28/17.
 */
public class VisualGraphCreator extends ApplicationFrame {

    private Map<String, List<Double>> results;
    private String title;
    private double stepValue;

    public VisualGraphCreator(String title) {
        super(title);
        this.title = title;
    }

    public void drawGraph() {
        XYDataset dataset = createDatasetFromResults();
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "% poisoning",
                "AUC 10%",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);
        LegendTitle legend = new LegendTitle(plot.getRenderer());
        Font font = new Font("Arial",0,12);
        legend.setItemFont(font);
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addLegend(legend);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private XYDataset createDatasetFromResults() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Map.Entry<String, List<Double>> result : results.entrySet()) {
            dataset.addSeries(createSeriesFromResult(result));
        }
        return dataset;
    }

    private XYSeries createSeriesFromResult(Map.Entry<String, List<Double>> result) {
        String seriesName = result.getKey(); // Its the name of weight estimation method (histogram, kernel..etc)
        List<Double> seriesValues = result.getValue();
        XYSeries series = new XYSeries(seriesName);
        double x = 0;
        for (Double seriesValue : seriesValues) {
            series.add(x, seriesValue);
            x += stepValue; // for every poisoning percentage. This doesn't change throught the application
        }
        return series;
    }

    public VisualGraphCreator withData(Map<String, List<Double>> results) {
        this.results = results;
        return this;
    }

    public VisualGraphCreator withStepValue(double stepValue) { // double is convenient to add
        this.stepValue = stepValue;
        return this;
    }


}
