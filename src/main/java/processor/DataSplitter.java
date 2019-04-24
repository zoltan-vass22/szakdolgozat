package processor;

import model.Share;
import model.SplitData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSplitter {

    public static Map<String,SplitData> sumOfReturns(Map<LocalDate, List<Share>> allShares, BigDecimal ratio) {
        final Map<LocalDate, List<Share>> trainingData = trainingData(allShares,ratio);
        final Map<String,SplitData> retVal = new LinkedHashMap<>();

        for (Map.Entry<LocalDate, List<Share>> shares : trainingData.entrySet()) {
            for (Share actualShare: shares.getValue()) {

                if (!retVal.containsKey(actualShare.getName())) {
                    retVal.put(actualShare.getName(), SplitData.builder().name(actualShare.getName()).sumOfYield(actualShare.getYield()).build());

                }else {
                    final SplitData localData = retVal.get(actualShare.getName());
                    localData.setSumOfYield(localData.getSumOfYield().add(actualShare.getYield()));
                    retVal.put(actualShare.getName(),localData);
                }
            }
        }
        return retVal;
    }

    private static Map<LocalDate, List<Share>> trainingData(Map<LocalDate, List<Share>> allShares, BigDecimal ratio) {

        final Map<LocalDate, List<Share>> retVal = new LinkedHashMap<>();
        final int trainingDataNum = ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(allShares.size())).setScale(0, RoundingMode.HALF_UP).intValue();
        int index = 0;
        for (Map.Entry<LocalDate, List<Share>> shares : allShares.entrySet()){

            if (index == trainingDataNum){
                break;
            }
            retVal.put(shares.getKey(),shares.getValue());
            index++;

        }

        return retVal;
    }


}
