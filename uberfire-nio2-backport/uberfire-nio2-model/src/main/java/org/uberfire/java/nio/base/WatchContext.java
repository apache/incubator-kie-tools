package org.uberfire.java.nio.base;

import org.uberfire.java.nio.file.Path;

public interface WatchContext {

    Path getPath();

    Path getOldPath();

    String getSessionId();

    String getUser();
}
