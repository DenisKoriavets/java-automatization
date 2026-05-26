package ua.edu.ukma;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Mojo(name = "report", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class ReportMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", property = "sourceDir", required = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/project-report.html", property = "outputFile", required = true)
    private File outputFile;

    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory not found. No report generated.");
            return;
        }

        long fileCount = 0;
        long lineCount = 0;
        long totalSizeBytes = 0;

        try (Stream<Path> paths = Files.walk(sourceDirectory.toPath())) {

            long[] stats = paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .map(this::analyzeFile)
                .reduce(new long[] {0L, 0L, 0L},
                    (a, b) -> new long[] {a[0] + b[0], a[1] + b[1], a[2] + b[2]});

            fileCount = stats[0];
            lineCount = stats[1];
            totalSizeBytes = stats[2];

            generateHtmlReport(fileCount, lineCount, totalSizeBytes);
            getLog().info(
                "Project report successfully generated at: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate project report", e);
        }
    }

    private long[] analyzeFile(Path file) {
        long lines = 0;
        long size = 0;

        try (Stream<String> fileStream = Files.lines(file)) {
            lines = fileStream.count();
            size = Files.size(file);
        } catch (IOException e) {
            getLog().warn("Could not read file: " + file.getFileName());
        }

        return new long[]{1L, lines, size};
    }

    private void generateHtmlReport(long files, long lines, long size) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html lang="uk">
                <head>
                    <meta charset="UTF-8">
                    <title>Звіт по проєкту</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f9; color: #333; padding: 40px; }
                        .container { background: #fff; padding: 20px 40px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); max-width: 500px; margin: auto; }
                        h1 { font-size: 24px; border-bottom: 2px solid #0078D7; padding-bottom: 10px; margin-bottom: 20px; }
                        .stat { font-size: 18px; margin: 10px 0; display: flex; justify-content: space-between; }
                        .value { font-weight: bold; color: #0078D7; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Аналітика вихідного коду</h1>
                        <div class="stat"><span>Кількість Java файлів:</span> <span class="value">%d</span></div>
                        <div class="stat"><span>Всього рядків коду:</span> <span class="value">%d</span></div>
                        <div class="stat"><span>Розмір у байтах:</span> <span class="value">%d</span></div>
                    </div>
                </body>
                </html>
                """.formatted(files, lines, size);

        Path outPath = outputFile.toPath();
        Files.createDirectories(outPath.getParent());
        Files.writeString(outPath, html);
    }
}