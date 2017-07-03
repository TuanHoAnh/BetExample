package vn.kms.ngaythobet.web.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.kms.ngaythobet.domain.competition.CompetitorRepository;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.competition.CompetitorService;
import vn.kms.ngaythobet.service.file.Base64EncodeDecode;
import vn.kms.ngaythobet.service.user.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hungptnguyen on 4/15/2017.
 */
@Controller
public class ImageController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Value("${app.upload-dir}")
    private String logoUploadDir;

    @RequestMapping(value = "/images/competitions/{aliasKey}", method = RequestMethod.GET)
    public void getCompetitionLogo(HttpServletResponse response, @PathVariable String aliasKey) throws IOException {

        File logoFile = new File("./files/" + competitionService.findOneByAliasKey(aliasKey).getLogo());
        try (FileInputStream inputStream = new FileInputStream(logoFile)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }

    @RequestMapping(value = "/images/competitors/{competitorLogoFileName}", method = RequestMethod.GET)
    public void getCompetitorLogo(HttpServletResponse response, @PathVariable String competitorLogoFileName) throws IOException {
        String competitorLogoFileNameDecoded =  Base64EncodeDecode.decode(competitorLogoFileName);
        String competitorName = competitorLogoFileNameDecoded.substring(0,competitorLogoFileNameDecoded.lastIndexOf("-"));
        String competitionId = competitorLogoFileNameDecoded.substring(competitorLogoFileNameDecoded.lastIndexOf("-")+1);
        String logoPath = competitorRepository.findOneByNameAndCompetitionName(competitorName,competitionService.findOneById(Long.parseLong(competitionId)).getName()).get().getLogo();
        File logoFile = new File("./files/" + logoPath );
        try (FileInputStream inputStream = new FileInputStream(logoFile)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }

    @RequestMapping(value = "/images/user-avatar/{userId}", method = RequestMethod.GET)
    public void getUserAvatar(HttpServletResponse response, @PathVariable Long userId) throws IOException {

        File avatarFile = new File("./files/" + userService.findOne(userId).getAvatar());

        try (FileInputStream inputStream = new FileInputStream(avatarFile)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }
}
