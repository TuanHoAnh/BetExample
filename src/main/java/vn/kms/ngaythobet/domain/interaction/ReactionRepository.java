/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */
package vn.kms.ngaythobet.domain.interaction;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReactionRepository extends MongoRepository<Reaction, String> {
}
