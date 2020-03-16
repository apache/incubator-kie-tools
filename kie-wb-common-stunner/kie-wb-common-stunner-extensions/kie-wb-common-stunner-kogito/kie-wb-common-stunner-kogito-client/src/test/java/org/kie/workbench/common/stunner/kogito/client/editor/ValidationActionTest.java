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
import java.util.HashSet;
import java.util.function.Consumer;

import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidationActionTest {

    @Mock
    private EditorSessionCommands editorSessionCommands;

    @Mock
    private ValidateSessionCommand validateSessionCommand;

    @Captor
    private ArgumentCaptor<ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>> violationsArgumentCapture;

    @Before
    public void setUp() throws Exception {
        doReturn(validateSessionCommand).when(editorSessionCommands).getValidateSessionCommand();
    }

    @Test
    public void loadingStarts() {
        final Command loadingStarts = mock(Command.class);
        new ValidationAction(editorSessionCommands,
                             loadingStarts,
                             () -> { /*  */},
                             s -> { /*  */ }).validate();

        verify(loadingStarts).execute();
        verify(validateSessionCommand).execute(any());
    }

    @Test
    public void loadingCompletedOnSuccess() {
        final Command loadingCompleted = mock(Command.class);
        new ValidationAction(editorSessionCommands,
                             () -> { /*  */},
                             loadingCompleted,
                             s -> { /*  */ }).validate();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());
        violationsArgumentCapture.getValue().onSuccess();
        verify(loadingCompleted).execute();
    }

    @Test
    public void loadingCompletedOnError() {
        final Command loadingCompleted = mock(Command.class);
        final Consumer onError = mock(Consumer.class);
        new ValidationAction(editorSessionCommands,
                             () -> { /*  */},
                             loadingCompleted,
                             onError).validate();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());
        violationsArgumentCapture.getValue().onError(new HashSet<>());
        verify(loadingCompleted).execute();
        verify(onError).accept(eq("[]"));
    }

    @Test
    public void preconditionsPreventValidation() {
        final ValidationAction validationAction = new ValidationAction(editorSessionCommands,
                                                                       () -> { /*  */ },
                                                                       () -> { /*  */ },
                                                                       s -> { /*  */ });
        validationAction.setBeforeValidation(() -> false);

        validationAction.validate();

        verify(validateSessionCommand, never()).execute(any());
    }

    @Test
    public void runAfterValidationActionsOnSuccess() {
        final ValidationAction validationAction = new ValidationAction(editorSessionCommands,
                                                                       () -> { /*  */},
                                                                       () -> { /*  */ },
                                                                       s -> { /*  */ });
        final Command onAfterValidation = mock(Command.class);
        validationAction.setAfterValidation(onAfterValidation);

        validationAction.validate();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());

        violationsArgumentCapture.getValue().onSuccess();

        verify(onAfterValidation).execute();
    }

    @Test
    public void runAfterValidationActionsOnFailure() {
        final ValidationAction validationAction = new ValidationAction(editorSessionCommands,
                                                                       () -> { /*  */ },
                                                                       () -> { /*  */ },
                                                                       s -> { /*  */ });
        final Command onAfterValidation = mock(Command.class);
        validationAction.setAfterValidation(onAfterValidation);

        validationAction.validate();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());

        violationsArgumentCapture.getValue().onError(new HashSet<>());

        verify(onAfterValidation).execute();
    }

    @Test
    public void preventValidationSpam() {
        final ValidationAction validationAction = new ValidationAction(editorSessionCommands,
                                                                       () -> { /*  */ },
                                                                       () -> { /*  */ },
                                                                       s -> { /*  */ });

        // SPAM
        validationAction.validate();
        validationAction.validate();
        validationAction.validate();

        verify(validateSessionCommand).execute(violationsArgumentCapture.capture());

        violationsArgumentCapture.getValue().onSuccess();

        // SPAM
        validationAction.validate();
        validationAction.validate();
        validationAction.validate();

        verify(validateSessionCommand, times(2)).execute(violationsArgumentCapture.capture());

        violationsArgumentCapture.getValue().onError(new HashSet<>());

        // SPAM
        validationAction.validate();
        validationAction.validate();
        validationAction.validate();

        verify(validateSessionCommand, times(3)).execute(violationsArgumentCapture.capture());
    }
}