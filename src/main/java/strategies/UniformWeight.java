package strategies;

import model.Share;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UniformWeight extends AbstractStrategy {
    private final Map<String, BigDecimal> weights;

    public UniformWeight( final List<Share> shares ) {
        weights = calculateWeight(shares);
    }

    private Map<String, BigDecimal> calculateWeight( final List<Share> shares ) {
        final int numberOfShares = shares.size();
        final Map<String, BigDecimal> retval = new LinkedHashMap<>();
        shares.forEach(actual -> retval.put(actual.getName(), new BigDecimal(1D / numberOfShares)));
        return retval;

    }

    public Map<String, BigDecimal> getWeights() {
        return weights;
    }
}
