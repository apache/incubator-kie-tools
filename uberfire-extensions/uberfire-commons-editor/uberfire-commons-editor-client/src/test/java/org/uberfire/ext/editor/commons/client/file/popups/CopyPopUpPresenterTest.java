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
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CopyPopUpPresenterTest {

    @Mock
    Path path;

    @Mock
    Validator validator;

    @Mock
    CommandWithFileNameAndCommitMessage command;

    @Mock
    ToggleCommentPresenter toggleCommentPresenter;

    @Mock
    CopyPopUpPresenter.View view;

    CopyPopUpPresenter presenter;

    @Before
    public void init() throws Exception {
        presenter = new CopyPopUpPresenter(view,
                                           toggleCommentPresenter);
    }

    @Test
    public void testSetup() throws Exception {
        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testShow() throws Exception {
        presenter.show(path,
                       validator,
                       command);

        assertNotNull(presenter.getPath());
        assertNotNull(presenter.getValidator());
        assertNotNull(presenter.getCommand());
        verify(view).show();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingPath() throws Exception {
        presenter.show(null,
                       validator,
                       command);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingValidator() throws Exception {
        presenter.show(path,
                       null,
                       command);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingCommand() throws Exception {
        presenter.show(path,
                       validator,
                       null);
    }

    @Test
    public void testShowWithDefaultValidator() throws Exception {
        presenter.show(path,
                       command);

        assertNotNull(presenter.getPath());
        assertNotNull(presenter.getValidator());
        assertNotNull(presenter.getCommand());
        verify(view).show();
    }

    @Test
    public void testCancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void testCopy() throws Exception {
        when(path.getFileName()).thenReturn("file.plugin");
        when(toggleCommentPresenter.getComment()).thenReturn("comment");

        presenter.show(path,
                       validator,
                       command);
        presenter.copy("newFile");

        verify(validator).validate(eq("newFile.plugin"),
                                   any(ValidatorWithReasonCallback.class));
    }
}