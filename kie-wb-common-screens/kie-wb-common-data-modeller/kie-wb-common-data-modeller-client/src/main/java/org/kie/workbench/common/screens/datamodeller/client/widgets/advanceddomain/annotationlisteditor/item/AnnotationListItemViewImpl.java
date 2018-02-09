/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;

@Dependent
public class AnnotationListItemViewImpl
        extends Composite
        implements AnnotationListItemView {

    interface AnnotationListItemViewImplUIBinder
            extends UiBinder< Widget, AnnotationListItemViewImpl > {

    }

    private static AnnotationListItemViewImplUIBinder uiBinder = GWT.create( AnnotationListItemViewImplUIBinder.class );

    @UiField
    PanelCollapse collapsePanel;

    @UiField
    Button deleteButton;

    @UiField
    Anchor collapseAnchor;

    @UiField
    PanelBody collapsePanelBody;

    private Presenter presenter;

    private boolean collapsed = true;

    @Inject
    public AnnotationListItemViewImpl( ) {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void init( ) {
        deleteButton.addClickHandler( new ClickHandler( ) {
            @Override
            public void onClick( final ClickEvent clickEvent ) {
                if ( presenter != null ) {
                    presenter.onDelete( );
                }
            }
        } );
        deleteButton.setPull( Pull.RIGHT );
        deleteButton.setIcon( IconType.TRASH );
        deleteButton.setType( ButtonType.DANGER );
        deleteButton.setSize( ButtonSize.SMALL );
        deleteButton.getElement( ).getStyle( ).setMarginTop( -4, Style.Unit.PX );

        collapseAnchor.addClickHandler( new ClickHandler( ) {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                setCollapsed( !isCollapsed( ) );
                presenter.onCollapseChange( );
            }
        } );
        setCollapsed( true );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadonly( boolean readonly ) {
        deleteButton.setEnabled( !readonly );
    }

    @Override
    public void setHeadingTitle( String fqcn ) {
        collapseAnchor.setText( "@" + fqcn.replaceAll(".*\\.", "") );
        collapseAnchor.setTitle( fqcn );
    }

    @Override
    public void setCollapsed( boolean collapsed ) {
        this.collapsed = collapsed;
        if ( collapsed ) {
            collapseAnchor.addStyleName( "collapsed" );
            collapsePanel.setIn( false );
        } else {
            collapseAnchor.removeStyleName( "collapsed" );
            collapsePanel.setIn( true );
        }
    }

    @Override
    public boolean isCollapsed( ) {
        return collapsed;
    }

    @Override
    public void addItem( AnnotationValuePairListItem valuePairListItem ) {
        collapsePanelBody.add( valuePairListItem );
    }
}