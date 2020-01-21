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

package org.kie.workbench.common.screens.archetype.mgmt.client.modal;

import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.ArchetypeAlreadyExistsException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.InvalidArchetypeException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.MavenExecutionException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

@Dependent
public class AddArchetypeModalPresenter implements HasBusyIndicator {

    private final View view;
    private final TranslationService ts;
    private final Caller<ArchetypeService> archetypeService;
    private final BusyIndicatorView busyIndicatorView;

    @Inject
    public AddArchetypeModalPresenter(final View view,
                                      final TranslationService ts,
                                      final Caller<ArchetypeService> archetypeService,
                                      final BusyIndicatorView busyIndicatorView) {
        this.view = view;
        this.ts = ts;
        this.archetypeService = archetypeService;
        this.busyIndicatorView = busyIndicatorView;
    }

    @PostConstruct
    public void postConstruct() {
        view.init(this);
    }

    public void show() {
        view.resetAll();
        view.show();
    }

    public void hide() {
        view.hide();
    }

    public void add() {
        final String archetypeGroupId = view.getArchetypeGroupId();
        final String archetypeArtifactId = view.getArchetypeArtifactId();
        final String archetypeVersion = view.getArchetypeVersion();

        validateFields(archetypeGroupId,
                       archetypeArtifactId,
                       archetypeVersion,
                       () -> {
                           beginAddArchetype();
                           final GAV archetypeGAV = new GAV(archetypeGroupId.trim(),
                                                            archetypeArtifactId.trim(),
                                                            archetypeVersion.trim());

                           archetypeService.call(v -> {
                               endAddArchetype();
                               hide();
                           }, addActionErrorCallback()).add(archetypeGAV);
                       });
    }

    private void beginAddArchetype() {
        view.enableAddButton(false);
        view.enableFields(false);
        busyIndicatorView.showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));
    }

    private void endAddArchetype() {
        view.enableAddButton(true);
        view.enableFields(true);
        busyIndicatorView.hideBusyIndicator();
    }

    private ErrorCallback<Object> addActionErrorCallback() {
        return (message, throwable) -> {
            endAddArchetype();

            if (throwable instanceof ArchetypeAlreadyExistsException) {
                view.showGeneralError(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ArchetypeAlreadyExistsMessage));
                return false;
            }

            if (throwable instanceof MavenExecutionException) {
                view.showGeneralError(
                        ts.format(ArchetypeManagementConstants.ArchetypeManagement_MavenExecutionExceptionMessage,
                                  throwable.getMessage()));
                return false;
            } else if (throwable instanceof InvalidArchetypeException) {
                view.showGeneralError(ts.getTranslation(
                        ArchetypeManagementConstants.ArchetypeManagement_InvalidArchetypeExceptionMessage));
                return false;
            }

            return true;
        };
    }

    private void validateFields(final String archetypeGroupId,
                                final String archetypeArtifactId,
                                final String archetypeVersion,
                                final Runnable callback) {
        view.clearErrors();

        boolean isValid = true;

        final Predicate<String> isInvalidContent = content -> content == null || content.trim().isEmpty();

        if (isInvalidContent.test(archetypeGroupId)) {
            final String errorMsg = ts.format(ArchetypeManagementConstants.ArchetypeManagement_EmptyFieldValidation,
                                              ts.getTranslation(ArchetypeManagementConstants.ArchetypeGroupId));
            view.showArchetypeGroupIdError(errorMsg);
            isValid = false;
        }

        if (isInvalidContent.test(archetypeArtifactId)) {
            final String errorMsg = ts.format(ArchetypeManagementConstants.ArchetypeManagement_EmptyFieldValidation,
                                              ts.getTranslation(ArchetypeManagementConstants.ArchetypeArtifactId));
            view.showArchetypeArtifactIdError(errorMsg);
            isValid = false;
        }

        if (isInvalidContent.test(archetypeVersion)) {
            final String errorMsg = ts.format(ArchetypeManagementConstants.ArchetypeManagement_EmptyFieldValidation,
                                              ts.getTranslation(ArchetypeManagementConstants.ArchetypeVersion));
            view.showArchetypeVersionError(errorMsg);
            isValid = false;
        }

        if (isValid) {
            callback.run();
        }
    }

    public void cancel() {
        hide();
    }

    @Override
    public void showBusyIndicator(final String message) {
        busyIndicatorView.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    public interface View extends UberElemental<AddArchetypeModalPresenter> {

        void show();

        void hide();

        void showGeneralError(String errorMessage);

        void showArchetypeGroupIdError(String errorMessage);

        void showArchetypeArtifactIdError(String errorMessage);

        void showArchetypeVersionError(String errorMessage);

        void clearErrors();

        String getArchetypeGroupId();

        String getArchetypeArtifactId();

        String getArchetypeVersion();

        void resetAll();

        void enableAddButton(boolean isEnabled);

        void enableFields(boolean isEnabled);
    }
}
