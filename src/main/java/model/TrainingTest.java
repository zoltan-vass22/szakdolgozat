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
@Builder
@ToString
public class TrainingTest {
    private BigDecimal test;
    private BigDecimal training;

}
