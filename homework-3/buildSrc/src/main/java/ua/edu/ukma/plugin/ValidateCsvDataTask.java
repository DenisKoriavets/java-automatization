package ua.edu.ukma.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ValidateCsvDataTask extends DefaultTask {

    @TaskAction
    public void validate() {
        File resourcesDir = new File(getProject().getProjectDir(), "src/main/resources");
        File csvFile = new File(resourcesDir, "students.csv");

        if (!csvFile.exists()) {
            throw new GradleException("Помилка збирання: Файл students.csv не знайдено у папці src/main/resources!");
        }

        try {
            List<String> lines = Files.readAllLines(csvFile.toPath());

            if (lines.isEmpty()) {
                throw new GradleException("Помилка збирання: Файл students.csv порожній!");
            }

            String header = lines.getFirst().trim();
            if (!header.equals("Name,Score")) {
                throw new GradleException("Помилка даних: Неправильний формат CSV. Очікувався заголовок 'Name,Score', а отримано: '" + header + "'");
            }

            getLogger().lifecycle("✅ Валідація CSV пройдена успішно. Формат даних правильний.");

        } catch (IOException e) {
            throw new GradleException("Системна помилка при читанні файлу students.csv: " + e.getMessage(), e);
        }
    }
}