package org.uberfire.ext.editor.commons.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface ValidationService {

    boolean isFileNameValid( final Path path,
                             final String fileName );

    boolean isFileNameValid( final String fileName );

}
