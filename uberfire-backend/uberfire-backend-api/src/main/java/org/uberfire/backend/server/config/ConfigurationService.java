package org.uberfire.backend.server.config;

import java.util.List;

public interface ConfigurationService {

    public static final String LAST_MODIFIED_MARKER_FILE = ".lastmodified";

    List<ConfigGroup> getConfiguration( ConfigType type );

    boolean addConfiguration( ConfigGroup configGroup );

    boolean updateConfiguration( ConfigGroup configGroup );

    boolean removeConfiguration( ConfigGroup configGroup );

}
