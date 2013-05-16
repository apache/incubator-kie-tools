package org.uberfire.backend.server.group;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.server.config.ConfigGroup;

public interface GroupFactory {

    Group newGroup(ConfigGroup groupConfig);
}
