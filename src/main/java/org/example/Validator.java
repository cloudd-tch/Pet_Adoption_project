package org.example;

public class Validator
{
    public static void validateOnlyLetters(String value, String fieldName) {
        if (value == null || !value.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException(fieldName + " must contain letters only!");
        }
    }

    public static void validateOnlyNumbers(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative!");
        }
    }

    public static void totalMonths(int totalMonths) {
        if (totalMonths > 360) {
            throw new IllegalArgumentException("Age cannot be above 30 years!");
        }
    }

    public static void validateCredentials(String value, String fieldName) {
        if (value == null || value.contains(" ") || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot contain spaces or be empty!");
        }
    }
}
