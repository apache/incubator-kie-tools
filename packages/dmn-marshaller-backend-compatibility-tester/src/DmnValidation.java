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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.DMNValidatorFactory;

/**
 * JBang script that performs DMN files' XML (in string format) validation
 * relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * The script can manage one or two (in case of imported model) DMN file paths.
 * The XSD SCHEMA, DMN COMPLIANCE and DMN COMPILATION are validated.
 */
@Command(name = "DmnSemanticComparison", mixinStandardHelpOptions = true, version = "DmnSemanticComparison 0.1", description = "It validates given DMN files")
class DmnValidation extends DmnMarshallerBackendCompatibilityTesterScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(DmnValidation.class);

    @Option(names = {"-c", "--command"}, description = "Command to execute (no_imports) or (with_imports)", required = true)
    private String command;

    @Option(names = {"-d", "--dmnFilePath"}, description = "Path of DMN file to be validated", required = true)
    private String dmnFilePath;

    @Option(names = {"-i", "--importedDmnFilesPaths"} , description = "Paths of the DMN files imported by the DMN file to validate", required = false, split = ",")
    private String[] importedDmnFilesPath;

    public static void main(String... args) {
        int exitCode = new CommandLine(new DmnValidation()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            switch (command) {
                case "no_imports":
                    return validateDMNModelsNoImports();
                case "with_imports": {
                    if (importedDmnFilesPath == null || importedDmnFilesPath.length == 0) {
                        throw new IllegalArgumentException("Imported DMN paths are missing");
                    }
                    return validateDMNModelsWithImports();
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

    private int validateDMNModelsNoImports() {
        File dmnFile = new File(dmnFilePath);
        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
        final List<DMNMessage> messages = dmnValidator.validateUsing(Validation.VALIDATE_SCHEMA,
                Validation.VALIDATE_MODEL,
                Validation.VALIDATE_COMPILATION)
                .theseModels(dmnFile);
        return assessDMNMessageResults(messages, dmnFile.getName());
    }

    private int validateDMNModelsWithImports() {
        File dmnFile = new File(dmnFilePath);
        List<File> models = new ArrayList<>();
        models.add(dmnFile);
        models.addAll(Stream.of(importedDmnFilesPath)
                .map(File::new)
                .collect(Collectors.toList()));
        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
        List<DMNMessage> messages = dmnValidator.validateUsing(
                Validation.VALIDATE_SCHEMA,
                Validation.VALIDATE_MODEL,
                Validation.VALIDATE_COMPILATION)
                .theseModels(models.toArray(File[]::new));
        return assessDMNMessageResults(messages, dmnFile.getName());
    }

    private int assessDMNMessageResults(List<DMNMessage> dmnMessageResults, String dmnFileName) {
        if (dmnMessageResults.size() == 0) {
            LOGGER.info("RESULT: Following files have been successfully validated!");
            return 0;
        } else {
            LOGGER.error("ERROR: DMN Validation failed for the DMN file " + dmnFileName);
            LOGGER.error("Validation Errors:");
            List<String> messages = dmnMessageResults.stream().map(DMNMessage::getText).collect(Collectors.toList());
            messages.forEach(LOGGER::error);
            System.err.println("ERROR: DMN Validation failed!");
            System.err.println("DMN File Name: " + dmnFileName);
            messages.forEach(System.err::println);
            return 1;
        }
    }
}