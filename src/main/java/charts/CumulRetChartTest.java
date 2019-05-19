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

public class CumulRetChartTest extends ApplicationFrame {

    private static final long serialVersionUID = -9196870830748359746L;

    public CumulRetChartTest( final String title, final List<RiskModel> cumulReturns, final BigDecimal ratio ) {

        super(title);

        final JFreeChart lineChartObject2 = ChartFactory
            .createLineChart(title, "Date", "Cumulative return", createDataset2(cumulReturns, ratio),
                PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel2 = new ChartPanel(lineChartObject2);
        chartPanel2.setPreferredSize(new java.awt.Dimension(700, 500));
        setContentPane(chartPanel2);
    }

    private CategoryDataset createDataset2( final List<RiskModel> portfolioReturns, final BigDecimal ratio ) {
        final DefaultCategoryDataset line_chart_dataset2 = new DefaultCategoryDataset();

        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final List<RiskModel> secondPart =
            new ArrayList<>(portfolioReturns.subList(splitRatio, portfolioReturns.size()));


        for ( final RiskModel actual : secondPart ) {
            line_chart_dataset2
                .addValue(actual.getDailyReturn().setScale(4, RoundingMode.HALF_UP), "Return", actual.getDate());

        }
        return line_chart_dataset2;
    }
}
