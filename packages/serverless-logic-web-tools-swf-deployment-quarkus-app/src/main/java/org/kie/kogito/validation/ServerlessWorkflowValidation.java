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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import javax.lang.model.SourceVersion;

import io.serverlessworkflow.api.Workflow;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileValidationResult;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

public class ServerlessWorkflowValidation implements FileValidation {

    @Override
    public FileValidationResult isValid(final Path path) {
        try {
            final WorkflowFormat format = resolveFormat(path);

            if (format == null) {
                return FileValidationResult.createInvalidResult(path, "Not a valid Serverless Workflow file format");
            }
            final Workflow workflow = ServerlessWorkflowUtils.getWorkflow(
                    new InputStreamReader(new FileInputStream(path.toAbsolutePath().toString())),
                    format);
            if (SourceVersion.isName(workflow.getId())) {
                return FileValidationResult.createValidResult(path);
            } else {
                return FileValidationResult.createInvalidResult(path, workflow.getId() + " is not a valid ID for a Serverless Workflow");
            }
        } catch (IOException e) {
            return FileValidationResult.createInvalidResult(path, e.getMessage());
        }
    }

    private WorkflowFormat resolveFormat(final Path path) {
        final String fileName = path.getFileName().toString();
        if (fileName.endsWith(".sw.json")) {
            return WorkflowFormat.JSON;
        }
        if (fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
            return WorkflowFormat.YAML;
        }
        return null;
    }
}
