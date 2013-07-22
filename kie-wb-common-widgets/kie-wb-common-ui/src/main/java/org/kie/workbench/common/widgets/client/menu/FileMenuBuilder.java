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

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.Menus;

public interface FileMenuBuilder {

    public Menus build();

    FileMenuBuilder addSave( final Command command );

    FileMenuBuilder addDelete( final Command command );

    FileMenuBuilder addDelete( final Path path );

    FileMenuBuilder addRename( final Command command );

    FileMenuBuilder addRename( final Path path );

    FileMenuBuilder addCopy( final Command command );

    FileMenuBuilder addCopy( final Path path );

    FileMenuBuilder addValidate( final Command command );

    FileMenuBuilder addRestoreVersion( final Path path );

    FileMenuBuilder addCommand( final String caption,
                                final Command command );

}
