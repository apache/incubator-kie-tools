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

package org.kie.workbench.common.screens.projecteditor.client.forms.repositories;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.shared.security.AppRoles;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Dependent
public class RepositoriesWidgetPresenter
        implements RepositoriesWidgetView.Presenter,
                   IsWidget {

    private User identity;
    private RepositoriesWidgetView view;

    public RepositoriesWidgetPresenter() {
    }

    @Inject
    public RepositoriesWidgetPresenter(final User identity,
                                       final RepositoriesWidgetView view) {
        this.identity = identity;
        this.view = view;
        view.init(this);
    }

    @Override
    public void setContent(final Set<ProjectRepositories.ProjectRepository> repositories,
                           final boolean isReadOnly) {
        checkNotNull("repositories",
                     repositories);

        view.setContent(repositories,
                        isReadOnly || !isUserAdministrator());
    }

    private boolean isUserAdministrator() {
        final Set<Role> roles = identity.getRoles();
        return roles.contains(new RoleImpl(AppRoles.ADMIN.getName()));
    }

    @Override
    public void setIncludeRepository(final ProjectRepositories.ProjectRepository repository,
                                     final boolean include) {
        repository.setIncluded(include);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
