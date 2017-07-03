/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.infras.repository.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "competitions")
@EqualsAndHashCode(of = "name")
public class Competition extends BaseEntity {
    @JsonProperty("caption")
    @Column
    private String aliasKey;

    @Column
    private String name;

    @Column
    private String logo;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> rounds;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status = Status.DRAFT;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "competition")
    private List<Competitor> competitors;

    @Transient
    private int numberOfMatchdays;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "competition")
    @OrderBy("name")
    private List<BettingGroup> groups;

    public String getValueByFieldName(String fieldName) {
        if ("aliasKey".equals(fieldName)) {
            return aliasKey;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        return null;
    }

    public enum Status {
        DRAFT,
        PUBLISHED,
        FINISHED
    }
}
