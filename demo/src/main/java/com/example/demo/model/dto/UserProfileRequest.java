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
    public static class CreateProfile {

        @NotBlank(message = "Địa chỉ email không được để trống")
        @Email(message = "Email phải hợp lệ")
        private String email;

        @NotBlank(message = "Họ tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
        private String fullName;

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại phải là định dạng Việt Nam hợp lệ")
        private String phoneNumber;

        @NotBlank(message = "Vai trò không được để trống")
//        @Pattern(regexp = "^(Admin|Staff Employee|Technician Employee|Customer)$",
//                message = "Vai trò phải là Admin, Staff Employee, Customer, hoặc Technician Employee")
        private String roleDisplayName;

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profile {

        @Email(message = "Email phải hợp lệ")
        private String email;

        @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
        private String fullName;

        @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại phải là định dạng Việt Nam hợp lệ")
        private String phoneNumber;

//        @Pattern(regexp = "^(Quản trị viên|Nhân viên|Technician Employee|Customer)$",
//                message = "Vai trò phải là Admin, Staff Employee, Customer, hoặc Technician Employee")
        private String roleDisplayName;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Password{
        @NotBlank(message = "Mật khẩu cũ không được để trống")
        @Size(min = 8, message = "Mật khẩu cũ phải có ít nhất 8 ký tự")
        private String oldPassword;

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 8, message = "Mật khẩu mới phải có ít nhất 8 ký tự")
        private String newPassword;
    }
}
