package strategies;

import model.SplitData;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MaxEV extends AbstractStrategy {

    private final Map<String, BigDecimal> weights;

    public MaxEV( final Map<String, SplitData> shares ) {
        weights = calculateWeight(shares);
    }

    private Map<String, BigDecimal> calculateWeight( final Map<String, SplitData> shares ) {
        final Map<String, BigDecimal> retval = new LinkedHashMap<>();
        shares.entrySet().forEach(actual -> retval.put(actual.getValue().getName(), BigDecimal.ZERO));

        final Optional<Map.Entry<String, SplitData>> maxentry = shares.entrySet().stream()
            .max(Comparator.comparing(( Map.Entry<String, SplitData> e ) -> e.getValue().getSumOfYield()));
        retval.put(maxentry.get().getKey(), BigDecimal.ONE);
        return retval;
    }

    public Map<String, BigDecimal> getWeights() {
        return weights;
    }
}
