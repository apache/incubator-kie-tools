package org.uberfire.ext.layout.editor.api;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;

@Remote
public interface LayoutServices {

    String convertLayoutToString( LayoutEditor layoutEditor );

    LayoutEditor convertLayoutFromString( String layoutEditorModel );

}
