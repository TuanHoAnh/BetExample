/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util;

import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Created by hieptran on 4/3/2017.
 */
public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateTokenKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateUsernameForSSOUser(String email, UserRepository userRepo) {
        String generatedUsername = email
            .replaceAll("@(.+)$", "")
            .replaceAll("[^a-zA-Z]", "");

        Random rand = new Random();
        while (userRepo.findOneByUsername(generatedUsername).isPresent()) {
            int min = 100;
            int max = 999;
            int threeDigitRandom = rand.nextInt((max - min) + 1) + min;
            generatedUsername = generatedUsername + threeDigitRandom;
        }

        return generatedUsername;
    }


}
