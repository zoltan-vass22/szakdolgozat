package processor;

import model.RiskModel;
import model.RiskModelListItem;
import model.TrainingTest;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RiskMetrics {
    private static final Logger log = LogManager.getLogger(RiskMetrics.class);

    public static TrainingTest standardDeviation( final LinkedMap<LocalDate, BigDecimal> portfolioReturns,
        final BigDecimal ratio ) {


        final LinkedMap<LocalDate, BigDecimal> firstPart = new LinkedMap<>();
        final LinkedMap<LocalDate, BigDecimal> secondPart = new LinkedMap<>();

        split(portfolioReturns, ratio, firstPart, secondPart);

        final double[] firstPartAsDouble = transformListToArray(firstPart);

        final double[] secondPartAsDouble = transformListToArray(secondPart);

        final StandardDeviation firstPartStdDeviation = new StandardDeviation(false);
        final StandardDeviation secondPartStdDeviation = new StandardDeviation(false);

        final BigDecimal firstPartValue =
            new BigDecimal(firstPartStdDeviation.evaluate(firstPartAsDouble)).setScale(4, RoundingMode.HALF_UP);

        final BigDecimal secondPartValue =
            new BigDecimal(secondPartStdDeviation.evaluate(secondPartAsDouble)).setScale(4, RoundingMode.HALF_UP);

        log.info("First part std dev: " + firstPartValue + " Second part std dev: " + secondPartValue);

        return TrainingTest.builder().training(firstPartValue).test(secondPartValue).build();
    }

    public static TrainingTest valueAtRisk( final LinkedMap<LocalDate, BigDecimal> portfolioReturns,
        final BigDecimal alpha, final BigDecimal ratio ) {
        final LinkedMap<LocalDate, BigDecimal> firstPart = new LinkedMap<>();
        final LinkedMap<LocalDate, BigDecimal> secondPart = new LinkedMap<>();

        split(portfolioReturns, ratio, firstPart, secondPart);

        final LinkedMap<LocalDate, BigDecimal> firstPartSorted =
            firstPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final LinkedMap<LocalDate, BigDecimal> secondPartSorted =
            secondPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final int alphaRatioFirst =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(firstPartSorted.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final int alphaRatioSecond =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(secondPartSorted.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();


        log.info("First part VaR: " + firstPartSorted.getValue(alphaRatioFirst).setScale(4, RoundingMode.HALF_UP)
            + " Second part VaR: " + secondPartSorted.getValue(alphaRatioSecond).setScale(4, RoundingMode.HALF_UP));

        return TrainingTest.builder()
            .training(firstPartSorted.getValue(alphaRatioFirst).setScale(4, RoundingMode.HALF_UP))
            .test(secondPartSorted.getValue(alphaRatioSecond).setScale(4, RoundingMode.HALF_UP)).build();
    }


    public static TrainingTest conditionalValueAtRisk( final LinkedMap<LocalDate, BigDecimal> portfolioReturns,
        final BigDecimal alpha, final BigDecimal ratio ) {

        final LinkedMap<LocalDate, BigDecimal> firstPart = new LinkedMap<>();
        final LinkedMap<LocalDate, BigDecimal> secondPart = new LinkedMap<>();

        split(portfolioReturns, ratio, firstPart, secondPart);

        final LinkedMap<LocalDate, BigDecimal> firstPartSorted =
            firstPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final LinkedMap<LocalDate, BigDecimal> secondPartSorted =
            secondPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final int alphaRatioFirst =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(firstPartSorted.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final int alphaRatioSecond =
            alpha.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(secondPartSorted.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        final double[] firstPartAsDouble = transformListToArray(firstPartSorted, alphaRatioFirst);
        final double[] secondPartAsDouble = transformListToArray(secondPartSorted, alphaRatioSecond);

        final BigDecimal firstPartCVaR =
            new BigDecimal(StatUtils.mean(firstPartAsDouble)).setScale(4, RoundingMode.HALF_UP);
        final BigDecimal secondPartCVaR =
            new BigDecimal(StatUtils.mean(secondPartAsDouble)).setScale(4, RoundingMode.HALF_UP);

        log.info("First part CVaR: " + firstPartCVaR + " Second part CVaR: " + secondPartCVaR);

        return TrainingTest.builder().training(firstPartCVaR).test(secondPartCVaR).build();
    }

    private static double[] transformListToArray( final LinkedMap<LocalDate, BigDecimal> values ) {
        final double[] retval = new double[values.values().size()];
        int i = 0;
        for ( final Map.Entry<LocalDate, BigDecimal> actual : values.entrySet() ) {
            retval[i++] = actual.getValue().doubleValue();
        }
        return retval;
    }

    private static double[] transformListToArray( final LinkedMap<LocalDate, BigDecimal> values, final int size ) {
        final double[] retval = new double[size];

        for ( int i = 0; i < size; i++ ) {
            retval[i] = values.getValue(i).doubleValue();
        }

        return retval;
    }

    private static void split( final LinkedMap<LocalDate, BigDecimal> portfolioReturns, final BigDecimal ratio,
        final LinkedMap<LocalDate, BigDecimal> firstPart, final LinkedMap<LocalDate, BigDecimal> secondPart ) {
        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(portfolioReturns.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        for ( int i = 0; i < portfolioReturns.size(); i++ ) {
            if ( i < splitRatio ) {
                firstPart.put(portfolioReturns.get(i), portfolioReturns.getValue(i));
            } else {
                secondPart.put(portfolioReturns.get(i), portfolioReturns.getValue(i));
            }
        }

    }

    public static RiskModel cdf( final LinkedMap<LocalDate, BigDecimal> portfolioReturns, final BigDecimal ratio ) {
        final List<RiskModelListItem> retValTraining = new ArrayList<>();
        final List<RiskModelListItem> retValTest = new ArrayList<>();

        final LinkedMap<LocalDate, BigDecimal> firstPart = new LinkedMap<>();
        final LinkedMap<LocalDate, BigDecimal> secondPart = new LinkedMap<>();

        split(portfolioReturns, ratio, firstPart, secondPart);

        final LinkedMap<LocalDate, BigDecimal> firstPartSorted =
            firstPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final LinkedMap<LocalDate, BigDecimal> secondPartSorted =
            secondPart.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final Set<BigDecimal> filteredFirstPartExisting = new HashSet<>();
        final LinkedMap<LocalDate, BigDecimal> filteredFirstPart =
            firstPartSorted.entrySet().stream().filter(entry -> filteredFirstPartExisting.add(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        final Set<BigDecimal> filteredSecondPartExisting = new HashSet<>();
        final LinkedMap<LocalDate, BigDecimal> filteredSecondPart =
            secondPartSorted.entrySet().stream().filter(entry -> filteredSecondPartExisting.add(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedMap::new));

        for ( int i = 1; i <= filteredFirstPart.size(); i++ ) {
            final RiskModelListItem riskModelListItem = RiskModelListItem.builder().date(filteredFirstPart.get(i - 1))
                .dailyReturn(filteredFirstPart.getValue(i - 1)).centralDistribution(new BigDecimal(
                    (double) getCount(firstPartSorted, filteredFirstPart.getValue(i - 1)) / firstPartSorted.size())
                    .setScale(4, RoundingMode.HALF_UP)).build();
            retValTraining.add(riskModelListItem);
        }

        for ( int i = 1; i <= filteredSecondPart.size(); i++ ) {
            final RiskModelListItem riskModelListItem = RiskModelListItem.builder().date(filteredSecondPart.get(i - 1))
                .dailyReturn(filteredSecondPart.getValue(i - 1)).centralDistribution(new BigDecimal(
                    (double) getCount(secondPartSorted, filteredSecondPart.getValue(i - 1)) / secondPartSorted.size())
                    .setScale(4, RoundingMode.HALF_UP)).build();
            retValTest.add(riskModelListItem);
            //log.info(getCount(secondPartSorted, filteredSecondPart.getValue(i - 1)));
        }

        return RiskModel.builder().training(retValTraining).test(retValTest).build();
    }

    private static int getCount( final LinkedMap<LocalDate, BigDecimal> returns, final BigDecimal value ) {
        int counter = 0;

        for ( final Map.Entry<LocalDate, BigDecimal> actual : returns.entrySet() ) {
            if ( actual.getValue().compareTo(value) <= 0 ) {
                counter++;
            }

        }
        return counter;
    }



}
