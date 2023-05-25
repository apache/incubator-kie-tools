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

package org.kie.kogito.model;

import java.nio.file.Path;
import java.util.Objects;

public class FileValidationResult {

    private final Path filePath;
    private final boolean isValid;

    private final String errorMessage;

    private FileValidationResult(Path filePath, boolean isValid, String errorMessage) {
        this.filePath = filePath;
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public Path getFilePath() {
        return filePath;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileValidationResult that = (FileValidationResult) o;
        return isValid == that.isValid && Objects.equals(filePath, that.filePath) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, isValid, errorMessage);
    }

    public static FileValidationResult createValidResult(final Path filepath) {
        return new FileValidationResult(filepath, true, null);
    }

    public static FileValidationResult createInvalidResult(final Path filepath, final String errorMessage) {
        return new FileValidationResult(filepath, false, errorMessage);
    }
}
