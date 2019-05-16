package strategies;

import model.Share;
import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigDecimal;
import java.util.List;

public class UniformWeight extends AbstractStrategy {
    private static final String NAME = "UniformWeight";
    private final LinkedMap<String, BigDecimal> weights;

    public UniformWeight( final List<Share> shares ) {
        weights = calculateWeight(shares);
    }

    private LinkedMap<String, BigDecimal> calculateWeight( final List<Share> shares ) {
        final int numberOfShares = shares.size();
        final LinkedMap<String, BigDecimal> retval = new LinkedMap<>();
        shares.forEach(actual -> retval.put(actual.getName(), new BigDecimal(1D / numberOfShares)));
        return retval;

    }

    @Override public LinkedMap<String, BigDecimal> getWeights() {
        return weights;
    }

    @Override public String getName() {
        return NAME;
    }
}
