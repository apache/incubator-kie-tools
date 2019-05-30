/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.integration.service;

import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService.ServiceError;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.uberfire.backend.vfs.Path;

@Portable
public class MigrateResult {

    private Path path;

    private ServiceError error;

    private String messageKey;

    private List<?> messageArguments;

    public MigrateResult(@MapsTo("path") final Path path,
                         @MapsTo("error") final ServiceError error,
                         @MapsTo("messageKey") final String messageKey,
                         @MapsTo("messageArguments") final List<?> messageArguments) {
        this.path = path;
        this.error = error;
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    public MigrateResult(final Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public ServiceError getError() {
        return error;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<?> getMessageArguments() {
        return messageArguments;
    }

    public boolean hasError() {
        return error != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MigrateResult that = (MigrateResult) o;
        return Objects.equals(path, that.path) &&
                error == that.error &&
                Objects.equals(messageKey, that.messageKey) &&
                Objects.equals(messageArguments, that.messageArguments);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(path),
                                         Objects.hashCode(error),
                                         Objects.hashCode(messageKey),
                                         Objects.hashCode(messageArguments));
    }
}
