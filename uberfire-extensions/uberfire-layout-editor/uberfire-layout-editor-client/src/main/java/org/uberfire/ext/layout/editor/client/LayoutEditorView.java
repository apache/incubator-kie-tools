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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.components.drag.DragElement;
import org.uberfire.ext.layout.editor.client.components.drag.DynamicLayoutDraggableGroup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Dependent
public class LayoutEditorView extends Composite
        implements UberView<LayoutEditorPresenter>,
        LayoutEditorPresenter.View {

    interface LayoutEditorViewBinder
            extends
            UiBinder<Widget, LayoutEditorView> {

    }

    private static LayoutEditorViewBinder uiBinder = GWT.create( LayoutEditorViewBinder.class );

    protected Map<String, DynamicLayoutDraggableGroup> draggableGroups = new HashMap<>();

    private LayoutEditorPresenter presenter;

    @UiField
    PanelBody components;

    @UiField
    FlowPanel container;


    @UiField
    PanelGroup accordion;

    @UiField
    Anchor anchor;

    @UiField
    PanelCollapse collapseTwo;

    @Inject
    public LayoutEditorView() {
        initWidget( uiBinder.createAndBindUi( this ) );

        accordion.setId( DOM.createUniqueId() );
        anchor.setDataParent( accordion.getId() );
        anchor.setDataTargetWidget( collapseTwo );
    }

    @Override
    public void setupContainer( Widget view ) {
        container.add( view );
    }

    @Override
    public void init( final LayoutEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupComponents( List<LayoutDragComponent> layoutDragComponents ) {
        components.clear();
        for ( LayoutDragComponent layoutDragComponent : layoutDragComponents ) {
            components.add( new DragElement( layoutDragComponent ) );
        }
    }

    @Override
    public void addDraggableComponentGroup( LayoutDragComponentGroup group ) {
        DynamicLayoutDraggableGroup componentGroup = new DynamicLayoutDraggableGroup();
        componentGroup.setName( group.getName() );
        for ( String id : group.getLayoutDragComponentIds() ) {
            LayoutDragComponent component = group.getLayoutDragComponent( id );
            if ( component != null ) {
                componentGroup.addDraggable( id, new DragElement( component ) );
            }
        }

        draggableGroups.put( group.getName(), componentGroup );

        accordion.add( componentGroup );
    }

    @Override
    public void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component ) {
        DynamicLayoutDraggableGroup group = draggableGroups.get( groupId );

        if ( group != null ) {
            group.addDraggable( componentId, new DragElement( component ) );
        }
    }

    @Override
    public void removeDraggableGroup( String id ) {
        DynamicLayoutDraggableGroup group = draggableGroups.remove( id );
        if ( group != null ) {
            group.removeFromParent();
        }
    }

    @Override
    public void removeDraggableComponentFromGroup( String groupId, String componentId ) {
        DynamicLayoutDraggableGroup group = draggableGroups.get( groupId );

        if ( group != null ) {
            group.removeDraggable( componentId );
        }
    }

}