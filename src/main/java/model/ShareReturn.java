package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder( toBuilder = true )
@ToString
public class ShareReturn {

    private String name;
    private BigDecimal sumOfReturn;

}
