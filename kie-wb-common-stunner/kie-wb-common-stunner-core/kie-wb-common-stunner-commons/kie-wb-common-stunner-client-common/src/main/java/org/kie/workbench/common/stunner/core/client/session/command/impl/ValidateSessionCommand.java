/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Default
public class ValidateSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private final CanvasDiagramValidator<AbstractCanvasHandler> validator;

    protected ValidateSessionCommand() {
        this(null);
    }

    @Inject
    public ValidateSessionCommand(final CanvasDiagramValidator<AbstractCanvasHandler> validator) {
        super(true);
        this.validator = validator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        final AbstractCanvasHandler canvasHandler = getSession().getCanvasHandler();
        validator.validate(canvasHandler,
                           elementViolations -> fireCallback(elementViolations,
                                                             callback));
    }

    @SuppressWarnings("unchecked")
    private <V> void fireCallback(final Collection<DiagramElementViolation<RuleViolation>> violations,
                                  final Callback<V> callback) {
        final boolean areViolations = violations.stream()
                .filter(v -> Violation.Type.ERROR.equals(v.getViolationType()))
                .findAny()
                .isPresent();
        if (!areViolations) {
            callback.onSuccess();
        } else {
            callback.onError((V) violations);
        }
    }
}
