package optimize;

import charts.BarChart_AWT;
import charts.LineChart_AWT;
import model.FileData;
import model.RiskModel;
import model.ShareReturn;
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
            new String[] { "--path=C:\\SP500_weekly_2003_2008.csv", "--ratio=75", "--strategy=MinVarMaxEv",
                "--lambda=0.5", "--alpha=5" };

        try {
            cmd = parser.parse(options, localArgs);

            final String filepath = cmd.getOptionValue("path");
            final long ratio = Long.parseLong(cmd.getOptionValue("ratio"));
            final String strategy = cmd.getOptionValue("strategy");
            final String lambdaAsString = cmd.getOptionValue("lambda");
            Double lambda = null;
            final Long alpha = Long.parseLong(cmd.getOptionValue("alpha"));

            if ( lambdaAsString != null && lambdaAsString.length() > 0 ) {
                lambda = Double.parseDouble(lambdaAsString);
            }

            if ( "MinVarMaxEV".equals(strategy) && lambda == null ) {
                throw new ParseException("If strategy is MinVarMaxEV then \"lambda\" is required.");
            }

            final FileReader reader = new FileReader(filepath);

            final FileData data = reader.read();

            final LinkedMap<String, ShareReturn> trainingData =
                DataSplitter.sumOfReturns(data.getData(), new BigDecimal(ratio));

            final AbstractStrategy strategyToUse;

            switch ( strategy ) {
                case "MaxEv":
                    strategyToUse = new MaxEV(trainingData);
                    break;
                case "MinVar":
                    strategyToUse =
                        new MinimumVariance(DataSplitter.trainingData(data.getData(), new BigDecimal(ratio)));
                    break;
                case "MinVarMaxEv":
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

            final TrainingTest trainingTestStdDev = RiskMetrics
                .standardDeviation(DailyReturn.calculateDailyReturn(data.getData(), strategyToUse),
                    new BigDecimal(ratio));

            final TrainingTest trainingTestVaR = RiskMetrics
                .valueAtRisk(DailyReturn.calculateDailyReturn(data.getData(), strategyToUse), new BigDecimal(alpha),
                    new BigDecimal(ratio));

            final TrainingTest trainingTestCVaR = RiskMetrics
                .conditionalValueAtRisk(DailyReturn.calculateDailyReturn(data.getData(), strategyToUse),
                    new BigDecimal(alpha), new BigDecimal(ratio));

            final RiskModel cdf =
                RiskMetrics.cdf(DailyReturn.calculateDailyReturn(data.getData(), strategyToUse), new BigDecimal(ratio));

            final BarChart_AWT chartStdDev =
                new BarChart_AWT("Standard Deviation", "Standard Deviation", trainingTestStdDev.getTraining(),
                    trainingTestStdDev.getTest(), "Standard Deviation", "Training data", "Test Data");
            chartStdDev.pack();
            RefineryUtilities.centerFrameOnScreen(chartStdDev);
            chartStdDev.setVisible(true);

            final BarChart_AWT chartVaR =
                new BarChart_AWT("VaR", "VaR", trainingTestVaR.getTraining(), trainingTestVaR.getTest(), "VaR",
                    "Training data", "Test Data");
            chartVaR.pack();
            RefineryUtilities.centerFrameOnScreen(chartVaR);
            chartVaR.setVisible(true);

            final BarChart_AWT chartCVaR =
                new BarChart_AWT("CVaR", "CVaR", trainingTestCVaR.getTraining(), trainingTestCVaR.getTest(), "CVaR",
                    "Training data", "Test Data");
            chartCVaR.pack();
            RefineryUtilities.centerFrameOnScreen(chartCVaR);
            chartCVaR.setVisible(true);

            final LineChart_AWT linechartCDFTraining =
                new LineChart_AWT("CDF Training", cdf.getTraining(), true, "Returns", "CDF value", " CDF value");
            linechartCDFTraining.pack();
            RefineryUtilities.centerFrameOnScreen(linechartCDFTraining);
            linechartCDFTraining.setVisible(true);

            final LineChart_AWT linechartCDFTest =
                new LineChart_AWT("CDF Test", cdf.getTest(), true, "Returns", "CDF value", " CDF value");
            linechartCDFTest.pack();
            RefineryUtilities.centerFrameOnScreen(linechartCDFTest);
            linechartCDFTest.setVisible(true);

            final RiskModel rm =
                DailyReturn.calculateAggregatedDailyReturn(data.getData(), strategyToUse, new BigDecimal(ratio));

            final LineChart_AWT linechartCumulRetTraining =
                new LineChart_AWT("Cumulative Return Training", rm.getTraining(), false, "Date", "Cumulative return",
                    "Cumulative Return");
            linechartCumulRetTraining.pack();
            RefineryUtilities.centerFrameOnScreen(linechartCumulRetTraining);
            linechartCumulRetTraining.setVisible(true);

            final LineChart_AWT linechartCumulRetTest =
                new LineChart_AWT("Cumulative Return Test", rm.getTest(), false, "Date", "Cumulative return",
                    "Cumulative Return");
            linechartCumulRetTest.pack();
            RefineryUtilities.centerFrameOnScreen(linechartCumulRetTest);
            linechartCumulRetTest.setVisible(true);


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

        final Option alpha =
            Option.builder("a").longOpt("alpha").desc("Alpha").required(true).hasArg(true).type(Long.class)
                .valueSeparator().build();

        return new Options().addOption(filepath).addOption(ratio).addOption(strategy).addOption(lambda)
            .addOption(alpha);
    }

}
