package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class RiskModelListItem {
    private BigDecimal dailyReturn;
    private LocalDate date;
    private BigDecimal centralDistribution;
}
