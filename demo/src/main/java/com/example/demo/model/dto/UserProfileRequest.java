package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class UserProfileRequest {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profile {

        @NotBlank(message = "Email address can not be blank")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Full name can not be blank")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        private String fullName;

        @NotBlank(message = "Phone number can not be blank")
        @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Phone number must be valid Vietnamese format")
        private String phoneNumber;

        @NotBlank(message = "Role can not be blank")
        @Pattern(regexp = "^(Admin|Staff Employee|Technician Employee|Customer)$",
                message = "Role must be Admin, Staff Employee, Customer, or Technician Employee")
        private String roleDisplayName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Password{
        @NotBlank(message = "Old password can not be blank")
        @Size(min = 8, message = "Old password must be at least 8 characters long")
        private String oldPassword;

        @NotBlank(message = "New password can not be blank")
        @Size(min = 8, message = "New password must be at least 8 characters long")
        private String newPassword;
    }
}
