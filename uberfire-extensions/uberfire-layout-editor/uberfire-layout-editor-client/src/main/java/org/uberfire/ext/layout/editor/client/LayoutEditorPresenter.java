/*
* Copyright 2012 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.uberfire.ext.layout.editor.client;

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.util.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class LayoutEditorPresenter {

    @Inject
    private Event<NotificationEvent> ufNotification;

    private final View view;

    public interface View extends UberView<LayoutEditorPresenter> {

        void setupGridSystem( LayoutDragComponent... layoutDragComponents );

        void setupComponents( LayoutDragComponent... layoutDragComponent );

        void setupContent( LayoutEditor layoutEditor );

        LayoutEditor getModel();

        int getCurrentModelHash();

        void loadDefaultContent( String pluginName );

        void addLayoutProperty( String key,
                                String value );

        String getLayoutProperty( String key );

        Map<String, String> getLayoutComponentProperties( EditorWidget component );

        void addComponentProperty( EditorWidget component,
                                   String key,
                                   String value );

        void resetLayoutComponentProperties( EditorWidget component );

        void removeLayoutComponentProperty( EditorWidget component,
                                            String key );

        void addPropertyToLayoutComponentByKey( String componentKey,
                                                String key,
                                                String value );
    }

    @Inject
    public LayoutEditorPresenter( final View view ) {
        this.view = view;
    }

    public UberView<LayoutEditorPresenter> getView() {
        return view;
    }

    public void setupDndPallete( LayoutDragComponent[] layoutDragComponent ) {
        view.setupGridSystem( new GridLayoutDragComponent( "12", ufNotification ), new GridLayoutDragComponent( "6 6", ufNotification ), new GridLayoutDragComponent( "4 4 4", ufNotification ) );
        view.setupComponents( layoutDragComponent );
    }

    public LayoutEditor getModel() {
        return view.getModel();
    }

    public void loadLayoutEditor( LayoutEditor layoutEditor ) {
        view.setupContent( layoutEditor );
    }

    public void loadDefaultLayout( String pluginName ) {
        view.loadDefaultContent( pluginName );
    }

    public int getCurrentModelHash() {
        return view.getCurrentModelHash();
    }

    public void addLayoutProperty( String key,
                                   String value ) {
        view.addLayoutProperty( key, value );
    }

    public String getLayoutProperty( String key ) {
        return view.getLayoutProperty( key );
    }

    public Map<String, String> getLayoutComponentProperties( EditorWidget component ) {
        return view.getLayoutComponentProperties( component );
    }

    public void addComponentProperty( EditorWidget component,
                                      String key,
                                      String value ) {
        view.addComponentProperty( component, key, value );
    }

    public void addPropertyToLayoutComponentByKey( String componentKey,
                                                   String key,
                                                   String value ) {
        view.addPropertyToLayoutComponentByKey( componentKey, key, value );
    }

    public void resetLayoutComponentProperties( EditorWidget component ) {
        view.resetLayoutComponentProperties( component );
    }

    public void removeLayoutComponentProperty( EditorWidget component,
                                               String key ) {
        view.removeLayoutComponentProperty( component, key );
    }

}

