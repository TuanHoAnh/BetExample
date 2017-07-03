/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.betting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.repository.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "betting_groups")
@EqualsAndHashCode(of = {"competition", "name"})
public class BettingGroup extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    @JsonIgnore
    private Competition competition;
    @Column
    private String name;
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition="clob")
    private List<String> players;

    @Column
    private String rules;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        DRAFT,
        PUBLISHED,
        CLOSED
    }
}
