plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("dataPipeline") {
            id = "ua.edu.ukma.data-pipeline"
            implementationClass = "ua.edu.ukma.plugin.DataPipelinePlugin"
        }
    }
}