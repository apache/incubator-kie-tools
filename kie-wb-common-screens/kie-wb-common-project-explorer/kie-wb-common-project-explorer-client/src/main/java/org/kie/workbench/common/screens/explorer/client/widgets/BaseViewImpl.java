/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mvp.ParameterizedCommand;

public abstract class BaseViewImpl extends Composite implements View {

    @Inject
    private DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    private RenamePopUpPresenter renamePopUpPresenter;

    @Inject
    private CopyPopUpPresenter copyPopUpPresenter;

    @Override
    public void deleteItem( final ParameterizedCommand<String> command ) {
        deletePopUpPresenter.show( command );
    }

    @Override
    public void renameItem( final Path path,
                            final Validator validator,
                            final CommandWithFileNameAndCommitMessage command ) {
        renamePopUpPresenter.show( path, validator, command );
    }

    @Override
    public void copyItem( final Path path,
                          final Validator validator,
                          final CommandWithFileNameAndCommitMessage command ) {
        copyPopUpPresenter.show( path, validator, command );
    }

    public abstract void showContent(final boolean isVisible);
}
