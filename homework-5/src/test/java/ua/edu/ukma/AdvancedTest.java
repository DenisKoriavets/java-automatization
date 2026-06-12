package ua.edu.ukma;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@Tag("advanced")
public class AdvancedTest {

    private final DataAnalyzer analyzer = new DataAnalyzer();

    @Test
    @DisplayName("Простий тест: обробка null має викидати IllegalArgumentException")
    void testNullValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            analyzer.isValidData(null);
        });
    }

    @ParameterizedTest(name = "Валідація правильного слова: {0}")
    @ValueSource(strings = {"apple", "banana", "cherry"})
    void testSingleParameter(String word) {
        assertTrue(analyzer.isValidData(word), "Слово '" + word + "' має бути валідним");
    }

    @ParameterizedTest(name = "Для слова {0} результат валідації має бути {1}")
    @CsvSource({
            "cat, false",
            "elephant, true",
            "dog, false",
            "tiger, true"
    })
    void testMultipleParameters(String input, boolean expectedResult) {
        assertEquals(expectedResult, analyzer.isValidData(input));
    }

    @TestFactory
    @DisplayName("Динамічна генерація тестів для обробки рядків")
    Stream<DynamicTest> dynamicTestsForStringProcessing() {
        Stream<String> inputData = Stream.of("  hello  ", "WORLD", "  jUnit  ");

        return inputData.map(input ->
            DynamicTest.dynamicTest("Тест обробки рядка: '" + input + "'", () -> {
                String result = analyzer.processData(input);
                
                assertFalse(result.startsWith(" "));
                assertFalse(result.endsWith(" "));
                assertEquals(result, result.toUpperCase());
            })
        );
    }

    @ParameterizedTest(name = "Тест витягування домену з: {0}")
    @ValueSource(strings = {
        "student@ukma.edu.ua",
        "just_a_random_string",
        "admin@gmail.com",
        "no_at_symbol_here"
    })
    void testDomainExtractionLogic(String input) {
        assumeTrue(input.contains("@"), "Пропуск: рядок '" + input + "' не є емейлом, тест домену неможливий");

        String domain = analyzer.extractDomain(input);

        assertFalse(domain.isEmpty(), "Домен не може бути порожнім");
        assertTrue(domain.contains("."), "Домен має містити крапку");
    }
}