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

package org.uberfire.ext.layout.editor.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.dnd.DropColumnPanel;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.ComponentEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;

public class LayoutComponentView extends Composite {

    private LayoutDragComponent type;
    private ComponentEditorWidget componentEditorWidget;

    @UiField
    Container fluidContainer;

    private boolean newComponent;
    private ColumnEditorWidget parent;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, LayoutComponentView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public LayoutComponentView( ColumnEditorWidget parent,
                                LayoutDragComponent type,
                                boolean newComponent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        this.parent = parent;
        this.componentEditorWidget = new ComponentEditorWidget( parent, fluidContainer, type );
        this.newComponent = newComponent;

        init();
    }

    public LayoutComponentView( ColumnEditorWidget parent,
                                LayoutComponent component,
                                LayoutDragComponent type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.type = type;
        this.componentEditorWidget = new ComponentEditorWidget( parent, fluidContainer, type );

        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        layoutEditorWidget.registerLayoutComponent( this.componentEditorWidget, component );
        init();
    }

    public ComponentEditorWidget getEditorWidget() {
        return componentEditorWidget;
    }

    public boolean isNewComponent() {
        return newComponent;
    }

    public void init() {
        if ( !newComponent ) {
            componentEditorWidget.getWidget().clear();
            componentEditorWidget.getWidget().add( generateMainRow() );
        } else if ( type instanceof HasConfiguration ) {
            showConfigurationScreen();
        }
    }

    public void update() {
        newComponent = false;
        init();
    }

    public void remove() {
        removeThisWidgetFromParent();
        addDropColumnPanel();
    }

    private Row generateMainRow() {
        Row rowWidget = new Row();

        generateHeaderRow( rowWidget );

        generateLayoutComponentPreview( rowWidget );

        return rowWidget;
    }

    private void generateLayoutComponentPreview( Row rowWidget ) {
        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        LayoutComponent layoutComponent = layoutEditorWidget.getLayoutComponent( componentEditorWidget );
        RenderingContext renderingContext = new RenderingContext( layoutComponent, parent.getWidget() );
        IsWidget previewWidget = type.getPreviewWidget( renderingContext );

        Column buttonColumn = new Column( ColumnSize.MD_12 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "left" );
        if ( previewWidget != null ) {
            buttonColumn.add( previewWidget );
        }
        rowWidget.add( buttonColumn );
    }

    private void generateHeaderRow( Row rowWidget ) {
        final Column header = new Column( ColumnSize.MD_12 );
        header.getElement().getStyle().setProperty( "textAlign", "right" );
        header.getElement().getStyle().setProperty( "marginBottom", "3px" );
        if ( type instanceof HasConfiguration ) {
            header.add( generateConfigureButton() );
        }
        header.add( generateRemoveButton() );
        rowWidget.add( header );
    }

    private Button generateConfigureButton() {
        Button remove = GWT.create( Button.class );
        remove.setSize( ButtonSize.EXTRA_SMALL );
        remove.setType( ButtonType.PRIMARY );
        remove.setIcon( IconType.EDIT );
        remove.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                showConfigurationScreen();
            }
        } );
        return remove;
    }

    private void showConfigurationScreen() {
        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        LayoutComponent layoutComponent = layoutEditorWidget.getLayoutComponent( componentEditorWidget );

        if ( type instanceof HasModalConfiguration ) {
            ModalConfigurationContext ctx = new ModalConfigurationContext( layoutComponent, fluidContainer, this );
            Modal configModal = ( ( HasModalConfiguration ) type ).getConfigurationModal( ctx );
            configModal.show();
        } else if ( type instanceof HasPanelConfiguration ) {
            PanelConfigurationContext ctx = new PanelConfigurationContext( layoutComponent, fluidContainer, this );
            Panel configPanel = ( ( HasPanelConfiguration ) type ).getConfigurationPanel( ctx );
            componentEditorWidget.getWidget().clear();
            componentEditorWidget.getWidget().add( configPanel );
        }
    }

    private Button generateRemoveButton() {
        Button remove = GWT.create( Button.class );
        remove.setSize( ButtonSize.EXTRA_SMALL );
        remove.setType( ButtonType.DANGER );
        remove.setIcon( IconType.REMOVE );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );

        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                remove();
            }
        } );
        return remove;
    }

    private void removeThisWidgetFromParent() {
        parent.getWidget().remove( this );
        parent.getWidget().getElement().getStyle().clearWidth();
        parent.getWidget().getElement().getStyle().clearHeight();
        componentEditorWidget.removeFromParent();
        if ( type instanceof HasOnRemoveNotification ) {
            ( ( HasOnRemoveNotification ) type ).onRemoveComponent();
        }
    }

    private void addDropColumnPanel() {
        parent.getWidget().add( new DropColumnPanel( parent ) );
    }

    protected LayoutEditorWidget getLayoutEditorWidget() {
        EditorWidget target = parent;
        while ( target != null ) {
            if ( target instanceof LayoutEditorWidget ) {
                return ( LayoutEditorWidget ) target;
            }
            target = target.getParent();
        }
        GWT.log( "LayoutEditorWidget not found!" );
        return null;
    }
}