package org.kie.uberfire.perspective.editor.model;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface PerspectiveEditorPersistenceAPI {

    Collection<String> listPerspectives( );

    PerspectiveEditor load( String perspectiveName );

    void save( PerspectiveEditor perspective );
}
