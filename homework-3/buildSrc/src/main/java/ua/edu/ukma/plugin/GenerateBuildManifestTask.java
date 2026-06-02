package ua.edu.ukma.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GenerateBuildManifestTask extends DefaultTask {

    @TaskAction
    public void generate() {
        File resourcesDir = new File(getProject().getProjectDir(), "src/main/resources");
        File csvFile = new File(resourcesDir, "students.csv");
        
        File buildDir = new File(getProject().getProjectDir(), "build");
        if (!buildDir.exists()) {
            buildDir.mkdirs();
        }
        
        File manifestFile = new File(buildDir, "manifest.txt");

        try {
            int studentCount = 0;
            if (csvFile.exists()) {
                List<String> lines = Files.readAllLines(csvFile.toPath());
                studentCount = lines.isEmpty() ? 0 : lines.size() - 1;
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String author = System.getProperty("user.name");
            String gradleVersion = getProject().getGradle().getGradleVersion();

            String content = String.format("""
                    ===================================
                    ЗВІТ ПРО ЗБІРКУ ПРОЄКТУ
                    ===================================
                    Час збірки: %s
                    Автор (ОС користувач): %s
                    Версія Gradle: %s
                    Кількість студентів у базі: %d
                    ===================================
                    """, timestamp, author, gradleVersion, studentCount);

            Files.writeString(manifestFile.toPath(), content,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            getLogger().lifecycle("📄 Маніфест збірки успішно згенеровано: " + manifestFile.getAbsolutePath());

        } catch (IOException e) {
            throw new GradleException("Помилка при генерації маніфесту: " + e.getMessage(), e);
        }
    }
}