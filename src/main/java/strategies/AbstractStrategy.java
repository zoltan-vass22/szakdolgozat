package strategies;

import model.ShareYield;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;

public abstract class AbstractStrategy {
    private final Logger log = LogManager.getLogger(AbstractStrategy.class);

    public BigDecimal optimize( final Map<String, ShareYield> trainingData, final Map<String, BigDecimal> weights ) {

        BigDecimal retval = BigDecimal.ZERO;

        for ( final Map.Entry<String, BigDecimal> entry : weights.entrySet() ) {

            retval = retval.add(entry.getValue().multiply(trainingData.get(entry.getKey()).getSumOfYield()));

        }

        //log.info("solution for " + getName() + ": " + retval);

        return retval;
    }

    public abstract LinkedMap<String, BigDecimal> getWeights();

    public abstract String getName();

}
