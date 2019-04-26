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

package org.kie.workbench.common.widgets.metadata.client.validation;

import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

public abstract class BaseAssetUpdateValidator implements Validator {

    @Inject
    protected WorkspaceProjectContext workbenchContext;

    @Inject
    protected ProjectController projectController;

    @Inject
    protected Promises promises;

    @Override
    public void validate(final String value,
                         final ValidatorCallback callback) {
        workbenchContext.getActiveWorkspaceProject().ifPresent(activeProject -> {
            projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    if (value != null) {
                        getFileNameValidator().validate(value,
                                                        callback);
                    } else {
                        callback.onSuccess();
                    }
                } else if (callback instanceof ValidatorWithReasonCallback) {
                    ((ValidatorWithReasonCallback) callback).onFailure(ValidationErrorReason.NOT_ALLOWED.name());
                } else {
                    callback.onFailure();
                }

                return promises.resolve();
            });
        });
    }

    protected abstract Validator getFileNameValidator();
}
