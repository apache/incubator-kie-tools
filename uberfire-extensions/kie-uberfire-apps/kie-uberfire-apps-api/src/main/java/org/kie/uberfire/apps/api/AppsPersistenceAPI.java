package org.kie.uberfire.apps.api;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface AppsPersistenceAPI {

    public Directory getRootDirectory();

    Directory createDirectory( Directory parentDirectory,
                               String parameter );
}
