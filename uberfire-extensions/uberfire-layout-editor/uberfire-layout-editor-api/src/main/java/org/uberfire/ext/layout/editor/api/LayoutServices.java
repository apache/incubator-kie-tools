package org.uberfire.ext.layout.editor.api;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Remote
public interface LayoutServices {

    String convertLayoutToString( LayoutTemplate layoutTemplate);

    LayoutTemplate convertLayoutFromString( String layoutEditorModel );

}
