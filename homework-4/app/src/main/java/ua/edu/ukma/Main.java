package ua.edu.ukma;

public class Main {
    public static void main(String[] args) {
        
        UserRegistrationDto userDto = new UserRegistrationDto();
        
        userDto.username = "Ad";
        userDto.password = "supersecret123";
        
        try {
            Validator.validate(userDto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        userDto.username = "AdminUser";
        try {
            Validator.validate(userDto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}