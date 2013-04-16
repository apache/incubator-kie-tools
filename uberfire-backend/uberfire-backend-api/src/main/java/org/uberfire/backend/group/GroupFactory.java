package org.uberfire.backend.group;

import org.uberfire.backend.server.config.ConfigGroup;

public interface GroupFactory {

    Group newGroup(ConfigGroup groupConfig);
}
