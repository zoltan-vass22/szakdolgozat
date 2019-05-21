package charts;

import model.RiskModelListItem;
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

    public LineChart_AWT( final String title, final List<RiskModelListItem> cdfReturns, final boolean isCdf,
        final String categoryAxisLabel, final String valueAxisLabel, final String rowKey ) {
        super(title);
        final JFreeChart lineChartObject = ChartFactory
            .createLineChart(title, categoryAxisLabel, valueAxisLabel, createDataset(cdfReturns, isCdf, rowKey),
                PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel = new ChartPanel(lineChartObject);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset( final List<RiskModelListItem> cdfReturns, final boolean isCdf,
        final String rowKey ) {
        final DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for ( final RiskModelListItem actual : cdfReturns ) {
            if ( isCdf ) {
                line_chart_dataset.addValue(actual.getCentralDistribution(), rowKey,
                    actual.getDailyReturn().setScale(4, RoundingMode.HALF_UP));
            } else {
                line_chart_dataset
                    .addValue(actual.getDailyReturn().setScale(4, RoundingMode.HALF_UP), rowKey, actual.getDate());
            }

        }
        return line_chart_dataset;
    }
}
