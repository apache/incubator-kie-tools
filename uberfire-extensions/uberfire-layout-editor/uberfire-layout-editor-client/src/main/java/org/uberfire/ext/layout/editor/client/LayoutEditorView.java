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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.DynamicLayoutDraggableGroup;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.dnd.DragGridElement;
import org.uberfire.ext.layout.editor.client.dnd.DropRowPanel;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class LayoutEditorView extends Composite
        implements UberView<LayoutEditorPresenter>,
                   LayoutEditorPresenter.View {

    interface LayoutEditorViewBinder
            extends
            UiBinder<Widget, LayoutEditorView> {

    }

    private static LayoutEditorViewBinder uiBinder = GWT.create( LayoutEditorViewBinder.class );

    private LayoutEditorPresenter presenter;

    LayoutEditorWidget layoutEditorWidget;

    protected Map<String, DynamicLayoutDraggableGroup> draggableGroups = new HashMap<String, DynamicLayoutDraggableGroup>(  );

    @UiField
    PanelBody gridSystem;

    @UiField
    PanelBody components;

    @UiField
    FlowPanel container;

    //needed to setup ids
    @UiField
    PanelGroup accordion;

    @UiField
    Anchor anchor1;

    @UiField
    PanelCollapse collapseOne;

    @UiField
    Anchor anchor2;

    @UiField
    PanelCollapse collapseTwo;

    // For proxying
    protected LayoutEditorView() {
    }

    @Inject
    public LayoutEditorView( LayoutEditorWidget layoutEditorWidget) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.layoutEditorWidget = layoutEditorWidget;

        accordion.setId( DOM.createUniqueId() );
        anchor1.setDataParent( accordion.getId() );
        anchor1.setDataTargetWidget( collapseOne );

        anchor2.setDataParent( accordion.getId() );
        anchor2.setDataTargetWidget( collapseTwo );
    }

    @Override
    public void init( final LayoutEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupGridSystem( List<LayoutDragComponent> layoutDragComponents ) {
        gridSystem.clear();
        presenter.clearGridSystem();
        for ( LayoutDragComponent layoutDragComponent : layoutDragComponents ) {
            gridSystem.add( new DragGridElement( layoutDragComponent ) );
        }
    }

    @Override
    public void setupComponents( List<LayoutDragComponent> layoutDragComponents ) {
        components.clear();
        for ( LayoutDragComponent layoutDragComponent : layoutDragComponents ) {
            components.add( new DragGridElement( layoutDragComponent ) );
        }
    }

    @Override
    public void setupContent( LayoutTemplate layoutTemplate ) {
        container.clear();
        layoutEditorWidget.setup( container, layoutTemplate );
        for ( LayoutRow row : layoutTemplate.getRows() ) {
            container.add( createRowView( row ) );
        }
        container.add( createDropRowPanel() );
    }

    private DropRowPanel createDropRowPanel() {
        return new DropRowPanel( layoutEditorWidget );
    }

    RowView createRowView( LayoutRow row ) {
        return new RowView( layoutEditorWidget, row );
    }

    @Override
    public LayoutTemplate getModel() {
        return layoutEditorWidget.toLayoutTemplate();
    }

    @Override
    public void loadDefaultLayout( String layoutName ) {
        setupContent( LayoutTemplate.defaultLayout( layoutName ) );
    }

    @Override
    public void addLayoutProperty( String key,
                                   String value ) {
        layoutEditorWidget.addLayoutProperty( key, value );
    }

    @Override
    public String getLayoutProperty( String key ) {
        return layoutEditorWidget.getLayoutProperty( key );
    }

    @Override
    public Map<String, String> getLayoutComponentProperties( EditorWidget component ) {
        return layoutEditorWidget.getLayoutComponentProperties( component );
    }

    @Override
    public void addComponentProperty( EditorWidget component,
                                      String key,
                                      String value ) {
        layoutEditorWidget.addPropertyToLayoutComponent( component, key, value );
    }

    @Override
    public void resetLayoutComponentProperties( EditorWidget component ) {
        layoutEditorWidget.resetLayoutComponentProperties( component );
    }

    @Override
    public void removeLayoutComponentProperty( EditorWidget component,
                                               String key ) {
        layoutEditorWidget.removeLayoutComponentProperty( component, key );
    }

    @Override
    public void addDraggableComponentGroup( LayoutDragComponentGroup group ) {
        DynamicLayoutDraggableGroup componentGroup = new DynamicLayoutDraggableGroup();
        componentGroup.setName( group.getName() );
        for ( String id : group.getLayoutDragComponentIds() ) {
            LayoutDragComponent component = group.getLayoutDragComponent( id );
            if ( component != null ) {
                componentGroup.addDraggable(  id, new DragGridElement( component ) );
            }
        }

        draggableGroups.put( group.getName(), componentGroup );

        accordion.add( componentGroup );
    }

    @Override
    public void addDraggableComponentToGroup( String groupId, String componentId, LayoutDragComponent component ) {
        DynamicLayoutDraggableGroup group = draggableGroups.get( groupId );

        if (group != null) group.addDraggable( componentId, new DragGridElement( component ) );
    }

    @Override
    public void removeDraggableGroup( String id ) {
        DynamicLayoutDraggableGroup group = draggableGroups.remove( id );

        if (group != null) group.removeFromParent();
    }

    @Override
    public void removeDraggableComponentFromGroup( String groupId, String componentId ) {
        DynamicLayoutDraggableGroup group = draggableGroups.get( groupId );

        if (group != null) group.removeDraggable( componentId );
    }
}