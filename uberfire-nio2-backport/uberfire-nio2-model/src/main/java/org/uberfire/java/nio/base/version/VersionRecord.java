package org.uberfire.java.nio.base.version;

import java.util.Date;

/**
 *
 */
public interface VersionRecord {

    String id();

    String author();

    String email();

    String comment();

    Date date();

    String uri();
}
