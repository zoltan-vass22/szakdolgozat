package processor;

import model.Share;
import model.ShareReturn;
import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DataSplitter {

    static LinkedMap<String, ShareReturn> sumOfReturns( final LinkedMap<LocalDate, List<Share>> allShares ) {
        return sumOfReturns(allShares, null);
    }

    public static LinkedMap<String, ShareReturn> sumOfReturns( final LinkedMap<LocalDate, List<Share>> allShares,
        final BigDecimal ratio ) {

        final LinkedMap<LocalDate, List<Share>> trainingData;
        if ( ratio == null ) {
            trainingData = new LinkedMap<>(allShares);
        } else {
            trainingData = trainingData(allShares, ratio);
        }

        final LinkedMap<String, ShareReturn> retVal = new LinkedMap<>();

        for ( final Map.Entry<LocalDate, List<Share>> shares : trainingData.entrySet() ) {
            for ( final Share actualShare : shares.getValue() ) {

                if ( !retVal.containsKey(actualShare.getName()) ) {
                    retVal.put(actualShare.getName(),
                        ShareReturn.builder().name(actualShare.getName()).sumOfYield(actualShare.getYield()).build());

                } else {
                    final ShareReturn localData = retVal.get(actualShare.getName());
                    localData.setSumOfYield(
                        localData.getSumOfYield().add(actualShare.getYield()).setScale(4, RoundingMode.HALF_UP));
                    retVal.put(actualShare.getName(), localData);
                }
            }
        }
        return retVal;
    }

    public static LinkedMap<LocalDate, List<Share>> trainingData( final LinkedMap<LocalDate, List<Share>> allShares,
        final BigDecimal ratio ) {

        final LinkedMap<LocalDate, List<Share>> retVal = new LinkedMap<>();
        final int trainingDataNum =
            ratio.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(allShares.size()))
                .setScale(0, RoundingMode.HALF_UP).intValue();
        int index = 0;
        for ( final Map.Entry<LocalDate, List<Share>> shares : allShares.entrySet() ) {

            if ( index == trainingDataNum ) {
                break;
            }
            retVal.put(shares.getKey(), shares.getValue());
            index++;

        }

        return retVal;
    }



}
