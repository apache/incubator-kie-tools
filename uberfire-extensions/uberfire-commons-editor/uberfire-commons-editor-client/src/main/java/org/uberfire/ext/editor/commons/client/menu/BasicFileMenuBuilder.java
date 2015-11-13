/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public interface BasicFileMenuBuilder {

    Menus build();

    BasicFileMenuBuilder addSave( final MenuItem menuItem );

    BasicFileMenuBuilder addSave( final Command command );

    BasicFileMenuBuilder addDelete( final Path path,
                                    final Caller<? extends SupportsDelete> deleteCaller );

    BasicFileMenuBuilder addDelete( final Command command );

    BasicFileMenuBuilder addRename( final Command command );

    BasicFileMenuBuilder addRename( final Path path,
                                    final Caller<? extends SupportsRename> renameCaller );

    BasicFileMenuBuilder addRename( final Path path,
                                    final Validator validator,
                                    final Caller<? extends SupportsRename> renameCaller );

    BasicFileMenuBuilder addCopy( final Command command );

    BasicFileMenuBuilder addCopy( final Path path,
                                  final Caller<? extends SupportsCopy> copyCaller );

    BasicFileMenuBuilder addCopy( final Path path,
                                  final Validator validator,
                                  final Caller<? extends SupportsCopy> copyCaller );

    BasicFileMenuBuilder addValidate( final Command command );

    BasicFileMenuBuilder addRestoreVersion( final Path path );

    BasicFileMenuBuilder addCommand( final String caption,
                                     final Command command );

    BasicFileMenuBuilder addNewTopLevelMenu( final MenuItem menu );

}
