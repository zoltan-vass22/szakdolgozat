import lombok.Getter;
import model.FileData;
import model.Share;
import model.SplitData;
import processor.DataSplitter;
import processor.FileReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Test {

      public static void main( String[] args ) throws IOException {


        FileReader reader = new FileReader("I:\\SP500_weekly_2003_2008.csv");

        final FileData data = reader.read();

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, SplitData> sumOfReturn : DataSplitter.sumOfReturns(data.getData(),new BigDecimal(40)).entrySet()){
            sb.append(sumOfReturn.getKey()).append(":").append(sumOfReturn.getValue().getSumOfYield()).append("|");

        }
          System.out.println(sb.toString());


      /*for ( Map.Entry<LocalDate, List<Share>> shares : data.getData().entrySet() ) {
          StringBuilder sb2= new StringBuilder(shares.getKey()+" ");
            for ( Share share : shares.getValue() ) {
                sb2.append(share.getName() + "|" + share.getYield() + ",");
            }
          System.out.println(sb2.toString());
        }*/

    }

}
