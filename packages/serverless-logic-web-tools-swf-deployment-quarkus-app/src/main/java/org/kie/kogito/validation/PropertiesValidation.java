/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileValidationResult;

public class PropertiesValidation implements FileValidation {

    @Override
    public FileValidationResult isValid(final Path path) {
        try {
            final Properties properties = new Properties();
            try (var inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
            return FileValidationResult.createValidResult(path);
        } catch (IOException e) {
            return FileValidationResult.createInvalidResult(path, e.getMessage());
        }
    }
}
