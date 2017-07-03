/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.competition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "competitors")
@NoArgsConstructor
@EqualsAndHashCode(of = {"competition", "name"})
public class Competitor extends BaseEntity {

    private String name;

    private String logo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    @Transient
    private String crestUrl;

    public Competitor(String name) {
        this.name = name;
    }
}
