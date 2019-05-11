import model.FileData;
import model.SplitData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import processor.DataSplitter;
import processor.FileReader;
import strategies.MaxEV;
import strategies.MinVarMaxEV;
import strategies.MinimumVariance;
import strategies.UniformWeight;

import java.math.BigDecimal;
import java.util.Map;

public class Test {

    public static void main( final String[] args ) throws Exception {
        PropertyConfigurator.configure("src/resources/log4j.properties");
        final Logger log = LogManager.getLogger(Test.class);

        final FileReader reader = new FileReader("C:\\SP500_weekly_2003_2008.csv");

        final FileData data = reader.read();

        final StringBuilder sb = new StringBuilder();

        final Map<String, SplitData> trainingData = DataSplitter.sumOfReturns(data.getData(), new BigDecimal(40));

        for ( final Map.Entry<String, SplitData> sumOfReturn : trainingData.entrySet() ) {
            sb.append(sumOfReturn.getKey()).append(":").append(sumOfReturn.getValue().getSumOfYield()).append("|");
        }

        log.info(sb.toString());

        final UniformWeight uniform = new UniformWeight(data.getData().get(data.getData().keySet().toArray()[1]));
        final MaxEV maxev = new MaxEV(trainingData);
        final MinimumVariance minvar =
            new MinimumVariance(DataSplitter.trainingData(data.getData(), new BigDecimal(40)));
        final MinVarMaxEV minvarev =
            new MinVarMaxEV(DataSplitter.trainingData(data.getData(), new BigDecimal(40)), trainingData, 0);

        uniform.optimize(trainingData, uniform.getWeights(), "uniform");
        maxev.optimize(trainingData, maxev.getWeights(), "maxev");
        minvar.optimize(trainingData, minvar.getWeights(), "minvar");
        minvarev.optimize(trainingData, minvarev.getWeights(), "minvarev");

    }

}
