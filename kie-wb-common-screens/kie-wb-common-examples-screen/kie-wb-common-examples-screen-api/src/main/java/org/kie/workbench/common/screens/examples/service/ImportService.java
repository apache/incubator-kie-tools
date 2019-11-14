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
 *
 */

package org.kie.workbench.common.screens.examples.service;

import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.uberfire.commons.lifecycle.PriorityDisposable;

public interface ImportService extends PriorityDisposable {

    Set<ImportProject> getProjects(final OrganizationalUnit targetOu, final ExampleRepository repository);

    WorkspaceProjectContextChangeEvent importProjects(final OrganizationalUnit activeOU,
                                                      final List<ImportProject> projects);

    WorkspaceProject importProject(final OrganizationalUnit activeOU,
                                   final ImportProject projects);

    boolean exist(OrganizationalUnit ou, ImportProject importProject);
}
