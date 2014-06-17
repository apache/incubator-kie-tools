package org.uberfire.backend.vfs;

public final class PathSupport {

    public static boolean isVersioned( final Path path ) {
        return path instanceof IsVersioned && ( (IsVersioned) path ).hasVersionSupport();
    }

}
