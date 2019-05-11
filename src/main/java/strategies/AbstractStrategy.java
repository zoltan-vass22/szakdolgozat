package strategies;

import model.SplitData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;

public class AbstractStrategy {
    private final Logger log = LogManager.getLogger(AbstractStrategy.class);

    public BigDecimal optimize( final Map<String, SplitData> trainingData, final Map<String, BigDecimal> weights,
        final String strategyName ) {

        BigDecimal retval = BigDecimal.ZERO;

        for ( final Map.Entry<String, BigDecimal> entry : weights.entrySet() ) {

            retval = retval.add(entry.getValue().multiply(trainingData.get(entry.getKey()).getSumOfYield()));

        }

        log.info("solution for " + strategyName + ": " + retval);

        return retval;
    }
}
