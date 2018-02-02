/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.service;

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DataSourceDefEditorService {

    DataSourceDefEditorContent loadContent(final Path path);

    Path save(final Path path,
              final DataSourceDefEditorContent editorContent,
              final String comment);

    Path create(final DataSourceDef dataSourceDef,
                final Module module);

    Path createGlobal(final DataSourceDef dataSourceDef);

    void delete(final Path path,
                final String comment);

    TestResult testConnection(final DataSourceDef dataSourceDef,
                              final Module module);

    TestResult testConnection(final DataSourceDef dataSourceDef);
}