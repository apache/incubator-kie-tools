/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dsl.serialization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.serialization.impl.DashboardZipSerializer;
import org.dashbuilder.dsl.validation.DashboardValidator;
import org.dashbuilder.dsl.validation.ValidationResult;
import org.dashbuilder.dsl.validation.ValidationResult.ValidationResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardExporter {

    private static final Logger logger = LoggerFactory.getLogger(DashboardExporter.class);
    private static final DashboardExporter INSTANCE = new DashboardExporter();

    DashboardValidator validator = DashboardValidator.get();

    public enum ExportType {
        ZIP
    }

    private DashboardExporter() {
        // empty
    }

    public static DashboardExporter get() {
        return INSTANCE;
    }

    public void export(Dashboard dashboard, String path, ExportType type) {
        export(dashboard, Paths.get(path), type);
    }

    public void export(Dashboard dashboard, Path path, ExportType type) {
        DashboardSerializer serializer = serializerFor(type);
        validate(dashboard);
        Path temp = createTempDashboardFile();
        try (FileOutputStream fos = new FileOutputStream(temp.toFile())) {
            serializer.serialize(dashboard, fos);
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file " + path, e);
        } finally {
            try {
                Files.deleteIfExists(temp);
            } catch (IOException e) {
                logger.error("Error deleting temp file", e);
            }
        }

    }

    private Path createTempDashboardFile() {
        try {
            return Files.createTempFile("dashboard", ".zip");
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp file to export dashboard", e);
        }
    }

    void validate(Dashboard dashboard) {
        List<ValidationResult> results = validator.validate(dashboard);

        printResult(results, ValidationResultType.ERROR, logger::error);
        printResult(results, ValidationResultType.WARNING, logger::warn);
        printResult(results, ValidationResultType.SUCCESS, logger::info);

        if (results.stream().anyMatch(p -> p.getType() == ValidationResultType.ERROR)) {
            throw new IllegalArgumentException("There are validation errors, check logs for more details");
        }

    }

    private void printResult(List<ValidationResult> results, ValidationResultType type, Consumer<String> printer) {
        results.stream()
               .filter(v -> v.getType() == type)
               .map(Object::toString)
               .forEach(printer::accept);
    }

    private static DashboardZipSerializer serializerFor(ExportType type) {
        // only ZIP is supported at the moment
        switch (type) {
            case ZIP:
                return new DashboardZipSerializer();
        }
        return new DashboardZipSerializer();
    }

}