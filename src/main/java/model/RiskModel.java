package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class RiskModel {
    private List<RiskModelListItem> training;
    private List<RiskModelListItem> test;
}
