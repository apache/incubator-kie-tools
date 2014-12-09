package org.kie.workbench.common.widgets.client.menu;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;

public interface FileMenuBuilder extends BasicFileMenuBuilder<FileMenuBuilder> {

    FileMenuBuilder addDelete( final Path path );

    FileMenuBuilder addRename( final Path path );

    FileMenuBuilder addRename( final Path path,
                               final Validator validator );

    FileMenuBuilder addCopy( final Path path );

    FileMenuBuilder addCopy( final Path path,
                             final Validator validator );

}
