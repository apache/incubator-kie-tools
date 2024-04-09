///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS org.kie:kie-dmn-api:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-dmn-core:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-dmn-model:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-api:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-internal:${kogito-runtime.version:LATEST}

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DynamicDMNContextBuilder;
import org.kie.internal.io.ResourceFactory;

/**
 * JBang script that performs DMN files' XML (in string format) validation relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * The script can manage one or two (in case of imported model) DMN file paths.
 * The XSD SCHEMA, DMN COMPLIANCE and DMN COMPILATION are validated.
 */
class dmnSemanticComparison {

    public static void main(String... args) throws Exception {
        int exitCode = compare(args);
        System.exit(exitCode);
    }

    public static int compare(String... args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Comparison requires 2 DMN files");
        }

        File[] models = Stream.of(args)
                              .map(File::new)
                              .toArray(File[]::new);

        Resource modelResource = ResourceFactory.newReaderResource(new FileReader(models[0]), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        DMNModel originalModel = dmnRuntime.getModels().get(0);

        Resource modelResource1 = ResourceFactory.newReaderResource(new FileReader(models[1]), "UTF-8");
        DMNRuntime dmnRuntime1 = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource1)).getOrElseThrow(RuntimeException::new);
        DMNModel parsedModel = dmnRuntime1.getModels().get(0);

        compareDMNModels(originalModel, parsedModel);

        //System.out.println(args[0]);
        //System.out.println(args[1]);



        return 0;

        /*File[] models = Stream.of(args)
                              .map(File::new)
                              .toArray(File[]::new);

        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));

        final List<DMNMessage> messages = dmnValidator.validateUsing(Validation.VALIDATE_SCHEMA,
                                                                     Validation.VALIDATE_MODEL,
                                                                     Validation.VALIDATE_COMPILATION)
                                                      .theseModels(models);

        if (messages.size() == 0) {
            System.out.println("=== DMN FILES SUCCESSFULLY VALIDATED! ===");
            Stream.of(models).forEach(model -> System.out.println(model.getName()));
            System.out.println("=========================================");
            return 0;
        } else {
            System.out.println("=== DMN VALIDATION FAILED ===");
            Stream.of(models).forEach(model -> System.out.println(model.getName()));
            messages.forEach(message -> System.out.println(message.getText()));
            System.out.println("=============================");
            return 1;
        } */
    }

    private static void compareDMNModels(DMNModel originalModel, DMNModel parsedModel) {

        Definitions originalModelDefinitions = originalModel.getDefinitions();
        Definitions parsedModelDefinitions = parsedModel.getDefinitions();

        checkElements(originalModelDefinitions.getDecisionService(), parsedModelDefinitions.getDecisionService());
        checkElements(originalModelDefinitions.getBusinessContextElement(), parsedModelDefinitions.getBusinessContextElement());
        checkElements(originalModelDefinitions.getDrgElement(), parsedModelDefinitions.getDrgElement());
        checkElements(originalModelDefinitions.getImport(), parsedModelDefinitions.getImport());
        checkElements(originalModelDefinitions.getItemDefinition(), parsedModelDefinitions.getItemDefinition());
    }

    static <T extends NamedElement> List<String> checkElements(Collection<T> target, Collection<T> source) {
        return source.map(sourceElement -> {
            boolean isAbsent = checkIfAbsent(target, sourceElement);
            return isAbsent ? "sourceElement " + sourceElement.getName() + "is missing!!!" : true;
            System.out.println(isAbsent);
        }).collect(Collectors.toList());
    }

    static <T extends NamedElement> boolean checkIfAbsent(Collection<T> target, T source) {
        return target.stream().noneMatch(namedElement -> {
            System.out.println(namedElement.getName() + " - " + source.getName());
            return Objects.equals(namedElement.getName(), source.getName());
        });
    }
}