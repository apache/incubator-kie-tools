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

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.*;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;


public class ModelPropertiesEditor extends Composite {

    interface ModelPropertiesEditorUIBinder
        extends UiBinder<Widget, ModelPropertiesEditor> {

    }

    private static ModelPropertiesEditorUIBinder uiBinder = GWT.create(ModelPropertiesEditorUIBinder.class);

    @UiField (provided = true)
    TabPanel tabPanel = new TabPanel(Bootstrap.Tabs.ABOVE);

    private Tab objectTab = new Tab();

    private Tab fieldTab = new Tab();

    private boolean fieldTabAdded;
    
    private static int ENTITY_TAB = 0;

    private static int FIELD_TAB = 1;

    @Inject
    private DataObjectEditor objectProperties;

    @Inject
    private DataObjectFieldEditor fieldProperties;

    private DataModelerContext context;

    public ModelPropertiesEditor() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    private void init() {

        objectTab.setHeading("Data object");
        objectTab.add(objectProperties);

        fieldTab.setHeading("Field");
        fieldTab.add(fieldProperties);

        tabPanel.add(objectTab);
        //tabPanel.add(fieldTab);

        fieldTabAdded = false;
        tabPanel.selectTab(ENTITY_TAB);
    }

    public DataModelerContext getContext() {
        return context;
    }
    
    private DataModelTO getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        objectProperties.setContext(context);
        fieldProperties.setContext(context);
    }

    //event observers

    private void onDataObjectSelected(@Observes DataObjectSelectedEvent event) {
        if (event.isFrom(getDataModel())) {
            checkFieldTabStatus(event);
            tabPanel.selectTab(ENTITY_TAB);
        }
    }

    private void onDataObjectDeleted(@Observes DataObjectDeletedEvent event) {
        if (event.isFrom(getDataModel())) {
            checkFieldTabStatus(event);
            tabPanel.selectTab(ENTITY_TAB);
        }
    }

    private void onDataObjectFieldChange(@Observes DataObjectFieldChangeEvent event) {
        checkFieldTabStatus(event);
    }

    private void onDataObjectFieldCreated(@Observes DataObjectFieldCreatedEvent event) {
        checkFieldTabStatus(event);
    }

    private void onDataObjectFieldDeleted(@Observes DataObjectFieldDeletedEvent event) {
        checkFieldTabStatus(event);
    }

    private void onDataObjectFieldSelected(@Observes DataObjectFieldSelectedEvent event) {
        if (event.isFrom(getDataModel())) {
            checkFieldTabStatus(event);
        }
    }

    private void checkFieldTabStatus(DataModelerEvent event) {
        if (event.isFrom(getDataModel())) {

            if (getDataModel() != null && 
                getDataModel().getDataObjects().size() > 0 &&
                event.getCurrentDataObject() != null &&
                event.getCurrentDataObject().getProperties() != null &&
                event.getCurrentDataObject().getProperties().size() > 0) {
                enableFieldTab(true);
            } else {
                enableFieldTab(false);
            }
        }
    }

    private void enableFieldTab(boolean enable) {
        if (enable) {
            if (!fieldTabAdded) {
                tabPanel.add(fieldTab);
                fieldTabAdded = true;
            }
            tabPanel.selectTab(FIELD_TAB);
        } else {
            if (fieldTabAdded) {
                if (fieldTab.isActive()) {
                    fieldTab.setActive(false);
                    tabPanel.selectTab(ENTITY_TAB);
                }
                tabPanel.remove(fieldTab);
                fieldTabAdded = false;
            }
        }
    }

}