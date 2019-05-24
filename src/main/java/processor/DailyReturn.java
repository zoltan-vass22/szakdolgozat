package processor;

import model.RiskModel;
import model.RiskModelListItem;
import model.Share;
import model.ShareReturn;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import strategies.AbstractStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyReturn {
    private static final Logger log = LogManager.getLogger(DailyReturn.class);

    public static LinkedMap<LocalDate, BigDecimal> calculateDailyReturn(
        final LinkedMap<LocalDate, List<Share>> allShares, final AbstractStrategy strategy ) {
        return strategy.optimizeDaily(allShares, strategy.getWeights());
    }

    public static RiskModel calculateAggregatedDailyReturn( final LinkedMap<LocalDate, List<Share>> allShares,
        final AbstractStrategy strategy, final BigDecimal ratio ) {

        final LinkedMap<LocalDate, List<Share>> firstPart = new LinkedMap<>();
        final LinkedMap<LocalDate, List<Share>> secondPart = new LinkedMap<>();

        split(allShares, ratio, firstPart, secondPart);

        final List<LinkedMap<LocalDate, List<Share>>> dailyPortfolioListFirst = new ArrayList<>();
        final List<LinkedMap<LocalDate, List<Share>>> dailyPortfolioListSecond = new ArrayList<>();

        for ( int i = 1; i < firstPart.size(); i++ ) {
            final LinkedMap<LocalDate, List<Share>> mapToList = new LinkedMap<>();
            for ( int j = 0; j <= i; j++ ) {
                mapToList.put(firstPart.get(j), firstPart.getValue(j));
            }
            dailyPortfolioListFirst.add(mapToList);
        }

        for ( int i = 1; i < secondPart.size(); i++ ) {
            final LinkedMap<LocalDate, List<Share>> mapToList = new LinkedMap<>();
            for ( int j = 0; j <= i; j++ ) {
                mapToList.put(secondPart.get(j), secondPart.getValue(j));
            }
            dailyPortfolioListSecond.add(mapToList);
        }

        final List<LinkedMap<String, ShareReturn>> dailyAggregatedPortfolioListFirst = new ArrayList<>();
        final List<LinkedMap<String, ShareReturn>> dailyAggregatedPortfolioListSecond = new ArrayList<>();

        dailyPortfolioListFirst
            .forEach(actualMap -> dailyAggregatedPortfolioListFirst.add(DataSplitter.sumOfReturns(actualMap)));
        dailyPortfolioListSecond
            .forEach(actualMap -> dailyAggregatedPortfolioListSecond.add(DataSplitter.sumOfReturns(actualMap)));

        final List<RiskModelListItem> modelsFirst = new ArrayList<>();

        for ( int i = 1; i <= dailyAggregatedPortfolioListFirst.size(); i++ ) {
            modelsFirst.add(RiskModelListItem.builder()
                .dailyReturn(strategy.optimize(dailyAggregatedPortfolioListFirst.get(i - 1), strategy.getWeights()))
                .date(firstPart.get(i)).build());
        }

        final List<RiskModelListItem> modelsSecond = new ArrayList<>();

        for ( int i = 1; i <= dailyAggregatedPortfolioListSecond.size(); i++ ) {
            modelsSecond.add(RiskModelListItem.builder().dailyReturn(
                strategy.optimize(dailyAggregatedPortfolioListSecond.get(i - 1), strategy.getWeights())
                    .add(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP)).date(secondPart.get(i)).build());
        }

        log.info("Training data cumulative return: " + modelsFirst.get(modelsFirst.size() - 1));
        log.info("Test data cumulative return: " + modelsSecond.get(modelsSecond.size() - 1));

        return RiskModel.builder().training(modelsFirst).test(modelsSecond).build();
    }

    private static void split( final LinkedMap<LocalDate, List<Share>> allshares, final BigDecimal ratio,
        final LinkedMap<LocalDate, List<Share>> firstPart, final LinkedMap<LocalDate, List<Share>> secondPart ) {
        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(allshares.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        for ( int i = 0; i < allshares.size(); i++ ) {
            if ( i < splitRatio ) {
                firstPart.put(allshares.get(i), allshares.getValue(i));
            } else {
                secondPart.put(allshares.get(i), allshares.getValue(i));
            }
        }

    }

}
