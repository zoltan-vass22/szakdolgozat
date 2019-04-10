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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
public class FileReader {

    private String filePath;

    public FileData read() throws IOException {
        final FileData data = new FileData();

        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        for ( CSVRecord csvRecord : csvParser.getRecords() ) {

            LocalDate key = LocalDate.parse(csvRecord.get("DATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if ( data.getData().get(key) == null ) {
                data.createListOfShare(key);
            }

            for ( Map.Entry<String, Integer> actual : csvParser.getHeaderMap().entrySet() ) {

                if ( !"DATE".equals(actual.getKey()) ) {
                    data.getData().get(key).add(Share.builder().name(actual.getKey()).price(new BigDecimal(csvRecord.get(actual.getKey()))).build());
                }
            }
        }

        return data;
    }

}
