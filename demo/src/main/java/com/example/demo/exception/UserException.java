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
                    "You don't have permission to perform this action",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class AccountInactive extends UserException {
        public AccountInactive() {
            super(
                    "ACCOUNT_INACTIVE",
                    "User account is inactive",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class AccountDeleted extends UserException {
        public AccountDeleted() {
            super(
                    "ACCOUNT_DELETED",
                    "User account has been deleted",
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
                    "Current password is incorrect",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class WeakPassword extends UserException {
        public WeakPassword() {
            super(
                    "WEAK_PASSWORD",
                    "Password does not meet security requirements",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotDeleteSelf extends UserException {
        public CannotDeleteSelf() {
            super(
                    "CANNOT_DELETE_SELF",
                    "You cannot delete your own account",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotChangeOwnRole extends UserException {
        public CannotChangeOwnRole() {
            super(
                    "CANNOT_CHANGE_OWN_ROLE",
                    "You cannot change your own role",
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
                    String.format("Cannot change role from %s to %s", from, to),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class RoleInUse extends UserException {
        public RoleInUse(String roleName) {
            super(
                    "ROLE_IN_USE",
                    String.format("Cannot delete role '%s' - it is assigned to users", roleName),
                    HttpStatus.CONFLICT
            );
        }
    }
}
