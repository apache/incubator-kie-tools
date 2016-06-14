/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageViewImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
public class
        FileMenuBuilderImpl
        implements FileMenuBuilder {

    @Inject
    private BasicFileMenuBuilder menuBuilder;

    @Inject
    private Caller<DeleteService> deleteService;

    @Inject
    private Caller<RenameService> renameService;

    @Inject
    private Caller<CopyService> copyService;

    @Inject
    private CopyPopupWithPackageViewImpl copyPopupView;

    @Override
    public Menus build() {
        return menuBuilder.build();
    }

    @Override
    public FileMenuBuilder addSave( final MenuItem menuItem ) {
        menuBuilder.addSave( menuItem );
        return this;
    }

    @Override
    public FileMenuBuilder addSave( final Command command ) {
        menuBuilder.addSave( command );
        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final Command command ) {
        menuBuilder.addDelete( command );
        return this;
    }

    @Override
    public FileMenuBuilder addRename( final Command command ) {
        menuBuilder.addRename( command );
        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final Command command ) {
        menuBuilder.addCopy( command );
        return this;
    }

    @Override
    public FileMenuBuilder addValidate( final Command command ) {
        menuBuilder.addValidate( command );
        return this;
    }

    @Override
    public FileMenuBuilder addRestoreVersion( final Path path ) {
        menuBuilder.addRestoreVersion( path );
        return this;
    }

    @Override
    public FileMenuBuilder addCommand( final String caption,
                                       final Command command ) {
        menuBuilder.addCommand( caption, command );
        return this;
    }

    @Override
    public FileMenuBuilder addNewTopLevelMenu( final MenuItem menu ) {
        menuBuilder.addNewTopLevelMenu( menu );
        return this;
    }

    @Override
    public FileMenuBuilderImpl addDelete( final Path path ) {
        menuBuilder.addDelete( path, deleteService );

        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final BasicFileMenuBuilder.PathProvider provider ) {
        menuBuilder.addDelete( provider, deleteService );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addRename( final Path path ) {
        menuBuilder.addRename( path, renameService );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addRename( final Path path,
                                          final Validator validator ) {
        menuBuilder.addRename( path, validator, renameService );

        return this;
    }

    @Override
    public FileMenuBuilder addRename( final BasicFileMenuBuilder.PathProvider provider,
                                      final Validator validator ) {
        menuBuilder.addRename( provider, validator, renameService );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addCopy( final Path path ) {
        menuBuilder.addCopy( path, copyService );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addCopy( final Path path,
                                        final Validator validator ) {
        menuBuilder.addCopy( path, validator, copyService, copyPopupView );

        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final BasicFileMenuBuilder.PathProvider provider,
                                    final Validator validator ) {
        menuBuilder.addCopy( provider, validator, copyService, copyPopupView );

        return this;
    }

    @Override
    public void setLockSyncMenuStateHelper( final LockSyncMenuStateHelper lockSyncMenuStateHelper ) {
        menuBuilder.setLockSyncMenuStateHelper( lockSyncMenuStateHelper );
    }

}
