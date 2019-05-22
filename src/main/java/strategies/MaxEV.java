package strategies;

import model.ShareReturn;
import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class MaxEV extends AbstractStrategy {
    private static final String NAME = "MaxEv";
    private final LinkedMap<String, BigDecimal> weights;

    public MaxEV( final LinkedMap<String, ShareReturn> shares ) {
        weights = calculateWeight(shares);
    }

    private LinkedMap<String, BigDecimal> calculateWeight( final LinkedMap<String, ShareReturn> shares ) {
        final LinkedMap<String, BigDecimal> retval = new LinkedMap<>();
        shares.entrySet().forEach(actual -> retval.put(actual.getValue().getName(), BigDecimal.ZERO));

        final Optional<Map.Entry<String, ShareReturn>> maxentry = shares.entrySet().stream()
            .max(Comparator.comparing(( Map.Entry<String, ShareReturn> e ) -> e.getValue().getSumOfYield()));
        retval.put(maxentry.get().getKey(), BigDecimal.ONE);
        return retval;
    }

    @Override public LinkedMap<String, BigDecimal> getWeights() {
        return weights;
    }

    @Override public String getName() {
        return NAME;
    }
}
