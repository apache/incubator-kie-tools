/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.preferences.scope;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.bean.PreferenceScopeBean;

@Dependent
public class UserProjectPreferenceScope implements PreferenceScopeBean {

    private PreferenceScopeFactory scopeFactory;

    private ProjectContext projectContext;

    private Project project;

    @Inject
    public UserProjectPreferenceScope(final PreferenceScopeFactory scopeFactory,
                                      final ProjectContext projectContext) {
        this.scopeFactory = scopeFactory;
        this.projectContext = projectContext;
    }

    public UserProjectPreferenceScope forProject(final Project project) {
        this.project = project;
        return this;
    }

    @Override
    public PreferenceScope resolve() {
        if (project != null) {
            return createProjectScope(project);
        }

        if (projectContext.getActiveProject() != null) {
            return createProjectScope(projectContext.getActiveProject());
        }

        throw new RuntimeException("A project must be selected or be active to use this scope.");
    }

    private PreferenceScope createProjectScope(final Project project) {
        final PreferenceScope projectScope = scopeFactory.createScope(GuvnorPreferenceScopes.PROJECT,
                                                                      project.getEncodedIdentifier());
        final PreferenceScope userProjectScope = scopeFactory.createScope(GuvnorPreferenceScopes.USER,
                                                                          projectScope);

        return userProjectScope;
    }
}
