package org.uberfire.client.mvp;

import org.uberfire.mvp.Command;

public interface PerspectiveManager {

    PerspectiveActivity getCurrentPerspective();

    void switchToPerspective( final PerspectiveActivity perspective,
                              final Command doWhenFinished );

}
