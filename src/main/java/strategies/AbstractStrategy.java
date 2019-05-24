package strategies;

import model.Share;
import model.ShareReturn;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class AbstractStrategy {
    private final Logger log = LogManager.getLogger(AbstractStrategy.class);

    public BigDecimal optimize( final Map<String, ShareReturn> trainingData, final Map<String, BigDecimal> weights ) {

        BigDecimal retval = BigDecimal.ZERO;

        for ( final Map.Entry<String, BigDecimal> entry : weights.entrySet() ) {

            retval = retval.add(entry.getValue().multiply(trainingData.get(entry.getKey()).getSumOfReturn()))
                .setScale(4, RoundingMode.HALF_UP);

        }

        //log.info("solution for " + getName() + ": " + retval);

        return retval;
    }

    public LinkedMap<LocalDate, BigDecimal> optimizeDaily( final LinkedMap<LocalDate, List<Share>> allShares,
        final Map<String, BigDecimal> weights ) {

        final LinkedMap<LocalDate, BigDecimal> retval = new LinkedMap<>();

        for ( final Map.Entry<LocalDate, List<Share>> actual : allShares.entrySet() ) {

            retval.put(actual.getKey(), optimize(transformToMap(weights, actual.getValue()), weights));

        }

        return retval;
    }

    private Map<String, ShareReturn> transformToMap( final Map<String, BigDecimal> weights, final List<Share> shares ) {
        final Map<String, ShareReturn> retVal = new LinkedMap<>();

        int i = 0;
        for ( final Map.Entry<String, BigDecimal> weight : weights.entrySet() ) {
            retVal.put(weight.getKey(),
                ShareReturn.builder().name(weight.getKey()).sumOfReturn(shares.get(i++).getYield()).build());
        }

        return retVal;
    }

    public abstract LinkedMap<String, BigDecimal> getWeights();

    public abstract String getName();

}
