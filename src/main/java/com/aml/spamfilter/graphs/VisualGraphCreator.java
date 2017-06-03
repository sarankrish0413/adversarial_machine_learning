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
import java.util.ArrayList;
import java.util.HashMap;
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

    public static void main(String[] args) {
        Map<String, List<Double>> testData = new HashMap<>();


        List<Double> histogramValues = new ArrayList<Double>() {{
            add(0.984277958495226);
            add(0.9080833408632271);
            add(0.6021731273154423);
            add(0.5142299930724978);
            add(0.5056139122315593);
            add(0.49040277401283094);
            add( 0.49338272943586037);
            add(0.49607843137254903);
            add( 0.4973856209150327);
            add( 0.4993464052287582);
            add( 0.4993464052287582);


        }};

        List<Double> kernelValues = new ArrayList<Double>() {{
            add(0.9843242673413451);
            add(0.8751441974639318);
            add(0.5805137646456432);
            add( 0.506834884491431);
            add( 0.5078694918827746);
            add(0.49170770458721125);
            add( 0.4947712418300654);
            add( 0.49673202614379086);
            add(0.4980392156862745);
            add( 0.4993464052287582);
            add(0.4993464052287582);



        }};

        testData.put("histogram", histogramValues);
        testData.put("kernel", kernelValues);

        VisualGraphCreator visualGraphCreator = new VisualGraphCreator("WeightedBagging with 10 iterations")
                .withData(testData)
                .withStepValue(4);
        visualGraphCreator.drawGraph();
    }

}
