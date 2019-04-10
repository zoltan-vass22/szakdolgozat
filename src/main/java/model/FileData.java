package model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FileData {

    private Map<LocalDate, List<Share>> data;

    public FileData() {
        data = new LinkedHashMap<>();
    }

    public void createListOfShare( final LocalDate date ) {
        data.put(date, new ArrayList<>());
    }
}
