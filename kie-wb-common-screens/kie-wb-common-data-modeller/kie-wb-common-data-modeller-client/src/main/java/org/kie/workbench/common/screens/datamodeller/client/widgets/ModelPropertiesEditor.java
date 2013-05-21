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
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;


public class ModelPropertiesEditor extends Composite {

    interface ModelPropertiesEditorUIBinder
        extends UiBinder<Widget, ModelPropertiesEditor> {

    }

    private static ModelPropertiesEditorUIBinder uiBinder = GWT.create(ModelPropertiesEditorUIBinder.class);

    @UiField (provided = true)
    public TabPanel tabPanel = new TabPanel(Bootstrap.Tabs.ABOVE);

    private Tab objectTab = new Tab();

    private Tab fieldTab = new Tab();

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

        objectTab.setHeading("Entity");
        objectTab.add(objectProperties);

        fieldTab.setHeading("Field");
        fieldTab.add(fieldProperties);

        tabPanel.add(objectTab);
        tabPanel.add(fieldTab);


        tabPanel.selectTab(0);
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        objectProperties.setContext(context);
        fieldProperties.setContext(context);
    }
}