package org.uberfire.java.nio.base.version;

import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public interface VersionAttributes extends BasicFileAttributes {

    VersionHistory history();

}
