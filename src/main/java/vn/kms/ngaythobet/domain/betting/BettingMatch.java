/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.betting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.Match;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "betting_matches")
@NoArgsConstructor
@EqualsAndHashCode(of = {"bettingGroup", "match"})
public class BettingMatch extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "betting_group_id")
    private BettingGroup bettingGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Column(name = "balance1")
    private double balance1;

    @Column(name = "balance2")
    private double balance2;

    @Column
    private LocalDateTime expiryTime;

    @Column(name="betting_amount")
    private BigDecimal bettingAmount;

    @Column
    private String comment;

    @Column
    private boolean active;

    @OneToMany(mappedBy = "bettingMatch")
    private List<BettingPlayer> bettingPlayers;

    public BettingMatch(Match match) {
        this.match = match;
    }

    @Transient
    public String getValueByFieldName(String fieldName){
        if (fieldName.equals("matchId")){
            return this.match.getId().toString();
        }
        if (fieldName.equals("groupId")){
            return this.bettingGroup.getId().toString();
        }
        return null;
    }

    @Transient
    public List<BettingPlayer> getBettingPlayersByCompetitor(Competitor competitor){
        List<BettingPlayer> players= new ArrayList<>();
        for (BettingPlayer player : bettingPlayers) {
            if(player.getBetCompetitor().getId()==competitor.getId()) {
                players.add(player);
            }
        }
        return players;

    }
}
