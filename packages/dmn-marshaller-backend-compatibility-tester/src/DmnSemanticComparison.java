/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

///usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES ./DmnMarshallerBackendCompatibilityTesterScript.java
/* Please do not declare new dependencies here, but in the above Parent class to preserve 
 *  the dependencies fetching mechanism */

package jbang;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

/**
 * JBang script that performs DMN files' XML (in string format) validation
 * relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * The script can manage one or two (in case of imported model) DMN file paths.
 * The XSD SCHEMA, DMN COMPLIANCE and DMN COMPILATION are validated.
 */
class DmnSemanticComparison extends DmnMarshallerBackendCompatibilityTesterScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(DmnSemanticComparison.class);

    @Option(names = {"-c", "--command"}, description = "Command to execute (no_imports) or (with_imports)", required = true)
    private String command;

    @Option(names = {"-o", "--originalDmnFilePath"}, description = "Path of original DMN file to be compared", required = true)
    private String originalDmnPath;

    @Option(names = {"-g", "--generatedDmnFilePath"}, description = "Path of generated DMN file to be compared", required = true)
    private String generatedDmnPath;

    @Option(names = {"-i", "--importedOriginalDmnFilesPaths"}, description = "Paths of the DMN files imported by the DMN file to validate", required = false, split = ",")
    private String[] importedOriginalDmnPaths;

    @Option(names = {"-j", "--importedGeneratedDmnFilesPaths"}, description = "Paths of the DMN files imported by the DMN file to validate", required = false, split = ",")
    private String[] importedGeneratedDmnPaths;

    public static void main(String... args) {
        int exitCode = new CommandLine(new DmnSemanticComparison()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            switch (command) {
                case "no_imports":
                    return compareDMNModelsNoImports();
                case "with_imports": {
                    if (importedOriginalDmnPaths == null || importedOriginalDmnPaths.length == 0
                            || importedGeneratedDmnPaths == null ||
                            importedGeneratedDmnPaths.length == 0
                            || importedOriginalDmnPaths.length != importedGeneratedDmnPaths.length) {
                        throw new IllegalArgumentException("Imported DMN paths are missing or wrong");
                    }
                    return compareDMNModelsWithImports();
                }
                default:
                    LOGGER.error("Unknown command {}", command);
                    return 1;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute {}", command, e);
            return 100;
        }
    }

    private int compareDMNModelsNoImports() throws Exception {
        DMNModel originalModel = instantiateDMNRuntimeAndReturnDMNModel(new File(originalDmnPath));
        DMNModel generatedModel = instantiateDMNRuntimeAndReturnDMNModel(new File(generatedDmnPath));

        LOGGER.info("========== SEMANTIC COMPARISON ==========");
        LOGGER.info("Evaluating DMN file: " + originalModel.getName());

        return compareDMNModels(originalModel, generatedModel);
    }

    private int compareDMNModelsWithImports() throws Exception {
        List<File> importedOriginalDmnFiles = Stream.of(importedOriginalDmnPaths)
                .map(File::new)
                .collect(Collectors.toList());
        List<File> importedGeneratedDmnFiles = Stream.of(importedGeneratedDmnPaths)
                .map(File::new)
                .collect(Collectors.toList());

        DMNModel originalModel = instantiateDMNRuntimeAndReturnDMNModel(new File(originalDmnPath),
                importedOriginalDmnFiles);
        DMNModel generatedModel = instantiateDMNRuntimeAndReturnDMNModel(new File(generatedDmnPath),
                importedGeneratedDmnFiles);

        LOGGER.info("========== SEMANTIC COMPARISON ==========");
        LOGGER.info("Evaluating DMN file: " + originalModel.getName());

        return compareDMNModels(originalModel, generatedModel);
    }

    private DMNModel instantiateDMNRuntimeAndReturnDMNModel(File dmnFile) throws Exception {
        Resource modelResource = ResourceFactory.newReaderResource(new FileReader(dmnFile), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(modelResource))
                .getOrElseThrow(RuntimeException::new);
        return dmnRuntime.getModels().get(0);
    }

    private DMNModel instantiateDMNRuntimeAndReturnDMNModel(File importerDmnFile, List<File> importedDmnFiles)
            throws Exception {
        List<Resource> resources = new ArrayList<>();
        String importerFileSourcePath = importerDmnFile.getCanonicalPath();
        List<File> allDMNFiles = new ArrayList(importedDmnFiles);
        allDMNFiles.add(importerDmnFile);

        for (File file : allDMNFiles) {
            Resource readerResource = ResourceFactory.newReaderResource(new FileReader(file), "UTF-8");
            readerResource.setSourcePath(file.getCanonicalPath());
            resources.add(readerResource);
        }

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(resources)
                .getOrElseThrow(RuntimeException::new);
        DMNModel importerModel = null;

        for (DMNModel m : dmnRuntime.getModels()) {
            if (m.getResource().getSourcePath().equals(importerFileSourcePath.replace("\\", "/"))) {
                importerModel = m;
                break;
            }
        }

        if (importerModel == null) {
            throw new IllegalStateException("Was not able to identify importer model: " + importerFileSourcePath);
        }
        return importerModel;
    }

    /**
     * This function compares two DMN models and returns a list of any missing
     * elements between them.
     * The function checks both the original model and the generated model to ensure
     * that all elements are present in both models.
     * If any missing elements are found, the function returns a list of error
     * messages describing the missing elements
     */
    private int compareDMNModels(DMNModel originalModel, DMNModel generatedModel) {
        Definitions originalModelDefinitions = originalModel.getDefinitions();
        Definitions generatedModelDefinitions = generatedModel.getDefinitions();

        List<String> missingElementsMessages = new ArrayList<>();

        /* Check if the ORIGINAL model elements are present in the GENERATED model */
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getDecisionService(),
                generatedModelDefinitions.getDecisionService()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getBusinessContextElement(),
                generatedModelDefinitions.getBusinessContextElement()));
        missingElementsMessages.addAll(
                checkElements(originalModelDefinitions.getDrgElement(), generatedModelDefinitions.getDrgElement()));
        missingElementsMessages
                .addAll(checkElements(originalModelDefinitions.getImport(), generatedModelDefinitions.getImport()));
        missingElementsMessages.addAll(checkElements(originalModelDefinitions.getItemDefinition(),
                generatedModelDefinitions.getItemDefinition()));

        /* Check if the GENERATED model elements are present in the ORIGINAL model */
        missingElementsMessages.addAll(checkElements(generatedModelDefinitions.getDecisionService(),
                originalModelDefinitions.getDecisionService()));
        missingElementsMessages.addAll(checkElements(generatedModelDefinitions.getBusinessContextElement(),
                originalModelDefinitions.getBusinessContextElement()));
        missingElementsMessages.addAll(
                checkElements(generatedModelDefinitions.getDrgElement(), originalModelDefinitions.getDrgElement()));
        missingElementsMessages
                .addAll(checkElements(generatedModelDefinitions.getImport(), originalModelDefinitions.getImport()));
        missingElementsMessages.addAll(checkElements(generatedModelDefinitions.getItemDefinition(),
                originalModelDefinitions.getItemDefinition()));

        if (missingElementsMessages.isEmpty()) {
            LOGGER.info("RESULT: Original and Generated files are semantically the same!");
            return 0;
        } else {
            LOGGER.error("ERROR: Original and Generated files are NOT semantically the same!");
            missingElementsMessages.forEach(message -> LOGGER.error(message));
            System.err.println("ERROR: Original and Generated files are NOT semantically the same!");
            System.err.println("DMN File Name: " + originalModel.getName());
            missingElementsMessages.forEach(System.err::println);
            return 1;
        }
    }

    /**
     * It's a generic method that checks if all elements in a Collection of type T
     * are present in another Collection of the same type.
     * It takes two parameters:
     *
     * @param target A Collection of type T that represents the target collection to
     *               search for missing elements
     * @param source A Collection of type T that represents the source collection
     *               containing the elements to check.
     * @return
     */
    static <T extends NamedElement> List<String> checkElements(Collection<T> target, Collection<T> source) {
        return source.stream().filter(sourceElement -> checkIfAbsent(target, sourceElement))
                .map(sourceElement -> "Missing element: " + sourceElement.getName())
                .collect(Collectors.toList());
    }

    /**
     * This method checks if a given element is absent in a collection of elements
     * based on its name. It takes two parameters:
     *
     * @param target A collection of elements to search through.
     * @param source The element to search for.
     * @return This method returns a boolean value indicating whether or not the
     *         element is absent from the collection.
     */
    static <T extends NamedElement> boolean checkIfAbsent(Collection<T> target, T source) {
        return target.stream().noneMatch(namedElement -> Objects.equals(namedElement.getName(), source.getName()));
    }
}
