package model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class SplitData {

    private String name;
    private BigDecimal sumOfYield;

}
