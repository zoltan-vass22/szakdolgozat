package optimize;

import charts.CumulRetChartTest;
import charts.CumulRetChartTrain;
import model.FileData;
import model.RiskModel;
import model.ShareYield;
import model.TrainingTest;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.ui.RefineryUtilities;
import processor.DailyReturn;
import processor.DataSplitter;
import processor.FileReader;
import processor.RiskMetrics;
import strategies.AbstractStrategy;
import strategies.MaxEV;
import strategies.MinVarMaxEV;
import strategies.MinimumVariance;
import strategies.UniformWeight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OptimizePortfolio {

    public static void main( final String[] args ) throws Exception {
        PropertyConfigurator.configure("src/resources/log4j.properties");
        final Logger log = LogManager.getLogger(OptimizePortfolio.class);

        final Options options = getArgParserOptions();
        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = new HelpFormatter();
        final CommandLine cmd;
        final String[] localArgs =
            new String[] { "--path=C:\\SP500_weekly_2003_2008.csv", "--ratio=40", "--strategy=MinVarMaxEV",
                "--lambda=0" };

        try {
            cmd = parser.parse(options, localArgs);

            final String filepath = cmd.getOptionValue("path");
            final long ratio = Long.parseLong(cmd.getOptionValue("ratio"));
            final String strategy = cmd.getOptionValue("strategy");
            final String lambdaAsString = cmd.getOptionValue("lambda");
            Long lambda = null;

            if ( lambdaAsString != null && lambdaAsString.length() > 0 ) {
                lambda = Long.parseLong(cmd.getOptionValue("lambda"));
            }

            if ( "MinVarMaxEV".equals(strategy) && lambda == null ) {
                throw new ParseException("If strategy is MinVarMaxEV then \"lambda\" is required.");
            }

            final FileReader reader = new FileReader(filepath);

            final FileData data = reader.read();

            final LinkedMap<String, ShareYield> trainingData =
                DataSplitter.sumOfReturns(data.getData(), new BigDecimal(ratio));

            final LinkedMap<String, ShareYield> testData =
                DataSplitter.sumOfReturnsTest(data.getData(), new BigDecimal(ratio));

            final AbstractStrategy strategyToUse;

            switch ( strategy ) {
                case "MaxEv":
                    strategyToUse = new MaxEV(trainingData);
                    break;
                case "MinVar":
                    strategyToUse =
                        new MinimumVariance(DataSplitter.trainingData(data.getData(), new BigDecimal(ratio)));
                    break;
                case "MinVarMaxEV":
                    strategyToUse =
                        new MinVarMaxEV(DataSplitter.trainingData(data.getData(), new BigDecimal(ratio)), trainingData,
                            Objects.requireNonNull(lambda));
                    break;
                case "Uniform":
                    strategyToUse = new UniformWeight(data.getData().getValue(1));
                    break;
                default:
                    throw new ParseException("Strategy type not found");
            }


            strategyToUse.optimize(trainingData, strategyToUse.getWeights());

            final TrainingTest trainingTest = RiskMetrics
                .standardDeviation(DailyReturn.calculateDailyYield(data.getData(), strategyToUse),
                    new BigDecimal(ratio));
            RiskMetrics
                .valueAtRisk(DailyReturn.calculateDailyYield(data.getData(), strategyToUse), new BigDecimal(ratio));
            RiskMetrics.conditionalValueAtRisk(DailyReturn.calculateDailyYield(data.getData(), strategyToUse),
                new BigDecimal(ratio));
            final List<RiskModel> cdfReturns =
                RiskMetrics.cdf(DailyReturn.calculateDailyYield(data.getData(), strategyToUse));

            final List<RiskModel> cumulRetTraining =
                DailyReturn.calculateDailyYieldTraining(data.getData(), strategyToUse, new BigDecimal(ratio));

            final List<RiskModel> cumulRetTest =
                DailyReturn.calculateDailyYieldTest(data.getData(), strategyToUse, new BigDecimal(ratio));


         /*   final BarChart_AWT chart =
                new BarChart_AWT("szoras", "szoras", trainingTest.getTrainingStdDev(), trainingTest.getTestStdDev());
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true); */

          /*  final LineChart_AWT linechart = new LineChart_AWT("CDF", cdfReturns);
            linechart.pack();
            RefineryUtilities.centerFrameOnScreen(linechart);
            linechart.setVisible(true); */

            final CumulRetChartTrain cumulativechart =
                new CumulRetChartTrain("Cumul ret train", cumulRetTraining, new BigDecimal(ratio));
            cumulativechart.pack();
            RefineryUtilities.centerFrameOnScreen(cumulativechart);
            cumulativechart.setVisible(true);


            final CumulRetChartTest cumulativechart2 =
                new CumulRetChartTest("Cumul ret test", cumulRetTest, new BigDecimal(ratio));
            cumulativechart2.pack();
            RefineryUtilities.centerFrameOnScreen(cumulativechart2);
            cumulativechart2.setVisible(true);



        } catch (final ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("Portfolio optimizer ", options);
            System.exit(1);
        }


    }

    private static Options getArgParserOptions() {
        final Option filepath =
            Option.builder("f").longOpt("path").desc("Path of input file").required(true).hasArg(true)
                .type(String.class).valueSeparator().build();

        final Option ratio =
            Option.builder("r").longOpt("ratio").desc("Training / Test data split ratio").required(true).hasArg(true)
                .type(Long.class).valueSeparator().build();

        final Option strategy =
            Option.builder("s").longOpt("strategy").desc("Stategy to use").required(true).hasArg(true)
                .type(String.class).valueSeparator().build();

        final Option lambda =
            Option.builder("l").longOpt("lambda").desc("Lambda").required(false).hasArg(true).type(Long.class)
                .valueSeparator().build();

        return new Options().addOption(filepath).addOption(ratio).addOption(strategy).addOption(lambda);
    }

}
