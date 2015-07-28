/**
 * Copyright 2012 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Legend;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataObjectBrowser;
import org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain.MainDomainEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelStatusChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;


public class DataModelerScreenViewImpl
        extends KieEditorViewImpl
        implements DataModelerScreenPresenter.DataModelerScreenView, RequiresResize {

    interface DataModelerScreenViewBinder
            extends
            UiBinder<Widget, DataModelerScreenViewImpl> {

    }

    private static DataModelerScreenViewBinder uiBinder = GWT.create( DataModelerScreenViewBinder.class );

    @UiField
    FlowPanel dataObjectPanel;

    @UiField
    FlowPanel domainContainerPanel;

    @UiField
    Legend domainContainerTitle;

    @Inject
    private DataObjectBrowser dataObjectBrowser;

    @Inject
    private MainDomainEditor mainDomainEditor;

    @Inject
    private Event<DataModelerEvent> dataModelerEvent;

    private DataModelerContext context;

    @Inject
    private DataModelerWorkbenchContext dataModelerWBContext;

    public DataModelerScreenViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void initUI() {
        dataObjectPanel.add( dataObjectBrowser );
        domainContainerPanel.add( mainDomainEditor );
    }

    @Override
    public void setContext( DataModelerContext context ) {
        this.context = context;
        dataObjectBrowser.setContext( context );
    }

    @Override
    public void refreshTypeLists( boolean keepSelection ) {
        mainDomainEditor.refreshTypeList( keepSelection );
    }

    private void updateChangeStatus( DataModelerEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            context.setEditionStatus( DataModelerContext.EditionStatus.EDITOR_CHANGED );
            dataModelerEvent.fire( new DataModelStatusChangeEvent( context.getContextId(), null, false, true ) );
        }
    }

    private void refreshTitle( DataObject dataObject ) {
        if ( dataObject != null ) {
            String label = DataModelerUtils.getDataObjectFullLabel( dataObject, false );
            String title = "'" + label + "'" + " - general properties";
            String tooltip = dataObject.getClassName();
            domainContainerTitle.setText( title );
            domainContainerTitle.setTitle( tooltip );
        }
    }

    private void refreshTitle( DataObject dataObject, ObjectProperty objectProperty ) {
        if ( dataObject != null && objectProperty != null ) {
            String title = "'" + objectProperty.getName() + "'" + " - general properties";
            String tooltip = dataObject.getClassName() + "." + objectProperty.getName();
            domainContainerTitle.setText( title );
            domainContainerTitle.setTitle( tooltip );
        }
    }

    // event observers

    private void onContextChange( @Observes DataModelerWorkbenchContextChangeEvent contextEvent ) {

        DataModelerContext activeContext = dataModelerWBContext.getActiveContext();
        if ( context != null && context.getContextId().equals( activeContext != null ? activeContext.getContextId() : null ) ) {

            if ( activeContext.getDataObject() != null && activeContext.getObjectProperty() != null ) {
                refreshTitle( activeContext.getDataObject(), activeContext.getObjectProperty() );
            } else if ( activeContext.getDataObject() != null ) {
                refreshTitle( activeContext.getDataObject() );
            }
        }
    }


    private void onDataObjectChange( @Observes DataObjectChangeEvent event ) {
        updateChangeStatus( event );
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            refreshTitle( event.getCurrentDataObject() );
        }
    }

    private void onDataObjectFieldCreated( @Observes DataObjectFieldCreatedEvent event ) {
        updateChangeStatus( event );
    }

    private void onDataObjectFieldChange( @Observes DataObjectFieldChangeEvent event ) {
        updateChangeStatus( event );
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            refreshTitle( event.getCurrentDataObject(), event.getCurrentField() );
        }
    }

    private void onDataObjectFieldDeleted( @Observes DataObjectFieldDeletedEvent event ) {
        updateChangeStatus( event );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }
}