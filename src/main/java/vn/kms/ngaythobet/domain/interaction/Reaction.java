/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.interaction;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reactions")
@Data
public class Reaction {
    @Id
    private String id;

    private String subjectType;
    private String subjectId;
    private ReactionType reactionType;
    private String comment;
    private String author;
    private LocalDateTime timestamp;
}
