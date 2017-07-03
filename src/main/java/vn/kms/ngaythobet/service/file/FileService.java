package vn.kms.ngaythobet.service.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.kms.ngaythobet.domain.util.exception.InvalidFileNameException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
public class FileService {
    private static final String IMAGE = "images";

    @Value("${app.upload-dir}")
    private String logoUploadDir;

    @Value("${ngaythobet.competitor-logo}")
    private String defaultLogo;

    @Value("${ngaythobet.user-avatar}")
    private String defaultAvatar;


    public String saveLogo(MultipartFile fileUpload,String aliasKey) throws IOException{
        try {
            File logoFile = createFileToSave("/images/competitions/", fileUpload.getOriginalFilename(), aliasKey);

            return saveMultipartFile(fileUpload, logoFile);
        } catch (InvalidFileNameException e) {
            log.warn("Cannot save the competition logo: missing the extension", e);
            return defaultLogo;
        }
    }

    public String saveLogoCompetitor(MultipartFile fileUpload,String competitorLogoFileName) throws IOException{
        try {
            File logoFile = createFileToSave("/images/competitors/", fileUpload.getOriginalFilename(), competitorLogoFileName);

            return saveMultipartFile(fileUpload, logoFile);
        } catch (InvalidFileNameException e) {
            log.warn("Cannot save the competitor logo: missing the extension", e);
            return defaultLogo;
        }
    }

    public String downloadLogoCompetitor(String imageUrl, String originalFileName, String competitorLogoFileName) {
        try {
            URL url = new URL(imageUrl);
            File logoFile = createFileToSave("/images/competitors/", originalFileName, competitorLogoFileName);

            return saveFileFromUrl(url, logoFile);
        } catch (IOException|InvalidFileNameException e) {
            log.warn("Cannot download the image with url " + imageUrl, e);
            return defaultLogo;
        }
    }

    private File createFileToSave(String filePath, String originalFileName, String logoFileName) throws InvalidFileNameException {
        File uploadDir = new File(logoUploadDir + filePath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return new File(uploadDir, logoFileName + "." + getFileExtenstion(originalFileName));
    }

    private String saveMultipartFile(MultipartFile fileUpload, File logoFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(logoFile)) {
            IOUtils.copy(fileUpload.getInputStream(), fileOutputStream);
            return "/" + logoFile.getPath().substring(logoFile.getPath().indexOf(IMAGE));
        }
    }

    private String saveFileFromUrl(URL fileUrl, File logoFile) throws InvalidFileNameException, IOException {
        BufferedImage img = ImageIO.read(fileUrl);
        String fileExtension = getFileExtenstion(logoFile.getName());

        if (img != null && ("png".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)
            || "jpeg".equalsIgnoreCase(fileExtension))) {

            ImageIO.write(img, getFileExtenstion(logoFile.getName()), logoFile);
            return "/" + logoFile.getPath().substring(logoFile.getPath().indexOf(IMAGE));
        }
        return defaultLogo;
    }

    private String getFileExtenstion(String fileName) throws InvalidFileNameException {
        int indexOfDot = fileName.lastIndexOf('.');
        if (indexOfDot != -1) {
            return fileName.substring(indexOfDot + 1);
        }

        throw new InvalidFileNameException("File name does not contain extension.");
    }

    public String saveUserAvatar(MultipartFile fileUpload, String userAvatarFileName) throws IOException{
        String urlAvatar = defaultAvatar;
        File uploadDir = new File(logoUploadDir + "/images/user-avatar/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String originalFileName = fileUpload.getOriginalFilename();
        String avatarName = userAvatarFileName + originalFileName.substring(originalFileName.lastIndexOf('.'));
        File avatarFile = new File(uploadDir, avatarName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(avatarFile)) {
            IOUtils.copy(fileUpload.getInputStream(), fileOutputStream);
            urlAvatar = "/" + avatarFile.getPath().substring(avatarFile.getPath().indexOf(IMAGE));
        }
        return urlAvatar;
    }
}
