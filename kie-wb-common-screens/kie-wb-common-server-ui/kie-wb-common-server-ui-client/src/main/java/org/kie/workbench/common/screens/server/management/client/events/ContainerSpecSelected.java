package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ContainerSpecKey;

/**
 * TODO: update me
 */
public class ContainerSpecSelected {

    final ContainerSpecKey containerSpecKey;

    public ContainerSpecSelected( final ContainerSpecKey containerSpecKey ) {
        this.containerSpecKey = containerSpecKey;
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ContainerSpecSelected ) ) {
            return false;
        }

        final ContainerSpecSelected that = (ContainerSpecSelected) o;

        return containerSpecKey.equals( that.containerSpecKey );

    }

    @Override
    public int hashCode() {
        return containerSpecKey.hashCode();
    }
}
