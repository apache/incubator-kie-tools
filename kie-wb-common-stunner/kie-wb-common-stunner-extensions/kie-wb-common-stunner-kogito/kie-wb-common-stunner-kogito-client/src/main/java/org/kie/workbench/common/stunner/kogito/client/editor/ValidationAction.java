/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.kogito.client.editor;

import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.google.gwt.user.client.Command;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ValidationAction {

    private final EditorSessionCommands sessionCommands;
    private final Command loadingStarts;
    private final Command loadingCompleted;
    private final Consumer<String> onError;

    private boolean isValidationRunning = false;
    private Command onAfterValidation = null;
    private BooleanSupplier onBeforeValidation = null;

    public ValidationAction(final EditorSessionCommands sessionCommands,
                            final Command loadingStarts,
                            final Command loadingCompleted,
                            final Consumer<String> onError) {
        this.sessionCommands = checkNotNull("sessionCommands", sessionCommands);
        this.loadingStarts = checkNotNull("loadingStarts", loadingStarts);
        this.loadingCompleted = checkNotNull("loadingCompleted", loadingCompleted);
        this.onError = checkNotNull("onError", onError);
    }

    private boolean onBeforeValidate() {

        if (onBeforeValidation != null) {
            return onBeforeValidation.getAsBoolean();
        } else {
            return true;
        }
    }

    private void onAfterValidation() {

        if (onAfterValidation != null) {
            onAfterValidation.execute();
        }
        isValidationRunning = false;
    }

    public void validate() {
        if (!isValidationRunning && onBeforeValidate()) {
            isValidationRunning = true;

            loadingStarts.execute();
            sessionCommands.getValidateSessionCommand().execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
                @Override
                public void onSuccess() {
                    loadingCompleted.execute();
                    onAfterValidation();
                }

                @Override
                public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                    loadingCompleted.execute();
                    onError.accept(violations.toString());
                    onAfterValidation();
                }
            });
        }
    }

    public void setBeforeValidation(final BooleanSupplier onBeforeValidation) {
        this.onBeforeValidation = onBeforeValidation;
    }

    public void setAfterValidation(final Command onAfterValidation) {
        this.onAfterValidation = onAfterValidation;
    }
}
