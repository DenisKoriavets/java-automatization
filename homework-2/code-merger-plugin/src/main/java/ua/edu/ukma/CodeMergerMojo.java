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
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class CodeMergerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", property = "sourceDir", required = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/merged-code.txt", property = "outputFile", required = true)
    private File outputFile;

    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist. Nothing to merge.");
            return;
        }

        try {
            Path outPath = outputFile.toPath();
            Files.createDirectories(outPath.getParent());
            Files.deleteIfExists(outPath);
            Files.createFile(outPath);

            try (Stream<Path> paths = Files.walk(sourceDirectory.toPath())) {
                paths.filter(Files::isRegularFile)
                     .filter(p -> p.toString().endsWith(".java"))
                     .forEach(p -> appendFileContent(p, outPath));
            }

            getLog().info("All Java files have been successfully merged into: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            throw new MojoExecutionException("Error occurred while merging Java files", e);
        }
    }

    private void appendFileContent(Path source, Path destination) {
        try {
            String content = Files.readString(source) + System.lineSeparator();
            Files.writeString(destination, content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            getLog().error("Failed to read or write file: " + source, e);
        }
    }
}