/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataObjectBrowser;
import org.kie.workbench.common.screens.datamodeller.client.widgets.ModelPropertiesEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelStatusChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

//@Dependent
public class DataModelerScreenViewImpl extends Composite
        implements DataModelerScreenPresenter.DataModelerScreenView {

    interface DataModelerScreenViewBinder
            extends
            UiBinder<Widget, DataModelerScreenViewImpl> {

    }

    private static DataModelerScreenViewBinder uiBinder = GWT.create(DataModelerScreenViewBinder.class);

    /*
    @UiField
    SimplePanel browserPanel = new SimplePanel();
    */

    @UiField
    SimplePanel dataObjectPanel = new SimplePanel();

    @UiField
    SimplePanel propertiesPanel = new SimplePanel();

    @Inject
    private ModelPropertiesEditor modelPropertiesEditor;

    /*
    @Inject
    private DataModelBrowser dataModelBrowser;
    */

    @Inject
    private DataObjectBrowser dataObjectBrowser;

    @Inject
    private Event<DataModelerEvent> dataModelerEvent;

    private DataModelerContext context;

    public DataModelerScreenViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void initUI() {
        //browserPanel.add(dataModelBrowser);
        dataObjectPanel.add(dataObjectBrowser);
        propertiesPanel.add(modelPropertiesEditor);
    }

    @Override
    public void setContext(DataModelerContext context) {
        this.context = context;
        //dataModelBrowser.setContext(context);
        dataObjectBrowser.setContext(context);
        modelPropertiesEditor.setContext(context);
    }

    @Override
    public void init(final DataModelerScreenPresenter presenter) {
        // Not used.
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( Constants.INSTANCE.modelEditor_discard_changes_message());
    }

    @Override
    public void refreshTypeLists( boolean keepSelection ) {
        dataObjectBrowser.refreshTypeList( keepSelection );
        modelPropertiesEditor.refreshTypeList( keepSelection );
    }

    private void updateChangeStatus(DataModelerEvent event) {
        if (context != null && event.isFrom(context.getDataModel())) {
            Boolean oldDirtyStatus = context.isDirty();
            context.setDirty(true);
            context.setEditionStatus( DataModelerContext.EditionStatus.EDITOR_CHANGED );
            dataModelerEvent.fire(new DataModelStatusChangeEvent(null, context.getDataModel(), oldDirtyStatus, context.isDirty()));
        }
    }

    // event observers

    private void onDataObjectChange(@Observes DataObjectChangeEvent event) {
        updateChangeStatus( event );
    }

    private void onDataObjectFieldCreated(@Observes DataObjectFieldCreatedEvent event) {
        updateChangeStatus(event);
    }

    private void onDataObjectFieldChange(@Observes DataObjectFieldChangeEvent event) {
        updateChangeStatus(event);
    }

    private void onDataObjectFieldDeleted(@Observes DataObjectFieldDeletedEvent event) {
        updateChangeStatus(event);
    }

    private DataModelerContext getContext() {
        return context;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void setNotDirty() {
        context.setDirty(false);
    }
}