package org.uberfire.ext.layout.editor.client;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;
import org.uberfire.ext.plugin.model.PluginType;

public interface LayoutEditorAPI {

    void init( PluginType pluginType,
               String layoutName,
               LayoutDragComponent... layoutDragComponent );

    Widget asWidget();

    LayoutEditor getModel();

    void addLayoutProperty( String key,
                            String value );

    String getLayoutProperty( String key );

    Map<String, String> getLayoutComponentProperties( EditorWidget component );

    void addLayoutComponentProperty( EditorWidget component,
                                     String key,
                                     String text );

    void removeLayoutComponentProperty( EditorWidget component,
                                        String key );

    void resetLayoutComponentProperties( EditorWidget component );

    void addPropertyToLayoutComponentByKey( String componentKey,
                                            String key,
                                            String value );
}
