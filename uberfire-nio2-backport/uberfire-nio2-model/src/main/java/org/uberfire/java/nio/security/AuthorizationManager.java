package org.uberfire.java.nio.security;

import org.uberfire.java.nio.file.FileSystem;

public interface AuthorizationManager {

    boolean authorize( final FileSystem fs,
                       final Subject subject );

}
