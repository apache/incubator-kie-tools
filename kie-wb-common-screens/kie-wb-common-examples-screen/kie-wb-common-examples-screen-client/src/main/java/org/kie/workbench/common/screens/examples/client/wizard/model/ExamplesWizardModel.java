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

package org.kie.workbench.common.screens.examples.client.wizard.model;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesModel;

public class ExamplesWizardModel extends ExamplesModel {

    private ExampleRepository selectedRepository;
    private String selectedBranch = "master";

    public ExampleRepository getSelectedRepository() {
        return selectedRepository;
    }

    public void setSelectedRepository(final ExampleRepository selectedRepository) {
        this.selectedRepository = selectedRepository;
    }

    public String getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(final String selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public void addProject(final ImportProject project) {
        getProjects().add(PortablePreconditions.checkNotNull("project",
                                                             project));
    }

    public void removeProject(final ImportProject project) {
        getProjects().remove(PortablePreconditions.checkNotNull("project",
                                                                project));
    }
}
