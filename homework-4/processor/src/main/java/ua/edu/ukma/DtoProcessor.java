package ua.edu.ukma;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("ua.edu.ukma.GenerateDto")
public class DtoProcessor extends AbstractProcessor {

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

            List<Element> fields = classElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD
                    && e.getAnnotation(ExcludeFromDto.class) == null)
                .collect(Collectors.toList());

            try (PrintWriter out = new PrintWriter(processingEnv.getFiler()
                .createSourceFile(packageName + "." + dtoClassName).openWriter())) {

                if (!packageName.isEmpty()) {
                    out.println("package " + packageName + ";");
                    out.println();
                }

                out.println("public record " + dtoClassName + "(");

                for (int i = 0; i < fields.size(); i++) {
                    Element field = fields.get(i);
                    boolean isLast = (i == fields.size() - 1);

                    MinLength minLength = field.getAnnotation(MinLength.class);
                    if (minLength != null) {
                        out.println("    @ua.edu.ukma.MinLength(" + minLength.value() + ")");
                    }

                    MaxValue maxValue = field.getAnnotation(MaxValue.class);
                    if (maxValue != null) {
                        out.println("    @ua.edu.ukma.MaxValue(" + maxValue.value() + ")");
                    }

                    String comma = isLast ? "" : ",";
                    out.println("    " + field.asType().toString() + " " + field.getSimpleName() + comma);
                }

                out.println(") {}");

            } catch (Exception e) {
                processingEnv.getMessager().printMessage(
                    javax.tools.Diagnostic.Kind.ERROR,
                    "Error generating DTO: " + e.getMessage()
                );
            }
        }

        return true;
    }
}