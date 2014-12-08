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
package org.uberfire.ext.editor.commons.client.menu;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public interface BasicFileMenuBuilder<T> {

    Menus build();

    T addSave( final MenuItem menuItem );

    T addSave( final Command command );

    T addDelete( final Command command );

    T addRename( final Command command );

    T addCopy( final Command command );

    T addValidate( final Command command );

    T addRestoreVersion( final Path path );

    T addCommand( final String caption,
                  final Command command );

    T addNewTopLevelMenu( final MenuItem menu );

}
