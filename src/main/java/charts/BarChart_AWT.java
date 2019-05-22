package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.math.BigDecimal;

public class BarChart_AWT extends ApplicationFrame {
    private static final long serialVersionUID = -2415640732273733564L;

    public BarChart_AWT( final String applicationTitle, final String chartTitle, final BigDecimal trainingParam,
        final BigDecimal testParam, final String rowKey, final String columnKeyTraining, final String columnKeyTest ) {
        super(applicationTitle);
        final JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Data", rowKey,
            createDataset(trainingParam, testParam, rowKey, columnKeyTraining, columnKeyTest), PlotOrientation.VERTICAL,
            true, true, false);
        final CategoryPlot plot = barChart.getCategoryPlot();
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.getHSBColor(0, 68, 300));
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);

        final ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);

    }

    private CategoryDataset createDataset( final BigDecimal trainingParam, final BigDecimal testParam,
        final String rowKey, final String columnKeyTraining, final String columnKeyTest ) {

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(trainingParam, rowKey, columnKeyTraining);
        dataset.addValue(testParam, rowKey, columnKeyTest);

        return dataset;
    }
}
