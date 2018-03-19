/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.service;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class MigrationResult {

    private Path path;

    private BPMNDiagramEditorService.ServiceError error;

    public MigrationResult(@MapsTo("path") final Path path,
                           @MapsTo("error") final BPMNDiagramEditorService.ServiceError error) {
        this.path = path;
        this.error = error;
    }

    public MigrationResult(final Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public BPMNDiagramEditorService.ServiceError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
