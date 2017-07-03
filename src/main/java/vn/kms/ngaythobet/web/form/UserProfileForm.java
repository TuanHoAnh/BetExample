/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.LanguageMatch;
import vn.kms.ngaythobet.infras.validation.ValidFileType;


/**
 * Created by vuongvu on 4/3/2017.
 */
@Data
public class UserProfileForm {

    private Long id;

    @Length(min = 1, max = 50, message = "{updateprofile.form.firstname}: {validation.updateform.length.message}")
    private String firstName;

    @Length(min = 1, max = 50, message = "{updateprofile.form.lastname}: {validation.updateform.length.message}")
    private String lastName;

    @Email
    private String email;

    @LanguageMatch(message = "{updateprofile.form.language}: {validation.updateform.language.message}")
    private String languageTag;

    private String avatar;

    private MultipartFile avatarFile;

    public static UserProfileForm from(User user) {
        UserProfileForm userProfileForm = new UserProfileForm();
        userProfileForm.setId(user.getId());
        userProfileForm.setFirstName(user.getFirstName());
        userProfileForm.setLastName(user.getLastName());
        userProfileForm.setEmail(user.getEmail());
        userProfileForm.setLanguageTag(user.getLanguageTag());
        userProfileForm.setAvatar(user.getAvatar());

        return userProfileForm;
    }

}
