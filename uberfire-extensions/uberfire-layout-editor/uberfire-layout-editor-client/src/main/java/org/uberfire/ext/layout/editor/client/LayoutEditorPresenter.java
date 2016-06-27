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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class LayoutEditorPresenter {

    public interface View extends UberView<LayoutEditorPresenter> {

        void setupContainer( Widget view );

        void setupComponents( List<LayoutDragComponent> layoutDragComponents );

        void addDraggableComponentGroup( LayoutDragComponentGroup group );

        void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component );

        void removeDraggableGroup( String id );

        void removeDraggableComponentFromGroup( String groupId, String componentId );

    }

    @Inject
    private Container container;

    @Inject
    private Event<NotificationEvent> ufNotification;

    private final View view;


    private List<LayoutDragComponent> addedGridSystemComponents = new ArrayList<LayoutDragComponent>();

    @Inject
    public LayoutEditorPresenter( final View view, Container container ) {
        this.view = view;
        this.container = container;
        view.init( this );
    }


    @PostConstruct
    public void initNew() {
        view.setupContainer( container.getView().asWidget() );
    }

    public UberView<LayoutEditorPresenter> getView() {
        return view;
    }

    public void setupDndPallete( List<LayoutDragComponent> layoutDragComponents ) {
        view.setupComponents( layoutDragComponents );
    }

    public LayoutTemplate getLayout() {
        return container.toLayoutTemplate();
    }

    public void loadLayout( LayoutTemplate layoutTemplate ) {
        container.load( layoutTemplate );
    }

    public void loadEmptyLayout( String layoutName ) {
        container.setLayoutName( layoutName );
    }

    public void addLayoutProperty( String key, String value ) {
        container.addProperty( key, value );
    }

    public String getLayoutProperty( String key ) {
        return container.getProperty( key );
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