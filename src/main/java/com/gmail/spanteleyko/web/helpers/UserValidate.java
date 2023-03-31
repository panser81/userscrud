package com.gmail.spanteleyko.web.helpers;

import com.gmail.spanteleyko.web.models.UserDTO;

import static com.gmail.spanteleyko.web.constants.UserConstants.*;

public class UserValidate {

    public static void validate(UserDTO user) {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
        validateAge(user.getAge());
        validateAddress(user.getAddress());
        validateTelephone(user.getTelephone());
    }

    private static void validateUsername(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Name does not have value");
        }

        if (name.length() > USERNAME_MAX_LENGTH) {
            throw new IllegalStateException("Name has more than 40 characters");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Password does not have value");
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalStateException("Your password must be at least 6 characters");
        }

        if (password.length() > USERNAME_MAX_LENGTH) {
            throw new IllegalStateException("Password has more than 40 characters");
        }
    }

    private static void validateAge(int age) {
        if (age < AGE_MIN_VALUE || age > AGE_MAX_VALUE) {
            throw new IllegalStateException("Please input correct age for the user: 0 -150");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalStateException("Name does not have value");
        }

        if (address.length() > ADDRESS_MAX_LENGTH) {
            throw new IllegalStateException("Name has more than 40 characters");
        }
    }

    private static void validateTelephone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalStateException("Name does not have value");
        }

        if (phoneNumber.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalStateException("Your phone number must be at least 7 characters");
        }
        if (phoneNumber.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalStateException("Name has more than 40 characters");
        }
    }
}
