package ua.edu.ukma;

public class DataAnalyzer {

    public boolean isValidData(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Вхідні дані не можуть бути null");
        }
        return input.trim().length() >= 5;
    }

    public String processData(String input) {
        if (!isValidData(input)) {
            throw new IllegalArgumentException("Дані не пройшли валідацію");
        }
        return input.trim().toUpperCase();
    }

    public String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Це не email");
        }
        return email.substring(email.indexOf("@") + 1).trim();
    }
}