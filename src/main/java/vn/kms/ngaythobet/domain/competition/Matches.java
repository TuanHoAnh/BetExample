package vn.kms.ngaythobet.domain.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Matches {
    @JsonProperty("fixtures")
    List<Match> matches;
}
