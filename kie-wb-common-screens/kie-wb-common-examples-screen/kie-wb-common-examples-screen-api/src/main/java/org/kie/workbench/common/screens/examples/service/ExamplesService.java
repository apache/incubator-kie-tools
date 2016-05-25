/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.service;

import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.uberfire.commons.lifecycle.PriorityDisposable;

@Remote
public interface ExamplesService extends PriorityDisposable {

    String EXAMPLES_SYSTEM_PROPERTY = "org.kie.demo";

    ExamplesMetaData getMetaData();

    Set<ExampleProject> getProjects( final ExampleRepository repository );

    boolean validateRepositoryName( final String name );

    ProjectContextChangeEvent setupExamples( final ExampleOrganizationalUnit exampleTargetOU,
                                             final ExampleTargetRepository exampleTarget,
                                             final List<ExampleProject> exampleProjects );

}
