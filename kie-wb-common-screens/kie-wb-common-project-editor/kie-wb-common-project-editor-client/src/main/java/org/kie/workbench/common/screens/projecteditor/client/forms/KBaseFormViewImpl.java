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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.PageHeader;
import org.gwtbootstrap3.client.ui.Radio;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

//@Dependent
public class KBaseFormViewImpl
        extends Composite
        implements KBaseFormView {

    private Presenter presenter;

    interface KnowledgeBaseConfigurationFormViewImplBinder
            extends
            UiBinder<Widget, KBaseFormViewImpl> {

    }

    private static KnowledgeBaseConfigurationFormViewImplBinder uiBinder = GWT.create( KnowledgeBaseConfigurationFormViewImplBinder.class );

    @UiField( provided = true )
    CRUDListBox includesListBox;

    @UiField( provided = true )
    CRUDListBox packagesListBox;

    @UiField
    PageHeader nameLabel;

    @UiField
    Radio equalsBehaviorIdentity;

    @UiField
    Radio equalsBehaviorEquality;

    @UiField
    Radio eventProcessingModeStream;

    @UiField
    Radio eventProcessingModeCloud;

    @UiField( provided = true )
    KSessionsPanel statefulSessionsPanel;

    @Inject
    public KBaseFormViewImpl( KSessionsPanel statefulSessionsPanel,
                              CRUDListBox includesListBox,
                              CRUDListBox packagesListBox ) {
        this.statefulSessionsPanel = statefulSessionsPanel;
        this.includesListBox = includesListBox;
        this.packagesListBox = packagesListBox;

        packagesListBox.addRemoveItemHandler( new RemoveItemHandler() {
            @Override
            public void onRemoveItem( RemoveItemEvent event ) {
                presenter.onDeletePackage( event.getItemName() );
            }
        } );

        packagesListBox.addAddItemHandler( new AddItemHandler() {
            @Override
            public void onAddItem( AddItemEvent event ) {
                presenter.onAddPackage( event.getItemName() );
            }
        } );

        includesListBox.addRemoveItemHandler( new RemoveItemHandler() {
            @Override
            public void onRemoveItem( RemoveItemEvent event ) {
                presenter.onDeleteIncludedKBase( event.getItemName() );
            }
        } );

        includesListBox.addAddItemHandler( new AddItemHandler() {
            @Override
            public void onAddItem( AddItemEvent event ) {
                presenter.onAddIncludedKBase( event.getItemName() );
            }
        } );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setName( String name ) {
        nameLabel.setText( name );
    }

    @Override
    public void setDefault( boolean aDefault ) {
        if ( aDefault ) {
            nameLabel.setSubText( ProjectEditorResources.CONSTANTS.BracketDefaultBracket() );
        } else {
            nameLabel.setSubText( "" );
        }
    }

    @Override
    public void setEqualsBehaviorEquality() {
        equalsBehaviorEquality.setValue( true );
    }

    @Override
    public void setEqualsBehaviorIdentity() {
        equalsBehaviorIdentity.setValue( true );
    }

    @Override
    public void setEventProcessingModeStream() {
        eventProcessingModeStream.setValue( true );
    }

    @Override
    public void setEventProcessingModeCloud() {
        eventProcessingModeCloud.setValue( true );
    }

    @Override
    public void setStatefulSessions( List<KSessionModel> items ) {
        statefulSessionsPanel.setItems( items );
    }

    @Override
    public void setReadOnly() {
        equalsBehaviorIdentity.setEnabled( false );
        equalsBehaviorEquality.setEnabled( false );
        eventProcessingModeStream.setEnabled( false );
        eventProcessingModeCloud.setEnabled( false );
        statefulSessionsPanel.makeReadOnly();
        includesListBox.makeReadOnly();
        packagesListBox.makeReadOnly();
    }

    @Override
    public void makeEditable() {
        equalsBehaviorIdentity.setEnabled( true );
        equalsBehaviorEquality.setEnabled( true );
        eventProcessingModeStream.setEnabled( true );
        eventProcessingModeCloud.setEnabled( true );
        statefulSessionsPanel.makeEditable();
        includesListBox.makeEditable();
        packagesListBox.makeEditable();
    }

    @Override
    public void addPackageName( String name ) {
        packagesListBox.addItem( name );
    }

    @Override
    public void addIncludedKBase( String name ) {
        includesListBox.addItem( name );
    }

    @Override
    public void clear() {
        nameLabel.setText( "" );
        includesListBox.clear();
        packagesListBox.clear();
        equalsBehaviorIdentity.setValue( true );
        eventProcessingModeStream.setValue( true );
        statefulSessionsPanel.setItems( new ArrayList<KSessionModel>() );
    }

    @UiHandler( "equalsBehaviorIdentity" )
    public void onEqualsBehaviorIdentityChange( ValueChangeEvent<Boolean> valueChangeEvent ) {
        if ( equalsBehaviorIdentity.getValue() ) {
            presenter.onEqualsBehaviorIdentitySelect();
        }
    }

    @UiHandler( "equalsBehaviorEquality" )
    public void onEqualsBehaviorEqualityChange( ValueChangeEvent<Boolean> valueChangeEvent ) {
        if ( equalsBehaviorEquality.getValue() ) {
            presenter.onEqualsBehaviorEqualitySelect();
        }
    }

    @UiHandler( "eventProcessingModeStream" )
    public void onEventProcessingModeStreamChange( ValueChangeEvent<Boolean> valueChangeEvent ) {
        if ( eventProcessingModeStream.getValue() ) {
            presenter.onEventProcessingModeStreamSelect();
        }
    }

    @UiHandler( "eventProcessingModeCloud" )
    public void onEventProcessingModeCloudChange( ValueChangeEvent<Boolean> valueChangeEvent ) {
        if ( eventProcessingModeCloud.getValue() ) {
            presenter.onEventProcessingModeCloudSelect();
        }
    }
}
