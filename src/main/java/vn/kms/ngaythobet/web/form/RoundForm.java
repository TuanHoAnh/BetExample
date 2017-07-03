package vn.kms.ngaythobet.web.form;

import lombok.Data;
import vn.kms.ngaythobet.domain.competition.Match;

import java.util.List;

@Data
public class RoundForm {
    private String name;

    private List<Match> matchList;
}
