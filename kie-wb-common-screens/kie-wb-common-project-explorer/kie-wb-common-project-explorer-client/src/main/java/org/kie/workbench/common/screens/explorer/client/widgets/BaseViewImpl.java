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

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mvp.ParameterizedCommand;

public abstract class BaseViewImpl extends Composite implements View {

    @Override
    public void deleteItem( final ParameterizedCommand<String> command ) {
        final DeletePopup popup = new DeletePopup( command );
        popup.show();
    }

    @Override
    public void renameItem( final Path path,
                            final Validator validator,
                            final CommandWithFileNameAndCommitMessage command,
                            final RenamePopupView renamePopupView ) {
        final RenamePopup popup = new RenamePopup( path,
                                                   validator,
                                                   command,
                                                   renamePopupView );
        popup.show();
    }

    @Override
    public void copyItem( final Path path,
                          final Validator validator,
                          final CommandWithFileNameAndCommitMessage command,
                          final CopyPopupView copyPopupView ) {
        final CopyPopup popup = new CopyPopup( path,
                                               validator,
                                               command,
                                               copyPopupView );
        popup.show();
    }

}
