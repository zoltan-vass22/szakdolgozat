import model.FileData;
import model.Share;
import processor.FileReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Test {


    public static void main( String[] args ) throws IOException {

        FileReader reader = new FileReader("p:\\SP500_weekly_2003_2008.csv");

        final FileData data = reader.read();

        for ( Map.Entry<LocalDate, List<Share>> shares : data.getData().entrySet() ) {
            System.out.println(shares.getKey() + "\r");
            for ( Share share : shares.getValue() ) {
                System.out.print(share.getName() + "|" + share.getPrice() + ",");
            }
            System.out.println(System.lineSeparator());
        }

    }

}
