package org.kie.workbench.common.widgets.client.menu;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public interface FileMenuBuilder {

    FileMenuBuilder addSave( final MenuItem menuItem );

    FileMenuBuilder addSave( final Command command );

    FileMenuBuilder addNewTopLevelMenu( final MenuItem menu );

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

    Menus build();
}
