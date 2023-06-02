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

import java.nio.file.Path;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileValidationResult;

public class OpenApiValidation implements FileValidation {

    private final OpenAPIV3Parser parser = new OpenAPIV3Parser();

    @Override
    public FileValidationResult isValid(final Path path) {
        try {
            final SwaggerParseResult result = parser.readLocation(path.toAbsolutePath().toString(), null, null);
            if (result.getMessages() != null && result.getMessages().size() > 0) {
                return FileValidationResult.createInvalidResult(path, "Errors have been found when parsing the OpenAPI");
            }
            if (result.getOpenAPI() == null) {
                return FileValidationResult.createInvalidResult(path, "OpenAPI could not be found");
            }

            for (PathItem pathItem : result.getOpenAPI().getPaths().values()) {
                if (!isOperationIdProvided(pathItem)) {
                    return FileValidationResult.createInvalidResult(path, "One or more paths does provide operationId");
                }
            }

            return FileValidationResult.createValidResult(path);
        } catch (Exception e) {
            return FileValidationResult.createInvalidResult(path, e.getMessage());
        }
    }

    private boolean isOperationIdProvided(final PathItem pathItem) {
        if (pathItem.getGet() != null && pathItem.getGet().getOperationId() == null) {
            return false;
        }
        if (pathItem.getPost() != null && pathItem.getPost().getOperationId() == null) {
            return false;
        }
        if (pathItem.getPut() != null && pathItem.getPut().getOperationId() == null) {
            return false;
        }
        if (pathItem.getDelete() != null && pathItem.getDelete().getOperationId() == null) {
            return false;
        }
        return true;
    }
}
