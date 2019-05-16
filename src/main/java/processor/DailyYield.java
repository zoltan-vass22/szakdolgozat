package processor;

import model.RiskModel;
import model.Share;
import model.ShareYield;
import org.apache.commons.collections4.map.LinkedMap;
import strategies.AbstractStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyYield {

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

}
