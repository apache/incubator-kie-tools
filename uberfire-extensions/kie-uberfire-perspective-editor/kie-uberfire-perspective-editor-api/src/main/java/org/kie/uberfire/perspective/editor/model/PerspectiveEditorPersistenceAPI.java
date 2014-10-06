package org.kie.uberfire.perspective.editor.model;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface PerspectiveEditorPersistenceAPI {

    PerspectiveEditorJSON load( String perspectiveName );

    void save( PerspectiveEditorJSON perspective );
}
