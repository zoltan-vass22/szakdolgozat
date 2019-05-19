package processor;

import model.RiskModel;
import model.Share;
import model.ShareYield;
import org.apache.commons.collections4.map.LinkedMap;
import strategies.AbstractStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyReturn {

    public static List<RiskModel> calculateDailyYield( final LinkedMap<LocalDate, List<Share>> allShares,
        final AbstractStrategy strategy ) {

        final List<LinkedMap<LocalDate, List<Share>>> dailyPortfolioList = new ArrayList<>();

        for ( int i = 1; i < allShares.size(); i++ ) {
            final LinkedMap<LocalDate, List<Share>> mapToList = new LinkedMap<>();
            for ( int j = 0; j <= i; j++ ) {
                mapToList.put(allShares.get(j), allShares.getValue(j));
            }
            dailyPortfolioList.add(mapToList);
        }

        final List<LinkedMap<String, ShareYield>> dailyAggregatedPortfolioList = new ArrayList<>();

        dailyPortfolioList.forEach(actualMap -> dailyAggregatedPortfolioList.add(DataSplitter.sumOfReturns(actualMap)));

        final List<RiskModel> models = new ArrayList<>();

        for ( int i = 1; i < allShares.size(); i++ ) {
            models.add(RiskModel.builder()
                .dailyReturn(strategy.optimize(dailyAggregatedPortfolioList.get(i - 1), strategy.getWeights()))
                .date(allShares.get(i)).build());
        }
        return models;
    }

    public static List<RiskModel> calculateDailyYieldTraining( final LinkedMap<LocalDate, List<Share>> allShares,
        final AbstractStrategy strategy, final BigDecimal ratio ) {

        final List<LinkedMap<LocalDate, List<Share>>> dailyPortfolioList = new ArrayList<>();

        for ( int i = 1; i < allShares.size(); i++ ) {
            final LinkedMap<LocalDate, List<Share>> mapToList = new LinkedMap<>();
            for ( int j = 0; j <= i; j++ ) {
                mapToList.put(allShares.get(j), allShares.getValue(j));
            }
            dailyPortfolioList.add(mapToList);
        }

        final List<LinkedMap<String, ShareYield>> dailyAggregatedPortfolioList = new ArrayList<>();

        dailyPortfolioList.forEach(actualMap -> dailyAggregatedPortfolioList.add(DataSplitter.sumOfReturns(actualMap)));

        final List<RiskModel> models = new ArrayList<>();

        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(allShares.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        for ( int i = 1; i < splitRatio; i++ ) {
            models.add(RiskModel.builder()
                .dailyReturn(strategy.optimize(dailyAggregatedPortfolioList.get(i - 1), strategy.getWeights()))
                .date(allShares.get(i)).build());
        }
        return models;
    }

    public static List<RiskModel> calculateDailyYieldTest( final LinkedMap<LocalDate, List<Share>> allShares,
        final AbstractStrategy strategy, final BigDecimal ratio ) {

        final List<LinkedMap<LocalDate, List<Share>>> dailyPortfolioList = new ArrayList<>();

        final int splitRatio =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(allShares.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();

        for ( int i = splitRatio; i < allShares.size(); i++ ) {
            final LinkedMap<LocalDate, List<Share>> mapToList = new LinkedMap<>();

            for ( int j = splitRatio; j <= i; j++ ) {
                mapToList.put(allShares.get(i), allShares.getValue(i));
            }

            dailyPortfolioList.add(mapToList);
        }

        final List<LinkedMap<String, ShareYield>> dailyAggregatedPortfolioList = new ArrayList<>();

        dailyPortfolioList
            .forEach(actualMap -> dailyAggregatedPortfolioList.add(DataSplitter.sumOfReturnsTest(actualMap)));

        final List<RiskModel> models = new ArrayList<>();

        for ( int i = 1; i < dailyAggregatedPortfolioList.size(); i++ ) {
            models.add(RiskModel.builder()
                .dailyReturn(strategy.optimize(dailyAggregatedPortfolioList.get(i - 1), strategy.getWeights()))
                .date(allShares.get((splitRatio + i - 1))).build());
        }
        return models;
    }

}
