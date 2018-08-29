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

package org.kie.workbench.common.screens.examples.client.wizard.pages.project;

import java.util.List;

import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.uberfire.client.mvp.UberView;

public interface ProjectPageView extends UberView<ProjectPage> {

    interface Presenter {

        void addProject(final ImportProject project);

        void removeProject(final ImportProject project);

        boolean isProjectSelected(final ImportProject project);

        void addTag(final String tag);

        void addPartialTag(final String tag);

        void removeTag(final String tag);

        void removeAllTags();
    }

    void initialise();

    void setProjectsInRepository(final List<ImportProject> projects);

    void destroy();
}
