/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.web.form.CreateMatchForm;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name="matches")
@NoArgsConstructor
@EqualsAndHashCode(of = {"round", "competitor1", "competitor2"})
public class Match extends BaseEntity {
    private String round;

    @ManyToOne
    @JoinColumn(name = "competitor1_id")
    private Competitor competitor1;

    private Integer score1;

    @ManyToOne
    @JoinColumn(name = "competitor2_id")
    private Competitor competitor2;

    private Integer score2;

    @JsonProperty("date")
    private LocalDateTime startTime;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    @Transient
    private int matchday;

    @Transient
    private String homeTeamName;

    @Transient
    private String awayTeamName;

    @Transient
    private Result result;

    public Match(Competitor competitor1, Competitor competitor2) {
        this.competitor1 = competitor1;
        this.competitor2 = competitor2;
    }

    public boolean isValidToUpdateMatchScore() {
        return LocalDateTime.now().isAfter(this.startTime);
    }

    public String getName(){
        return competitor1.getName() + " vs " + competitor2.getName();
    }
}
