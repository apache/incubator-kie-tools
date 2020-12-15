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

package org.uberfire.ext.editor.commons.client.file.popups;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeletePopUpPresenterTest {

    @Mock
    DeletePopUpPresenter.View view;

    @Mock
    Validator validator;

    @Mock
    ParameterizedCommand<String> command;

    @Mock
    ToggleCommentPresenter toggleCommentPresenter;

    DeletePopUpPresenter presenter;

    @Before
    public void init() throws Exception {
        presenter = new DeletePopUpPresenter(view,
                                             toggleCommentPresenter);
    }

    @Test
    public void testSetup() throws Exception {
        presenter.setup();
        verify(view).init(presenter);
    }

    @Test
    public void testShow() throws Exception {
        presenter.show(validator,
                       command);

        verify(view).show();
        assertEquals(validator,
                     presenter.getValidator());
        assertEquals(command,
                     presenter.getCommand());
    }

    @Test
    public void testDeleteWithCommand() throws Exception {
        when(toggleCommentPresenter.getComment()).thenReturn("test");

        presenter.show((value, callback) -> callback.onSuccess(),
                       command);
        presenter.delete();

        verify(command).execute("test");
        verify(view).hide();
    }

    @Test
    public void testDeleteWithValidationFailed() throws Exception {
        when(toggleCommentPresenter.getComment()).thenReturn("test");

        presenter.show((value, callback) -> callback.onFailure(),
                       command);
        presenter.delete();

        verify(command,
               never()).execute("test");
        verify(view).handleUnexpectedError();
    }

    @Test
    public void testNotAllowedDelete() throws Exception {
        when(toggleCommentPresenter.getComment()).thenReturn("test");

        presenter.show((value, callback) -> ((ValidatorWithReasonCallback) callback).onFailure(ValidationErrorReason.NOT_ALLOWED.name()),
                       command);
        presenter.delete();

        verify(command,
               never()).execute("test");
        verify(view).handleDeleteNotAllowed();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithoutCommand() throws Exception {
        presenter.show(null,
                       null);
        presenter.delete();
    }

    @Test
    public void cancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void testPrompt() throws Exception {
        final String prompt = "any text";
        presenter.setPrompt(prompt);

        verify(view).setPrompt(prompt);
    }

    @Test
    public void testHiddenComment() throws Exception {
        presenter.setCommentIsHidden(true);
        verify(toggleCommentPresenter).setHidden(true);
    }

    @Test
    public void testVisibleComment() throws Exception {
        presenter.setCommentIsHidden(false);
        verify(toggleCommentPresenter).setHidden(false);
    }
}