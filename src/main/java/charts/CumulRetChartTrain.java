package charts;

import model.RiskModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CumulRetChartTrain extends ApplicationFrame {


    private static final long serialVersionUID = -5947728099201760550L;

    public CumulRetChartTrain( final String title, final List<RiskModel> cumulReturns, final BigDecimal ratio ) {

        super(title);
        final JFreeChart lineChartObject1 = ChartFactory
            .createLineChart(title, "Date", "Cumulative return", createDataset1(cumulReturns, ratio),
                PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel1 = new ChartPanel(lineChartObject1);
        chartPanel1.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel1);

    }

    private CategoryDataset createDataset1( final List<RiskModel> portfolioReturns, final BigDecimal ratio ) {
        final DefaultCategoryDataset line_chart_dataset1 = new DefaultCategoryDataset();

        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final List<RiskModel> firstPart = new ArrayList<>(portfolioReturns.subList(0, splitRatio));

        for ( final RiskModel actual : firstPart ) {
            line_chart_dataset1
                .addValue(actual.getDailyReturn().setScale(4, RoundingMode.HALF_UP), "Return", actual.getDate());

        }

        return line_chart_dataset1;
    }
}
