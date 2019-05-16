package processor;

import model.RiskModel;
import model.TrainingTest;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RiskMetrics {
    private static final Logger log = LogManager.getLogger(RiskMetrics.class);

    public static TrainingTest standardDeviation( final List<RiskModel> portfolioReturns, final BigDecimal ratio ) {
        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final List<RiskModel> firstPart = new ArrayList<>(portfolioReturns.subList(0, splitRatio));
        final List<RiskModel> secondPart =
            new ArrayList<>(portfolioReturns.subList(splitRatio, portfolioReturns.size()));

        final double[] firstPartAsDouble =
            firstPart.stream().mapToDouble(item -> item.getDailyReturn().doubleValue()).toArray();
        final double[] secondPartAsDouble =
            secondPart.stream().mapToDouble(item -> item.getDailyReturn().doubleValue()).toArray();

        final StandardDeviation firstPartStdDeviation = new StandardDeviation(false);
        final StandardDeviation secondPartStdDeviation = new StandardDeviation(false);
        log.info("First part std dev: " + firstPartStdDeviation.evaluate(firstPartAsDouble) + " Second part std dev: "
            + secondPartStdDeviation.evaluate(secondPartAsDouble));

        return TrainingTest.builder().trainingStdDev(firstPartStdDeviation.evaluate(firstPartAsDouble))
            .testStdDev(secondPartStdDeviation.evaluate(secondPartAsDouble)).build();
    }

    public static void valueAtRisk( final List<RiskModel> portfolioReturns, final BigDecimal alpha ) {
        final List<RiskModel> localPortfolioReturn = new ArrayList<>(portfolioReturns);

        final int splitRatio =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        localPortfolioReturn.sort(Comparator.comparing(RiskModel::getDailyReturn));

        log.info("VaR: " + localPortfolioReturn.get(splitRatio));
    }

    public static void conditionalValueAtRisk( final List<RiskModel> portfolioReturns, final BigDecimal alpha ) {
        final List<RiskModel> localPortfolioReturn = new ArrayList<>(portfolioReturns);

        final int splitRatio =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        localPortfolioReturn.sort(Comparator.comparing(RiskModel::getDailyReturn));

        final List<RiskModel> part = new ArrayList<>(localPortfolioReturn.subList(0, splitRatio));

        final double[] partAsDouble = part.stream().mapToDouble(item -> item.getDailyReturn().doubleValue()).toArray();

        log.info("CVaR: " + StatUtils.mean(partAsDouble));
    }

    public static List<RiskModel> cdf( final List<RiskModel> portfolioReturns ) {
        final List<RiskModel> localPortfolioReturn = new ArrayList<>(portfolioReturns);

        localPortfolioReturn.sort(Comparator.comparing(RiskModel::getDailyReturn));

        for ( int i = 1; i <= localPortfolioReturn.size(); i++ ) {

            localPortfolioReturn.get(i - 1)
                .setCentralDistribution(new BigDecimal((double) i / localPortfolioReturn.size()));
        }

        return localPortfolioReturn;
    }

}
