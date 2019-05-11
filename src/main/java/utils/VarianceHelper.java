package utils;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import model.Share;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class VarianceHelper {
    public static ConvexMultivariateRealFunction[] createInequalities( final int size ) {
        final RealMatrix identity = MatrixUtils.createRealIdentityMatrix(size).scalarMultiply(-1D);

        final ConvexMultivariateRealFunction[] retval = new ConvexMultivariateRealFunction[size];

        for ( int i = 0; i < size; i++ ) {
            retval[i] = new LinearMultivariateRealFunction(identity.getData()[i], 0);
        }

        return retval;
    }

    public static double[][] createMatrix( final int size ) {
        final double[][] retval = new double[1][size];
        for ( int i = 0; i < 1; i++ ) {
            for ( int j = 0; j < size; j++ ) {
                retval[i][j] = 1D;
            }
        }
        return retval;
    }

    public static double[][] getMatrixFromTrainingData( final Map<LocalDate, List<Share>> trainingData ) {
        final double[][] retval =
            new double[trainingData.size()][trainingData.get(trainingData.keySet().toArray()[1]).size()];

        int i = 0;
        int j = 0;
        for ( final Map.Entry<LocalDate, List<Share>> actual : trainingData.entrySet() ) {
            for ( final Share share : actual.getValue() ) {
                retval[i][j] = share.getYield().doubleValue();
                j++;
            }
            i++;
            j = 0;
        }

        return retval;
    }

}
