package org.uberfire.ext.plugin.editor;

import java.util.Collection;
import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface PerspectiveEditorPersistenceAPI {

    List<PerspectiveEditor> loadAll();

    Collection<String> listPerspectives();

    PerspectiveEditor load( String perspectiveName );

    void save( PerspectiveEditor perspective );
}
