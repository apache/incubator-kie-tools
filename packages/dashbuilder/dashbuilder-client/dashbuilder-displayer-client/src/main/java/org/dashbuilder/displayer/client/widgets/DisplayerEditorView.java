/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.common.client.widgets.AlertPanel;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.uberfire.mvp.Command;

@Dependent
public class DisplayerEditorView extends Composite
        implements DisplayerEditor.View {

    interface Binder extends UiBinder<Widget, DisplayerEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Column westColumn;

    @UiField
    Column leftColumn;

    @UiField
    Column centerColumn;

    @UiField
    TabListItem optionType;

    @UiField
    TabListItem optionData;

    @UiField
    TabListItem optionSettings;
    
    @UiField
    TabListItem optionComponentSettings;

    @UiField
    Row viewAsTableButtonRow;

    @UiField
    CheckBox viewAsTableButton;

    protected DisplayerEditor presenter;
    
    @Inject
    protected DisplayerErrorWidget errorWidget;

    public void init(DisplayerEditor presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        viewAsTableButtonRow.getElement().setAttribute("cellpadding", "5");
        // disabled by default
        setComponentSettingsEnabled(false);
    }

    @Override
    public String getBrandNewDisplayerTitle() {
        return "- " + CommonConstants.INSTANCE.displayer_editor_new() + " -";
    }

    @Override
    public void showDisplayer(IsWidget displayer) {
        centerColumn.clear();
        centerColumn.add(displayer);
    }

    @Override
    public void setTypeSelectionEnabled(boolean enabled) {
        optionType.setVisible(enabled);
        goToOtherSectionIfActive(optionType);
    }

    @Override
    public void setDisplaySettingsEnabled(boolean enabled) {
        optionSettings.setVisible(enabled);
        goToOtherSectionIfActive(optionSettings);
    }

    @Override
    public void setDataSetLookupConfEnabled(boolean enabled) {
        optionData.setVisible(enabled);
        goToOtherSectionIfActive(optionData);
    }
    
    @Override
    public void setComponentSettingsEnabled(boolean enabled) {
        optionComponentSettings.setVisible(enabled);
        goToOtherSectionIfActive(optionComponentSettings);
    }

    @Override
    public boolean isTableDisplayModeOn() {
        return viewAsTableButtonRow.isVisible() && viewAsTableButton.getValue();
    }

    @Override
    public void setTableDisplayModeEnabled(boolean enabled) {
        viewAsTableButtonRow.setVisible(enabled);
    }

    @Override
    public void goToTypeSelection(DisplayerTypeSelector typeSelector) {
        leftColumn.clear();
        leftColumn.getElement().getStyle().setOverflowY(Style.Overflow.HIDDEN);
        leftColumn.add(typeSelector);

        viewAsTableButtonRow.setVisible(false);
        optionData.setActive(false);
        optionSettings.setActive(false);
        optionType.setActive(true);
        optionComponentSettings.setActive(false);
    }

    @Override
    public void goToDataSetLookupConf(DataSetLookupEditor lookupEditor) {
        leftColumn.clear();
        leftColumn.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
        leftColumn.add(lookupEditor);
        
        optionSettings.setActive(false);
        optionType.setActive(false);
        optionData.setActive(true);
        optionComponentSettings.setActive(false);
    }

    @Override
    public void goToDisplaySettings(DisplayerSettingsEditor settingsEditor) {
        leftColumn.clear();
        leftColumn.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
        leftColumn.add(settingsEditor);

        viewAsTableButtonRow.setVisible(false);
        optionType.setActive(false);
        optionData.setActive(false);
        optionSettings.setActive(true);
        optionComponentSettings.setActive(false);
    }
    
    @Override
    public void gotoExternalComponentSettings(ExternalComponentPropertiesEditor externalComponentPropertiesEditor) {
        leftColumn.clear();
        leftColumn.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
        leftColumn.add(externalComponentPropertiesEditor);

        viewAsTableButtonRow.setVisible(false);
        optionType.setActive(false);
        optionData.setActive(false);
        optionSettings.setActive(false);
        optionComponentSettings.setActive(true);
    }

    @Override
    public void showTypeChangedWarning(Command yes, Command no) {
        AlertPanel alertPanel = new AlertPanel();
        String alertMsg = CommonConstants.INSTANCE.displayer_editor_incompatible_settings();
        alertPanel.show(AlertType.WARNING, alertMsg, 400, yes, no);
        centerColumn.clear();
        centerColumn.add(alertPanel);
    }

    @Override
    public void error(String error) {
        centerColumn.clear();
        centerColumn.add(errorWidget);
        errorWidget.show(error, null);

        GWT.log(error);
    }

    @Override
    public void error(ClientRuntimeError e) {
        centerColumn.clear();
        centerColumn.add(errorWidget);
        errorWidget.show(e.getMessage(), e.getThrowable());

        if (e.getThrowable() != null) {
            GWT.log(e.getMessage(), e.getThrowable());
        } else {
            GWT.log(e.getMessage());
        }
    }

    @UiHandler(value = "optionType")
    public void onTypeSelected(ClickEvent clickEvent) {
        presenter.gotoTypeSelection();
    }

    @UiHandler(value = "optionData")
    public void onDataSelected(ClickEvent clickEvent) {
        presenter.gotoDataSetLookupConf();
    }

    @UiHandler(value = "optionSettings")
    public void onSettingsSelected(ClickEvent clickEvent) {
        presenter.gotoDisplaySettings();
    }
    
    @UiHandler(value = "optionComponentSettings")
    public void onExternalComponentSettingsSelected(ClickEvent clickEvent) {
        presenter.gotoExternalComponentSettings();
    }

    @UiHandler(value = "viewAsTableButton")
    public void onRawTableChecked(ClickEvent clickEvent) {
        presenter.showDisplayer();
    }
 
    private void goToOtherSectionIfActive(TabListItem item) {
        if (item.isActive()) {
            presenter.gotoFirstSectionEnabled();
        }
    }
    
}