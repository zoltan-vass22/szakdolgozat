package strategies;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;
import model.Share;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import utils.CovarianceHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MinimumVariance extends AbstractStrategy {
    private static final String NAME = "MinimumVariance";
    private final DoubleFactory2D f2 = DoubleFactory2D.dense;
    private final LinkedMap<String, BigDecimal> weights;

    public MinimumVariance( final LinkedMap<LocalDate, List<Share>> trainingData ) throws Exception {
        weights = calculateWeight(trainingData);
    }

    private LinkedMap<String, BigDecimal> calculateWeight( final LinkedMap<LocalDate, List<Share>> trainingData )
        throws JOptimizerException {
        final LinkedMap<String, BigDecimal> retval = new LinkedMap<>();

        final RealMatrix covMatrix =
            new Covariance(MatrixUtils.createRealMatrix(CovarianceHelper.getMatrixFromTrainingData(trainingData)))
                .getCovarianceMatrix();
        final DoubleMatrix2D hMatrix = f2.make(covMatrix.getData());

        final PDQuadraticMultivariateRealFunction objectiveFunction =
            new PDQuadraticMultivariateRealFunction(hMatrix.toArray(), null, 0);

        //equalitites
        final double[][] a = CovarianceHelper.createMatrix(hMatrix.rows());
        final double[] b = new double[] { 1 };

        //inequalities
        final ConvexMultivariateRealFunction[] inequalities = CovarianceHelper.createInequalities(hMatrix.rows());

        final OptimizationRequest oR = new OptimizationRequest();
        oR.setF0(objectiveFunction);
        oR.setFi(inequalities);
        oR.setA(a);
        oR.setB(b);
        oR.setToleranceFeas(1.0E-12);
        oR.setTolerance(1.0E-12);

        final JOptimizer opt = new JOptimizer();
        opt.setOptimizationRequest(oR);

        opt.optimize();

        final OptimizationResponse response = opt.getOptimizationResponse();
        int i = 0;
        final double[] solution = response.getSolution();

        for ( final Map.Entry<LocalDate, List<Share>> data : trainingData.entrySet() ) {
            for ( final Share share : data.getValue() ) {
                retval.put(share.getName(), new BigDecimal(solution[i++]));

            }
            break;
        }

        return retval;
    }

    @Override public LinkedMap<String, BigDecimal> getWeights() {
        return weights;
    }

    @Override public String getName() {
        return NAME;
    }
}
