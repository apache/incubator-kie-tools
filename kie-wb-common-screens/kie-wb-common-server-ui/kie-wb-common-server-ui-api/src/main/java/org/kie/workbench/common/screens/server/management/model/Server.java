package org.kie.workbench.common.screens.server.management.model;

import java.util.Collection;

public interface Server extends ServerRef {

    Collection<Container> containers();

}
