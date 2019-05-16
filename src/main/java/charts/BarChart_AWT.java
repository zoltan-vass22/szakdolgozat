package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class BarChart_AWT extends ApplicationFrame {
    private static final long serialVersionUID = -2415640732273733564L;

    public BarChart_AWT( final String applicationTitle, final String chartTitle, final double trainingParam,
        final double testParam ) {
        super(applicationTitle);
        final JFreeChart barChart = ChartFactory
            .createBarChart(chartTitle, "Category", "Score", createDataset(trainingParam, testParam),
                PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset( final double trainingParam, final double testParam ) {
        final String stdDev = "Standard Deviation";
        final String training = "Training stdDev";
        final String test = "Test stdDev";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(trainingParam, stdDev, training);
        dataset.addValue(testParam, stdDev, test);

        return dataset;
    }
}
