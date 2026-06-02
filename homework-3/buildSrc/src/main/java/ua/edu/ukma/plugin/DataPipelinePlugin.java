package ua.edu.ukma.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DataPipelinePlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        project.getTasks().register("validateCsv", ValidateCsvDataTask.class);
        
        project.getTasks().register("generateManifest", GenerateBuildManifestTask.class);
    }
}