package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class UserException extends BaseServiceException {

    public UserException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // ACCOUNT STATUS & PERMISSIONS
    // ================================

    public static class InsufficientPermissions extends UserException {
        public InsufficientPermissions() {
            super(
                    "INSUFFICIENT_PERMISSIONS",
                    "Bạn không có quyền thực hiện hành động này",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class AccountInactive extends UserException {
        public AccountInactive() {
            super(
                    "ACCOUNT_INACTIVE",
                    "Tài khoản người dùng không hoạt động",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class AccountDeleted extends UserException {
        public AccountDeleted() {
            super(
                    "ACCOUNT_DELETED",
                    "Tài khoản người dùng đã bị xóa",
                    HttpStatus.GONE
            );
        }
    }

    // ================================
    // PASSWORD & PROFILE
    // ================================

    public static class InvalidPassword extends UserException {
        public InvalidPassword() {
            super(
                    "INVALID_PASSWORD",
                    "Mật khẩu hiện tại không chính xác",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class WeakPassword extends UserException {
        public WeakPassword() {
            super(
                    "WEAK_PASSWORD",
                    "Mật khẩu không đáp ứng yêu cầu bảo mật",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotDeleteSelf extends UserException {
        public CannotDeleteSelf() {
            super(
                    "CANNOT_DELETE_SELF",
                    "Bạn không thể xóa tài khoản của chính mình",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotChangeOwnRole extends UserException {
        public CannotChangeOwnRole() {
            super(
                    "CANNOT_CHANGE_OWN_ROLE",
                    "Bạn không thể thay đổi vai trò của chính mình",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ================================
    // ROLE SPECIFIC
    // ================================

    public static class InvalidRoleTransition extends UserException {
        public InvalidRoleTransition(String from, String to) {
            super(
                    "INVALID_ROLE_TRANSITION",
                    String.format("Không thể thay đổi vai trò từ %s sang %s", from, to),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class RoleInUse extends UserException {
        public RoleInUse(String roleName) {
            super(
                    "ROLE_IN_USE",
                    String.format("Không thể xóa vai trò '%s' - nó đang được gán cho người dùng", roleName),
                    HttpStatus.CONFLICT
            );
        }
    }
}
