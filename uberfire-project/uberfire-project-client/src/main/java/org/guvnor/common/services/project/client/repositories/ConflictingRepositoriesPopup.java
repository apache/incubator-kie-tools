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

package org.guvnor.common.services.project.client.repositories;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.shared.security.AppRoles;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class ConflictingRepositoriesPopup
        implements ConflictingRepositoriesPopupView.Presenter {

    private User identity;
    private ConflictingRepositoriesPopupView view;

    private Command okCommand;
    private Command overrideCommand;

    public ConflictingRepositoriesPopup() {
    }

    @Inject
    public ConflictingRepositoriesPopup(final User identity,
                                        final ConflictingRepositoriesPopupView view) {
        this.identity = identity;
        this.view = view;
        view.init(this);
    }

    public void setContent(final GAV gav,
                           final Set<MavenRepositoryMetadata> repositories,
                           final Command overrideCommand) {
        setContent(gav, repositories, null, overrideCommand);
    }

    public void setContent(final GAV gav,
                           final Set<MavenRepositoryMetadata> metadata,
                           final Command okCommand,
                           final Command overrideCommand) {
        checkNotNull("gav", gav);
        checkNotNull("metadata", metadata);
        checkNotNull("overrideCommand", overrideCommand);

        this.okCommand = okCommand;
        this.overrideCommand = overrideCommand;

        view.setContent(gav, metadata);

        view.clear();
        view.addOKButton();
        if (isUserAdministrator()) {
            view.addOverrideButton();
        }
    }

    private boolean isUserAdministrator() {
        final Set<Role> roles = identity.getRoles();
        return roles.contains(new RoleImpl(AppRoles.ADMIN.getName()));
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void override() {
        safeExecute(overrideCommand);
        view.hide();
    }

    @Override
    public void hide() {
        safeExecute(okCommand);
        view.hide();
    }

    private void safeExecute(final Command command) {
        if (command != null) {
            command.execute();
        }
    }
}
