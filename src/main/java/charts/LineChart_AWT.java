package charts;

import model.RiskModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.math.RoundingMode;
import java.util.List;

public class LineChart_AWT extends ApplicationFrame {
    private static final long serialVersionUID = -4252913454258292505L;

    public LineChart_AWT( final String title, final List<RiskModel> cdfReturns ) {
        super(title);
        final JFreeChart lineChartObject = ChartFactory
            .createLineChart("Schools Vs Years", "Year", "Schools Count", createDataset(cdfReturns),
                PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel = new ChartPanel(lineChartObject);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset( final List<RiskModel> cdfReturns ) {
        final DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for ( final RiskModel actual : cdfReturns ) {
            line_chart_dataset.addValue(actual.getCentralDistribution(), "CDF",
                actual.getDailyReturn().setScale(4, RoundingMode.HALF_UP));

        }
        return line_chart_dataset;
    }
}
