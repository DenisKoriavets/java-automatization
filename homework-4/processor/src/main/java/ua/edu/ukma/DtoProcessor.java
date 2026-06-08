package ua.edu.ukma;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.PrintWriter;
import java.util.Set;

public class DtoProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(GenerateDto.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateDto.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;

            TypeElement classElement = (TypeElement) element;
            String originalClassName = classElement.getSimpleName().toString();
            String dtoClassName = originalClassName + "Dto";
            PackageElement pkgElement = processingEnv.getElementUtils().getPackageOf(classElement);
            String packageName = pkgElement.getQualifiedName().toString();

            try (PrintWriter out = new PrintWriter(processingEnv.getFiler()
                .createSourceFile(packageName + "." + dtoClassName).openWriter())) {
                if (!packageName.isEmpty()) {
                    out.println("package " + packageName + ";\n");
                }
                out.println("public class " + dtoClassName + " {");

                for (Element enclosed : classElement.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.FIELD) {
                        MinLength minLength = enclosed.getAnnotation(MinLength.class);
                        if (minLength != null) {
                            out.println("    @ua.edu.ukma.MinLength(" + minLength.value() + ")");
                        }
                        out.println("    public " + enclosed.asType().toString() + " " + enclosed.getSimpleName() + ";\n");
                    }
                }
                out.println("}");
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "Error generating DTO: " + e.getMessage());
            }
        }

        return true;
    }
}