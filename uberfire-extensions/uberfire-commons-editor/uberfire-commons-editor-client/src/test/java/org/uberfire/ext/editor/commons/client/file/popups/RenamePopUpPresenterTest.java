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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RenamePopUpPresenterTest {

    @Mock
    Path path;

    @Mock
    Validator validator;

    @Mock
    CommandWithFileNameAndCommitMessage renameCommand;

    @Mock
    CommandWithFileNameAndCommitMessage saveAndRenameCommand;

    @Mock
    ToggleCommentPresenter toggleCommentPresenter;

    @Mock
    RenamePopUpPresenter.View view;

    RenamePopUpPresenter presenter;

    @Before
    public void init() throws Exception {
        presenter = spy(new RenamePopUpPresenter(view, toggleCommentPresenter));
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
                       renameCommand);

        assertNotNull(presenter.getPath());
        assertNotNull(presenter.getValidator());
        assertNotNull(presenter.getRenameCommand());

        verify(presenter).setupView();
        verify(presenter).showView();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingPath() throws Exception {
        presenter.show(null,
                       validator,
                       renameCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingValidator() throws Exception {
        presenter.show(path,
                       null,
                       renameCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingRenameCommand() throws Exception {

        final boolean isDirty = true;
        final String fileName = "file.plugin";

        presenter.show(path,
                       validator,
                       null,
                       saveAndRenameCommand,
                       isDirty,
                       fileName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowMissingSaveAndRenameCommand() throws Exception {

        final boolean isDirty = true;
        final String fileName = "file.plugin";

        presenter.show(path,
                       validator,
                       renameCommand,
                       null,
                       isDirty,
                       fileName);
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
                       renameCommand);

        assertNotNull(presenter.getPath());
        assertNotNull(presenter.getValidator());
        assertNotNull(presenter.getRenameCommand());

        verify(presenter).setupView();
        verify(presenter).showView();
    }

    @Test
    public void testCancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void testRename() throws Exception {

        when(path.getFileName()).thenReturn("file.plugin");

        presenter.show(path, validator, renameCommand);
        presenter.rename("newFile");

        verify(validator).validate(eq("newFile.plugin"), any(ValidatorWithReasonCallback.class));
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final boolean isDirty = true;

        when(path.getFileName()).thenReturn("file.plugin");

        presenter.show(path, validator, isDirty, renameCommand, saveAndRenameCommand);
        presenter.saveAndRename("newFile");

        verify(validator).validate(eq("newFile.plugin"), any(ValidatorWithReasonCallback.class));
    }

    @Test
    public void testEnablePrimaryButtonWhenAssetIsDirty() {

        final boolean isDirty = true;

        doReturn(isDirty).when(presenter).isDirty();

        presenter.enablePrimaryButton();

        verify(view).saveAndRenameAsPrimary();
    }

    @Test
    public void testEnablePrimaryButtonWhenAssetIsNotDirty() {

        final boolean isDirty = false;

        doReturn(isDirty).when(presenter).isDirty();

        presenter.enablePrimaryButton();

        verify(view).renameAsPrimary();
    }

    @Test
    public void testHideSaveAndRenameIfAssetIsNotDirtyWhenAssetIsDirty() {

        final boolean isDirty = true;
        final boolean hidden = false;

        doReturn(isDirty).when(presenter).isDirty();

        presenter.hideSaveAndRenameIfAssetIsNotDirty();

        verify(view).hideSaveAndRename(hidden);
    }

    @Test
    public void testHideSaveAndRenameIfAssetIsNotDirtyWhenAssetIsNotDirty() {

        final boolean isDirty = false;
        final boolean hidden = true;

        doReturn(isDirty).when(presenter).isDirty();

        presenter.hideSaveAndRenameIfAssetIsNotDirty();

        verify(view).hideSaveAndRename(hidden);
    }

    @Test
    public void testSetupView() {

        presenter.setupView();

        verify(presenter).enablePrimaryButton();
        verify(presenter).hideSaveAndRenameIfAssetIsNotDirty();
    }

    @Test
    public void testShowView() {

        final String originalFileName = "originalFileName";

        doReturn(originalFileName).when(presenter).getOriginalFileName();

        presenter.showView();

        verify(view).setOriginalFileName(originalFileName);
        verify(view).show();
    }
}
