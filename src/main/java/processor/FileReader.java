package processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.FileData;
import model.Share;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter @AllArgsConstructor @Setter public class FileReader {

    private String filePath;

    public FileData read() throws IOException {
        final FileData data = new FileData();

        final Reader reader = Files.newBufferedReader(Paths.get(filePath));
        final CSVParser csvParser =
            new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        for ( final CSVRecord csvRecord : csvParser.getRecords() ) {

            final LocalDate key = LocalDate.parse(csvRecord.get("DATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if ( data.getData().get(key) == null ) {
                data.createListOfShare(key);
            }

            for ( final Map.Entry<String, Integer> actual : csvParser.getHeaderMap().entrySet() ) {

                if ( !"DATE".equals(actual.getKey()) ) {
                    if ( csvRecord.getRecordNumber() == 1 ) {
                        data.getData().get(key).add(
                            Share.builder().name(actual.getKey()).price(new BigDecimal(csvRecord.get(actual.getKey())))
                                .yield(BigDecimal.ONE).build());

                    } else {
                        final List<Share> prevShares = data.getData().getValue((int) csvRecord.getRecordNumber() - 2);
                        final BigDecimal prevPrice = prevShares.get(actual.getValue() - 1).getPrice();
                        final Share actualShare =
                            Share.builder().name(actual.getKey()).price(new BigDecimal(csvRecord.get(actual.getKey())))
                                .build();
                        actualShare.setYield(
                            actualShare.getPrice().divide(prevPrice, 4, RoundingMode.HALF_UP).add(new BigDecimal(-1)));
                        data.getData().get(key).add(actualShare);


                    }


                }
            }
        }

        return data;
    }


}
