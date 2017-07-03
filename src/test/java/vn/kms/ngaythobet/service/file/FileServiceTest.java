/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.service.file;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.kms.ngaythobet.BaseTest;

import static org.junit.Assert.*;

public class FileServiceTest extends BaseTest {
    @Value("${ngaythobet.competitor-logo}")
    private String defaultLogo;

    @Autowired
    FileService fileService;

    @Test
    public void saveLogo() throws Exception {

    }

    @Test
    public void saveLogoCompetitor() throws Exception {

    }

    @Test
    public void downloadLogoCompetitorNotExistLink() throws Exception {
        String actual = fileService.downloadLogoCompetitor("https://abc.com/not-exist.png", "", "");
        String expected = defaultLogo;

        assertEquals(expected, actual);
    }

    @Test
    public void downloadLogoCompetitorInvalidType() throws Exception {
        String actual = fileService.downloadLogoCompetitor("https://abc.com/not-exist.png", "not-exist.png", "france-1.png");
        String expected = defaultLogo;

        assertEquals(expected, actual);
    }

    @Test
    public void downloadLogoCompetitorFileWithoutExtension() throws Exception {
        String actual = fileService.downloadLogoCompetitor("https://abc.com/xyz/not-exist", "not-exist", "france-1");
        String expected = defaultLogo;

        assertEquals(expected, actual);
    }
}
