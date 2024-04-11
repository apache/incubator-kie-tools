///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS org.kie:kie-dmn-api:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-dmn-core:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-dmn-model:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-api:${kogito-runtime.version:LATEST}
//DEPS org.kie:kie-internal:${kogito-runtime.version:LATEST}

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
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

    /**
     * For models without an Imported model, the args array holds the Original model and the parsed one (2 elements)
     * In case of Imported models, the following logic is applied: The odd index elements refer to the original
     * DMN file, while the even ones relate to the parsed DMN file. Example:
     * - originalModel
     * - originalModelImport1
     * - originalModelImport2
     * - parsedModel
     * - parsedModelImport1
     * - parsedModelImport2
     * ....
     */
    public static int compare(String... args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Comparison requires more than 2 DMN files");
        }

        List<File> models = Stream.of(args)
                .map(File::new)
                .collect(Collectors.toList());
        boolean areImportedModelsPresent = models.size() > 2;

        DMNModel originalModel = instantiateDMNRuntimeAndReturnDMNModel(models.subList(0, models.size() / 2));
        DMNModel parsedModel = instantiateDMNRuntimeAndReturnDMNModel(models.subList(models.size() / 2, models.size()));

        System.out.println("========== SEMANTIC COMPARISON ==========");
        System.out.println("Evaluating DMN file: " + models.get(0).getName());

        return compareDMNModels(originalModel, parsedModel);
    }

    static DMNModel instantiateDMNRuntimeAndReturnDMNModel(List<File> dmnFiles) throws Exception {
        if (dmnFiles.size() == 1) {
            Resource modelResource = ResourceFactory.newReaderResource(new FileReader(dmnFiles.get(0)), "UTF-8");
            DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                    .buildConfiguration()
                    .fromResources(Collections.singletonList(modelResource))
                    .getOrElseThrow(RuntimeException::new);
            return dmnRuntime.getModels().get(0);
        } else {
            List<Resource> resources = new ArrayList<>();
            String importerFileSourcePath = dmnFiles.get(0).getCanonicalPath();
            System.out.println("DEBUG - IMPORTED FILE SOURCE PATH");
            System.out.println(importerFileSourcePath);

            for (File file : dmnFiles) {
                Resource readerResource = ResourceFactory.newReaderResource(new FileReader(file), "UTF-8");
                readerResource.setSourcePath(file.getCanonicalPath());
                System.out.println("DEBUG - ADDED CANONICAL FILE SOURCE PATH");
                System.out.println(file.getCanonicalPath());
                resources.add(readerResource);
            }

            DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                    .buildConfiguration()
                    .fromResources(resources)
                    .getOrElseThrow(RuntimeException::new);
            DMNModel importerModel = null;

            for (DMNModel m : dmnRuntime.getModels()) {
                System.out.println("DEBUG - CHECkING CANONICAL FILE SOURCE PATH");
                System.out.println("DEBUG - SEARCHING: " + importerFileSourcePath);
                System.out.println("DEBUG - FOUND: " + m.getResource().getSourcePath());
                if (m.getResource().getSourcePath().equals(importerFileSourcePath)) {
                    importerModel = m;
                    break;
                }
            }

            if (importerModel == null) {
                throw new IllegalStateException("Was not able to identify importer model: " + importerFileSourcePath);
            }
            return importerModel;
        }
    }

    /**
     * This function compares two DMN models and returns a list of any missing elements between them.
     * The function checks both the original model and the parsed model to ensure that all elements are present in both models.
     * If any missing elements are found, the function returns a list of error messages describing the missing elements
     */
    static int compareDMNModels(DMNModel originalModel, DMNModel parsedModel) {
        Definitions originalModelDefinitions = originalModel.getDefinitions();
        Definitions parsedModelDefinitions = parsedModel.getDefinitions();

        List<String> missingElementsMessages = new ArrayList<String>();

        /* Check if the ORIGINAL model elements are present in the PARSED model */
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getDecisionService(), parsedModelDefinitions.getDecisionService()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getBusinessContextElement(), parsedModelDefinitions.getBusinessContextElement()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getDrgElement(), parsedModelDefinitions.getDrgElement()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getImport(), parsedModelDefinitions.getImport()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getItemDefinition(), parsedModelDefinitions.getItemDefinition()));

        /* Check if the PARSED model elements are present in the ORIGINAL model */
        missingElementsMessages.addAll(checkElements(parsedModelDefinitions.getDecisionService(), originalModelDefinitions.getDecisionService()));
        missingElementsMessages.addAll(checkElements(parsedModelDefinitions.getBusinessContextElement(), originalModelDefinitions.getBusinessContextElement()));
        missingElementsMessages.addAll(checkElements(parsedModelDefinitions.getDrgElement(), originalModelDefinitions.getDrgElement()));
        missingElementsMessages.addAll(checkElements(parsedModelDefinitions.getImport(), originalModelDefinitions.getImport()));
        missingElementsMessages.addAll(checkElements(parsedModelDefinitions.getItemDefinition(), originalModelDefinitions.getItemDefinition()));

        if (missingElementsMessages.isEmpty()) {
            System.out.println("RESULT: Original and Parsed files are semantically the same!");
            return 0;
        } else {
            System.out.println("ERROR: Original and Parsed files are NOT semantically the same!");
            missingElementsMessages.forEach(message -> System.out.println(message));
            return 1;
        }
    }

    /**
     * It's a generic method that checks if all elements in a Collection of type T are present in another Collection of the same type.
     * It takes two parameters:
     *
     * @param target A Collection of type T that represents the target collection to search for missing elements
     * @param source A Collection of type T that represents the source collection containing the elements to check.
     * @return
     */
    static <T extends NamedElement> List<String> checkElements(Collection<T> target, Collection<T> source) {
        return source.stream().filter(sourceElement -> checkIfAbsent(target, sourceElement))
                .map(sourceElement -> "Missing element: " + sourceElement.getName())
                .collect(Collectors.toList());
    }

    /**
     * This method checks if a given element is absent in a collection of elements based on its name. It takes two parameters:
     *
     * @param target A collection of elements to search through.
     * @param source The element to search for.
     * @return This method returns a boolean value indicating whether or not the element is absent from the collection.
     */
    static <T extends NamedElement> boolean checkIfAbsent(Collection<T> target, T source) {
        return target.stream().noneMatch(namedElement -> Objects.equals(namedElement.getName(), source.getName()));
    }
}
