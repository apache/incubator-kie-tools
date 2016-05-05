/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class LayoutEditorPresenter {

    public static final String[] SPANS = {"12", "6 6", "4 4 4"};

    @Inject
    private Event<NotificationEvent> ufNotification;

    private final View view;

    private List<LayoutDragComponent> addedGridSystemComponents = new ArrayList<LayoutDragComponent>( );

    public interface View extends UberView<LayoutEditorPresenter> {

        void setupGridSystem( List<LayoutDragComponent> layoutDragComponents );

        void setupComponents( List<LayoutDragComponent> layoutDragComponents );

        void setupContent( LayoutTemplate layoutTemplate);

        LayoutTemplate getModel();

        void loadDefaultLayout(String layoutName);

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

        void addDraggableComponentGroup( LayoutDragComponentGroup group );

        void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component );

        void removeDraggableGroup( String id );

        void removeDraggableComponentFromGroup( String groupId, String componentId );

    }
    @Inject
    public LayoutEditorPresenter( final View view ) {
        this.view = view;
        view.init( this );
    }

    public UberView<LayoutEditorPresenter> getView() {
        return view;
    }

    public void setupDndPallete(List<LayoutDragComponent> layoutDragComponents ) {
        view.setupComponents( layoutDragComponents );

        List<LayoutDragComponent> gridSystemComponents = new ArrayList<LayoutDragComponent>(  );

        for ( String span : SPANS ) {
            GridLayoutDragComponent component = getGridLayoutDragComponent();
            component.setSpan( span );
            gridSystemComponents.add( component );
        }

        view.setupGridSystem( gridSystemComponents );
    }

    private GridLayoutDragComponent getGridLayoutDragComponent() {
        final GridLayoutDragComponent gridLayoutDragComponent = IOC.getBeanManager().lookupBean(
                GridLayoutDragComponent.class ).newInstance();
        addedGridSystemComponents.add( gridLayoutDragComponent );

        return gridLayoutDragComponent;
    }

    void clearGridSystem() {
        for ( LayoutDragComponent addedGridSystemComponent : addedGridSystemComponents ) {
            IOC.getBeanManager().destroyBean( addedGridSystemComponent );
        }
        addedGridSystemComponents.clear();
    }

    public LayoutTemplate getLayout() {
        return view.getModel();
    }

    public void loadLayout(LayoutTemplate layoutTemplate) {
        view.setupContent(layoutTemplate);
    }

    public void loadDefaultLayout( String layoutName ) {
        view.loadDefaultLayout(layoutName);
    }

    public void addLayoutProperty(String key, String value) {
        view.addLayoutProperty( key, value );
    }

    public String getLayoutProperty(String key) {
        return view.getLayoutProperty( key );
    }

    public void addDraggableComponentGroup( LayoutDragComponentGroup group ) {
        view.addDraggableComponentGroup( group );
    }

    public void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component ) {
        view.addDraggableComponentToGroup( groupId, componentId, component );
    }

    public void removeDraggableGroup( String id ) {
        view.removeDraggableGroup( id );
    }

    public void removeDraggableComponentFromGroup( String groupId, String componentId ) {
        view.removeDraggableComponentFromGroup( groupId, componentId );
    }
}

