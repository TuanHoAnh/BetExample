/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findOneByUsername(String username);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByActivationKey(String activationKey);

    Optional<List<User>> findByEmailIgnoreCaseContainingOrUsernameIgnoreCaseContaining(String email, String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByEmailAndActivated(String email, boolean activated);

    User findOne(Long id);
}
