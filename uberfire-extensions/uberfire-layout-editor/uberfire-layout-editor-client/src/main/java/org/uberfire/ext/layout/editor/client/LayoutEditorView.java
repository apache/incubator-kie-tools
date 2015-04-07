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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;
import org.uberfire.ext.layout.editor.client.dnd.DragGridElement;
import org.uberfire.ext.layout.editor.client.dnd.DropRowPanel;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorUI;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

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

    LayoutEditorUI layoutEditorUI;

    @UiField
    AccordionGroup gridSystem;

    @UiField
    AccordionGroup components;

    @UiField
    FlowPanel container;

    @Inject
    public LayoutEditorView( LayoutEditorUI layoutEditorUI ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.layoutEditorUI = layoutEditorUI;
    }

    @Override
    public void init( final LayoutEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupGridSystem( LayoutDragComponent... layoutDragComponents ) {
        for ( LayoutDragComponent layoutDragComponent : layoutDragComponents ) {
            gridSystem.add( new DragGridElement( layoutDragComponent ) );
        }
    }

    @Override
    public void setupComponents( LayoutDragComponent... layoutDragComponents ) {
        for ( LayoutDragComponent layoutDragComponent : layoutDragComponents ) {
            components.add( new DragGridElement( layoutDragComponent ) );
        }
    }

    @Override
    public void setupContent( LayoutEditor layoutEditor ) {
        container.clear();
        layoutEditorUI.setup( container, layoutEditor );
        for ( RowEditor row : layoutEditor.getRows() ) {
            container.add( createRowView( row ) );
        }
        container.add( createDropRowPanel() );
    }

    private DropRowPanel createDropRowPanel() {
        return new DropRowPanel( layoutEditorUI );
    }

    RowView createRowView( RowEditor row ) {
        return new RowView( layoutEditorUI, row );
    }

    @Override
    public LayoutEditor getModel() {
        return layoutEditorUI.toLayoutEditor();
    }

    @Override
    public int getCurrentModelHash() {
        return layoutEditorUI.toLayoutEditor().hashCode();
    }

    @Override
    public void loadDefaultContent( String pluginName ) {
        setupContent( LayoutEditor.defaultContent( pluginName ) );
    }

    @Override
    public void addLayoutProperty( String key,
                                   String value ) {
        layoutEditorUI.addLayoutProperty( key, value );
    }

    @Override
    public String getLayoutProperty( String key ) {
        return layoutEditorUI.getLayoutProperty( key );
    }

    @Override
    public Map<String, String> getLayoutComponentProperties( EditorWidget component ) {
        return layoutEditorUI.getLayoutComponentProperties( component );
    }

    @Override
    public void addComponentProperty( EditorWidget component,
                                      String key,
                                      String value ) {
        layoutEditorUI.addPropertyToLayoutComponent( component, key, value );
    }

    @Override
    public void addPropertyToLayoutComponentByKey( String addPropertyToLayoutComponentByKey,
                                                   String key,
                                                   String value ) {
        layoutEditorUI.addPropertyToLayoutComponentByKey( addPropertyToLayoutComponentByKey, key, value );
    }

    @Override
    public void resetLayoutComponentProperties( EditorWidget component ) {
        layoutEditorUI.resetLayoutComponentProperties( component );
    }

    @Override
    public void removeLayoutComponentProperty( EditorWidget component,
                                               String key ) {
        layoutEditorUI.removeLayoutComponentProperty( component, key );
    }

}