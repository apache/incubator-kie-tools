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

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.jboss.logging.Logger;
import org.kie.kogito.api.FileValidation;

public class OpenApiValidation implements FileValidation {

    private static final Logger LOGGER = Logger.getLogger(OpenApiValidation.class);

    private final OpenAPIParser parser = new OpenAPIParser();

    @Override
    public boolean isValid(final Path path) {
        try {
            final SwaggerParseResult result = parser.readLocation(path.toAbsolutePath().toString(), null, null);
            if (result.getMessages() != null && result.getMessages().size() > 0) {
                LOGGER.error("The following errors were found when validating the Open API file:");
                result.getMessages().forEach(LOGGER::error);
                return false;
            }
            if (result.getOpenAPI() == null) {
                LOGGER.error("OpenAPI cannot be found");
                return false;
            }

            for (PathItem pathItem : result.getOpenAPI().getPaths().values()) {
                if (!isOperationIdProvided(pathItem)) {
                    LOGGER.error("One or more paths does provide operationId");
                    return false;
                }
            }

            LOGGER.info("OpenAPI file validated: " + result.getOpenAPI().getInfo().getTitle());
            return true;
        } catch (Exception e) {
            LOGGER.error("Error when validating Open API file: " + e.getMessage());
            return false;
        }
    }

    private boolean isOperationIdProvided(final PathItem pathItem) {
        boolean result = true;
        if (pathItem.getGet() != null && pathItem.getGet().getOperationId() == null) {
            LOGGER.error("operationId not present for GET method");
            result = false;
        }
        if (pathItem.getPost() != null && pathItem.getPost().getOperationId() == null) {
            LOGGER.error("operationId not present for POST method");
            result = false;
        }
        if (pathItem.getPut() != null && pathItem.getPut().getOperationId() == null) {
            LOGGER.error("operationId not present for PUT method");
            result = false;
        }
        if (pathItem.getDelete() != null && pathItem.getDelete().getOperationId() == null) {
            LOGGER.error("operationId not present for DELETE method");
            result = false;
        }
        return result;
    }
}
