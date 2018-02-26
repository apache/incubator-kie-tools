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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation.DependencyValidator;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class NewDependencyPopup {

    private final NewDependencyPopupView view;
    private DependencyValidator validator;

    private Dependency dependency;

    private Callback<Dependency> callback;

    @Inject
    public NewDependencyPopup(final NewDependencyPopupView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void show(final Callback<Dependency> callback) {
        this.callback = callback;
        dependency = new Dependency();
        validator = new DependencyValidator(dependency);
        view.clean();
        view.show();
    }

    public void onOkClicked() {

        if (validator.validate()) {
            callback.callback(dependency);
            view.hide();
        } else {
            validateGroupId();
            validateArtifactId();
            validateVersion();
        }
    }

    public void onGroupIdChange(final String groupId) {
        dependency.setGroupId(groupId);
        validateGroupId();
    }

    public void validateGroupId() {
        if (validator.validateGroupId()) {
            view.invalidGroupId("");
            view.setGroupIdValidationState(ValidationState.SUCCESS);
        } else {
            view.invalidGroupId(validator.getMessage());
            view.setGroupIdValidationState(ValidationState.ERROR);
        }
    }

    public void onArtifactIdChange(final String artifactId) {
        dependency.setArtifactId(artifactId);
        validateArtifactId();
    }

    public void validateArtifactId() {
        if (validator.validateArtifactId()) {
            view.invalidArtifactId("");
            view.setArtifactIdValidationState(ValidationState.SUCCESS);
        } else {
            view.invalidArtifactId(validator.getMessage());
            view.setArtifactIdValidationState(ValidationState.ERROR);
        }
    }

    public void onVersionChange(final String version) {
        dependency.setVersion(version);
        validateVersion();
    }

    public void validateVersion() {
        if (validator.validateVersion()) {
            view.invalidVersion("");
            view.setVersionValidationState(ValidationState.SUCCESS);
        } else {
            view.invalidVersion(validator.getMessage());
            view.setVersionValidationState(ValidationState.ERROR);
        }
    }
}
