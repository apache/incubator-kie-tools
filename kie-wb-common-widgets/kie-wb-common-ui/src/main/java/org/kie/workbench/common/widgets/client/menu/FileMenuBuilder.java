/*
 * Copyright 2012 JBoss Inc
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

import org.kie.workbench.common.services.shared.validation.Validator;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.ResourceTypeDefinition;
import sun.font.LayoutPathImpl;

public interface FileMenuBuilder {


    public enum MenuItems {
        SAVE, DELETE, RENAME, COPY, VALIDATE, RESTORE, CUSTOM
    }

    public Menus build();

    FileMenuBuilder addSave( final Command command );

    FileMenuBuilder addDelete( final Command command );

    FileMenuBuilder addDelete( final Path path );

    FileMenuBuilder addRename( final Command command );

    FileMenuBuilder addRename( final Path path );

    FileMenuBuilder addRename( final Path path,
                               final Validator validator );

    FileMenuBuilder addCopy( final Command command );

    FileMenuBuilder addCopy( final Path path );

    FileMenuBuilder addCopy( final Path path,
                             final Validator validator );

    FileMenuBuilder addValidate( final Command command );

    FileMenuBuilder addRestoreVersion( final Path path );

    FileMenuBuilder addCommand( final String caption,
                                final Command command );

    FileMenuBuilder addNewTopLevelMenu(final MenuItem menu);

}
