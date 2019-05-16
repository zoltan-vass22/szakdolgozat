package processor;

import model.Share;
import model.ShareYield;
import org.apache.commons.collections4.map.LinkedMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DataSplitter {

    public static LinkedMap<String, ShareYield> sumOfReturns( final LinkedMap<LocalDate, List<Share>> allShares ) {
        return sumOfReturns(allShares, null);
    }

    public static LinkedMap<String, ShareYield> sumOfReturns( final LinkedMap<LocalDate, List<Share>> allShares,
        final BigDecimal ratio ) {

        final LinkedMap<LocalDate, List<Share>> trainingData;
        if ( ratio == null ) {
            trainingData = new LinkedMap<>(allShares);
        } else {
            trainingData = trainingData(allShares, ratio);
        }

        final LinkedMap<String, ShareYield> retVal = new LinkedMap<>();

        for ( final Map.Entry<LocalDate, List<Share>> shares : trainingData.entrySet() ) {
            for ( final Share actualShare : shares.getValue() ) {

                if ( !retVal.containsKey(actualShare.getName()) ) {
                    retVal.put(actualShare.getName(),
                        ShareYield.builder().name(actualShare.getName()).sumOfYield(actualShare.getYield()).build());

                } else {
                    final ShareYield localData = retVal.get(actualShare.getName());
                    localData.setSumOfYield(localData.getSumOfYield().add(actualShare.getYield()));
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
