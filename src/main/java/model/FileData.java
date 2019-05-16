package model;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.LinkedMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter public class FileData {

    private LinkedMap<LocalDate, List<Share>> data;

    public FileData() {
        data = new LinkedMap<>();
    }

    public void createListOfShare( final LocalDate date ) {
        data.put(date, new ArrayList<>());
    }
}
