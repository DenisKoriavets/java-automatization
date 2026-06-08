package ua.edu.ukma;

import java.lang.reflect.Field;

public class Validator {

    public static void validate(Object obj) throws IllegalAccessException {
        System.out.println("--- Starting validation of object of class " + obj.getClass().getSimpleName() + " ---");

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(MinLength.class)) {
                MinLength rule = field.getAnnotation(MinLength.class);
                int requiredLength = rule.value();
                String actualValue = (String) field.get(obj);

                if (actualValue == null) {
                    throw new IllegalArgumentException("Error! Field '" + field.getName() + "' cannot be null.");
                }

                if (actualValue.length() < requiredLength) {
                    throw new IllegalArgumentException(
                        "Validation error! Field '" + field.getName() +
                            "' has value '" + actualValue + "' (length " + actualValue.length() + "). " +
                            "Minimum required length is " + requiredLength + " characters!"
                    );
                }

                System.out.println("Field '" + field.getName() + "' successfully passed validation.");
            }

            if (field.isAnnotationPresent(MaxValue.class)) {
                MaxValue rule = field.getAnnotation(MaxValue.class);
                int limit = rule.value();

                int actualValue = field.getInt(obj);

                if (actualValue > limit) {
                    throw new IllegalArgumentException(
                        "Validation error! Field '" + field.getName() +
                            "' has value " + actualValue + ", which exceeds the maximum allowed " + limit + "!"
                    );
                }
                System.out.println("Field '" + field.getName() + "' (max value check) passed.");
            }
        }
        System.out.println("--- Validation completed successfully! ---\n");
    }
}