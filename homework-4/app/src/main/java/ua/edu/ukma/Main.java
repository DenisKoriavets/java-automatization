package ua.edu.ukma;

public class Main {
    public static void main(String[] args) {
        AccountEntity entity = new AccountEntity();
        entity.id = 1L;
        entity.username = "DevUser";
        entity.password = "secret12345";
        entity.age = 25;
        entity.email = "dev@ukma.edu.ua";

        AccountEntityDto dto = new AccountEntityDto(
            entity.id,
            entity.username,
            entity.age,
            entity.email
        );

        System.out.println("Testing valid dto for: " + dto.username());
        try {
            Validator.validate(dto);
            System.out.println("Validation passed!");
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }

        AccountEntityDto invalidDto = new AccountEntityDto(
            entity.id,
            "Ab",
            150,
            entity.email
        );

        System.out.println("\nTesting invalid dto (username too short, age > 120)...");
        try {
            Validator.validate(invalidDto);
            System.out.println("Validation passed!");
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }
}