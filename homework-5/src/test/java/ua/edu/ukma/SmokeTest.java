package ua.edu.ukma;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("smoke")
public class SmokeTest {

    @Test
    void fastSystemCheck() {
        DataAnalyzer analyzer = new DataAnalyzer();
        assertNotNull(analyzer, "Система має створювати об'єкти");
    }
}