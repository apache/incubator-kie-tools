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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.ValidationError;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.RendererManager;
import org.dashbuilder.displayer.client.events.DataSetLookupChangedEvent;
import org.dashbuilder.displayer.client.events.DisplayerEditorClosedEvent;
import org.dashbuilder.displayer.client.events.DisplayerEditorSavedEvent;
import org.dashbuilder.displayer.client.events.DisplayerSettingsChangedEvent;
import org.dashbuilder.displayer.client.events.DisplayerSubtypeSelectedEvent;
import org.dashbuilder.displayer.client.events.DisplayerTypeSelectedEvent;
import org.dashbuilder.displayer.client.prototypes.DisplayerPrototypes;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class DisplayerEditor implements IsWidget {

    private static final int DEFAULT_SECTION = 0;
    private static final int DATASET_LOOKUP_SECTION = 1;
    private static final int DISPLAY_SETTINGS_SECTION = 2;
    private static final int EXTERNAL_COMPONENT_SECTION = 3;

    public interface View extends UberView<DisplayerEditor> {

        String getBrandNewDisplayerTitle();

        boolean isTableDisplayModeOn();

        void setTableDisplayModeEnabled(boolean enabled);

        void showDisplayer(IsWidget displayer);

        void setTypeSelectionEnabled(boolean enabled);

        void setDisplaySettingsEnabled(boolean enabled);

        void setDataSetLookupConfEnabled(boolean enabled);

        void setComponentSettingsEnabled(boolean enabled);

        void goToTypeSelection(DisplayerTypeSelector typeSelector);

        void goToDataSetLookupConf(DataSetLookupEditor lookupEditor);

        void goToDisplaySettings(DisplayerSettingsEditor settingsEditor);

        void showTypeChangedWarning(Command yes, Command no);

        void error(String error);

        void error(ClientRuntimeError error);

        void gotoExternalComponentSettings(ExternalComponentPropertiesEditor externalComponentPropertiesEditor);

    }

    protected View view = null;
    protected DataSetClientServices clientServices = null;
    protected DisplayerLocator displayerLocator = null;
    protected DisplayerPrototypes displayerPrototypes = null;
    protected DisplayerSettings displayerSettings = null;
    protected DisplayerSettings selectedTypeSettings = null;
    protected boolean brandNewDisplayer = true;
    protected DisplayerTypeSelector typeSelector;
    protected DataSetLookupEditor lookupEditor;
    protected DisplayerSettingsEditor settingsEditor;
    protected DisplayerEditorStatus editorStatus;
    protected Displayer displayer = null;
    protected DisplayerHtmlEditor displayerHtmlEditor = null;
    protected int activeSection = -1;
    protected boolean typeSelectionEnabled = true;
    protected boolean dataLookupConfEnabled = true;
    protected boolean displaySettingsEnabled = true;
    protected boolean externalComponentSettingsEnabled = false;
    protected Event<DisplayerEditorSavedEvent> saveEvent;
    protected Event<DisplayerEditorClosedEvent> closeEvent;
    protected Command onCloseCommand = () -> {
    };
    protected Command onSaveCommand = () -> {
    };
    protected DisplayerType displayerType = DisplayerType.BARCHART;
    protected DisplayerSubType displayerSubType = null;
    protected RendererManager rendererManager;
    protected Event<DisplayerSettingsChangedEvent> displayerSettingsChangedEvent;
    private ExternalComponentPropertiesEditor externalComponentPropertiesEditor;
    protected String currentRenderer = "";

    DisplayerListener displayerListener = new AbstractDisplayerListener() {

        public void onError(Displayer displayer, ClientRuntimeError error) {
            view.error(error);
        }
    };

    @Inject
    public DisplayerEditor(View view,
                           DataSetClientServices clientServices,
                           DisplayerLocator displayerLocator,
                           DisplayerPrototypes displayerPrototypes,
                           DisplayerTypeSelector typeSelector,
                           DataSetLookupEditor lookupEditor,
                           DisplayerSettingsEditor settingsEditor,
                           DisplayerEditorStatus editorStatus,
                           DisplayerHtmlEditor displayerHtmlEditor,
                           Event<DisplayerEditorSavedEvent> savedEvent,
                           Event<DisplayerEditorClosedEvent> closedEvent,
                           RendererManager rendererManager,
                           ExternalComponentPropertiesEditor externalComponentPropertiesEditor,
                           Event<DisplayerSettingsChangedEvent> displayerSettingsChangedEvent) {
        this.view = view;
        this.displayerLocator = displayerLocator;
        this.clientServices = clientServices;
        this.displayerPrototypes = displayerPrototypes;
        this.typeSelector = typeSelector;
        this.lookupEditor = lookupEditor;
        this.settingsEditor = settingsEditor;
        this.editorStatus = editorStatus;
        this.displayerHtmlEditor = displayerHtmlEditor;
        this.saveEvent = savedEvent;
        this.closeEvent = closedEvent;
        this.rendererManager = rendererManager;
        this.externalComponentPropertiesEditor = externalComponentPropertiesEditor;
        this.displayerSettingsChangedEvent = displayerSettingsChangedEvent;

        view.init(this);
    }

    public void setDisplayerType(DisplayerType displayerType) {
        this.displayerType = displayerType != null ? displayerType : DisplayerType.BARCHART;
    }

    public void setDisplayerSubType(DisplayerSubType displayerSubType) {
        this.displayerSubType = displayerSubType;
    }

    public void init(DisplayerSettings settings) {
        if (settings != null) {
            brandNewDisplayer = false;
            displayerSettings = settings;
        } else {
            brandNewDisplayer = true;
            displayerSettings = displayerPrototypes.getProto(displayerType, displayerSubType);
            displayerSettings.setTitle(view.getBrandNewDisplayerTitle());
        }
        selectedTypeSettings = displayerSettings;

        initDisplayer();
        initTypeSelector();
        initLookupEditor();
        initSettingsEditor();
        initComponentEditor();
        gotoLastSection();
        showDisplayer();

        currentRenderer = displayerSettings.getRenderer();
        if (currentRenderer == null || currentRenderer.trim().isEmpty()) {
            currentRenderer = rendererManager.getDefaultRenderer(displayerSettings.getType()).getUUID();
        }
    }

    private void initComponentEditor() {
        externalComponentPropertiesEditor.init(displayerSettings.getComponentId(),
                                               displayerSettings.getComponentProperties(),
                                               this::onComponentPropertiesUpdate);
    }

    protected boolean supportsHtmlTemplate() {
        return displayer.getDisplayerConstraints().getSupportedAttributes().contains(DisplayerAttributeDef.HTML_TEMPLATE);
    }

    protected void initDisplayer() {
        if (displayer != null) {
            displayer.close();
        }
        displayer = displayerLocator.lookupDisplayer(displayerSettings);
        displayer.addListener(displayerListener);
        displayer.setRefreshOn(false);
        displayer.draw();
    }

    protected void initLookupEditor() {
        DataSetLookupConstraints lookupConstraints = displayer.getDisplayerConstraints().getDataSetLookupConstraints();
        lookupEditor.init(lookupConstraints, displayerSettings.getDataSetLookup());
    }

    protected void initTypeSelector() {
        typeSelector.init(displayerSettings.getType(), displayerSettings.getSubtype());
    }

    protected void initSettingsEditor() {
        settingsEditor.init(displayer);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public View getView() {
        return view;
    }

    public boolean isBrandNewDisplayer() {
        return brandNewDisplayer;
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public Displayer getDisplayer() {
        return displayer;
    }

    public String getCurrentRenderer() {
        return currentRenderer;
    }

    public DisplayerTypeSelector getTypeSelector() {
        return typeSelector;
    }

    public DataSetLookupEditor getLookupEditor() {
        return lookupEditor;
    }

    public DisplayerSettingsEditor getSettingsEditor() {
        return settingsEditor;
    }

    public void setTypeSelectorEnabled(boolean enabled) {
        typeSelectionEnabled = enabled;
        view.setTypeSelectionEnabled(enabled);
    }

    public void setDataSetLookupConfEnabled(boolean enabled) {
        dataLookupConfEnabled = enabled;
        view.setDataSetLookupConfEnabled(enabled);
    }

    public void setDisplaySettingsEnabled(boolean enabled) {
        displaySettingsEnabled = enabled;
        view.setDisplaySettingsEnabled(enabled);
    }

    public void setExternalComponentSettingsEnabled(boolean enabled) {
        externalComponentSettingsEnabled = enabled;
        view.setComponentSettingsEnabled(enabled);
    }

    public void setOnSaveCommand(Command saveCommand) {
        this.onSaveCommand = saveCommand != null ? saveCommand : onCloseCommand;
    }

    public void setOnCloseCommand(Command closeCommand) {
        this.onCloseCommand = closeCommand != null ? closeCommand : onCloseCommand;
    }

    public void showDisplayer() {
        if (view.isTableDisplayModeOn()) {
            try {
                DisplayerSettings tableSettings = displayerSettings.cloneInstance();
                tableSettings.setTitleVisible(false);
                tableSettings.setType(DisplayerType.TABLE);
                tableSettings.setTablePageSize(10);
                tableSettings.setTableWidth(800);
                tableSettings.setRenderer("default");
                Displayer tableDisplayer = displayerLocator.lookupDisplayer(tableSettings);
                tableDisplayer.addListener(displayerListener);
                tableDisplayer.setRefreshOn(false);
                tableDisplayer.draw();
                view.showDisplayer(tableDisplayer);
            } catch (Exception e) {
                view.error(new ClientRuntimeError(e));
            }
        } else if (supportsHtmlTemplate()) {
            displayerHtmlEditor.setDisplayer(displayer);
            view.showDisplayer(displayerHtmlEditor);
        } else {
            view.showDisplayer(displayer);
        }
    }

    public void gotoFirstSectionEnabled() {
        if (typeSelectionEnabled) {
            gotoTypeSelection();
        } else if (dataLookupConfEnabled) {
            gotoDataSetLookupConf();
        } else if (displaySettingsEnabled) {
            gotoDisplaySettings();
        } else if (externalComponentSettingsEnabled) {
            gotoExternalComponentSettings();
        } else {
            view.error("Nothing to show!");
        }
    }

    public void gotoLastSection() {
        int lastOption = editorStatus.getSelectedOption(displayerSettings.getUUID());
        if (activeSection < 0 || activeSection != lastOption) {
            switch (lastOption) {
                case EXTERNAL_COMPONENT_SECTION:
                    gotoExternalComponentSettings();
                    break;
                case DISPLAY_SETTINGS_SECTION:
                    gotoDisplaySettings();
                    break;
                case DATASET_LOOKUP_SECTION:
                    gotoDataSetLookupConf();
                    break;
                default:
                    gotoFirstSectionEnabled();
                    break;
            }
        }
    }

    public void gotoTypeSelection() {
        activeSection = DEFAULT_SECTION;
        editorStatus.saveSelectedOption(displayerSettings.getUUID(), activeSection);
        view.goToTypeSelection(typeSelector);
    }

    public void gotoDataSetLookupConf() {
        activeSection = DATASET_LOOKUP_SECTION;
        editorStatus.saveSelectedOption(displayerSettings.getUUID(), activeSection);
        view.goToDataSetLookupConf(lookupEditor);
        view.setTableDisplayModeEnabled(!DisplayerType.TABLE.equals(displayerSettings.getType()));
    }

    public void gotoDisplaySettings() {
        activeSection = DISPLAY_SETTINGS_SECTION;
        editorStatus.saveSelectedOption(displayerSettings.getUUID(), activeSection);
        view.goToDisplaySettings(settingsEditor);
    }

    public void gotoExternalComponentSettings() {
        activeSection = EXTERNAL_COMPONENT_SECTION;
        editorStatus.saveSelectedOption(displayerSettings.getUUID(), activeSection);
        initComponentEditor();
        view.gotoExternalComponentSettings(externalComponentPropertiesEditor);
    }

    public void save() {
        // Clear settings before return
        DisplayerConstraints displayerConstraints = displayer.getDisplayerConstraints();
        displayerConstraints.removeUnsupportedAttributes(displayerSettings);

        // Dispose the displayer
        if (displayer != null) {
            displayer.close();
        }
        // Notify event
        onSaveCommand.execute();
        saveEvent.fire(new DisplayerEditorSavedEvent(displayerSettings));
    }

    public void close() {
        if (displayer != null) {
            displayer.close();
        }
        onCloseCommand.execute();
        closeEvent.fire(new DisplayerEditorClosedEvent(displayerSettings));
    }

    // Widget listeners callback notifications

    void onDataSetLookupChanged(@Observes DataSetLookupChangedEvent event) {
        DataSetLookup dataSetLookup = event.getDataSetLookup();
        displayerSettings.setDataSet(null);
        displayerSettings.setDataSetLookup(dataSetLookup);
        removeStaleSettings();
        initDisplayer();
        initSettingsEditor();
        showDisplayer();
    }

    void onDisplayerSettingsChanged(@Observes DisplayerSettingsChangedEvent event) {
        String newRenderer = event.getDisplayerSettings().getRenderer();
        displayerSettings = event.getDisplayerSettings();
        initDisplayer();
        showDisplayer();
        if (newRenderer != null && !currentRenderer.equals(newRenderer)) {
            initSettingsEditor();
            currentRenderer = newRenderer;
        }
    }

    void onDisplayerTypeChanged(@Observes DisplayerTypeSelectedEvent event) {
        displayerTypeChanged(event.getSelectedType(), null);
    }

    void onDisplayerSubtypeChanged(@Observes DisplayerSubtypeSelectedEvent event) {
        displayerTypeChanged(selectedTypeSettings.getType(), event.getSelectedSubType());
    }

    void displayerTypeChanged(DisplayerType type, DisplayerSubType displayerSubType) {

        // Create new settings for the selected type
        selectedTypeSettings = displayerPrototypes.getProto(type, displayerSubType);
        DataSet oldDataSet = displayerSettings.getDataSet();
        DataSetLookup oldDataLookup = displayerSettings.getDataSetLookup();

        // Check if the current data lookup is compatible with the new displayer type
        if (oldDataSet == null && oldDataLookup != null) {
            Displayer displayer = displayerLocator.lookupDisplayer(selectedTypeSettings);
            DisplayerConstraints displayerConstraints = displayer.getDisplayerConstraints();
            DataSetLookupConstraints dataConstraints = displayerConstraints.getDataSetLookupConstraints();
            DataSetMetadata metadata = clientServices.getMetadata(oldDataLookup.getDataSetUUID());

            // Keep the current data settings provided it satisfies the data constraints
            ValidationError validationError = dataConstraints.check(oldDataLookup, metadata);
            if (validationError == null) {
                selectedTypeSettings.setDataSet(null);
                selectedTypeSettings.setDataSetLookup(oldDataLookup);
                applySelectedType();
            }
            // If the data lookup is not compatible then ask the user what to do
            else {
                view.showTypeChangedWarning(this::applySelectedType, this::abortSelectedType);
            }
        }
        // If the displayer is static (no data lookup) then just display the selected displayer prototype
        else {
            applySelectedType();
        }
    }

    void applySelectedType() {
        // Remove the non supported attributes
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.TYPE);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.SUBTYPE);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.GENERAL_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.CHART_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.AXIS_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.SELECTOR_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.FILTER_GROUP);
        displayerSettings.removeDisplayerSetting(DisplayerAttributeGroupDef.HTML_GROUP);
        selectedTypeSettings.getSettingsFlatMap().putAll(displayerSettings.getSettingsFlatMap());

        try {
            // Ensure the renderer supports the new type
            displayerLocator.lookupDisplayer(selectedTypeSettings);
        } catch (Exception e) {
            // The new type might not support the selected renderer.
            selectedTypeSettings.removeDisplayerSetting(DisplayerAttributeDef.RENDERER);
            view.error(new ClientRuntimeError(e));
        }

        // Re-initialize the editor with the new settings
        init(selectedTypeSettings);
        removeStaleSettings();
    }

    void abortSelectedType() {
        selectedTypeSettings = displayerSettings;
        typeSelector.init(displayerSettings.getType(), displayerSettings.getSubtype());
        view.showDisplayer(displayer);
    }

    List<String> getExistingDataColumnIds() {
        DataSet dataSet = displayerSettings.getDataSet();
        DataSetLookup dataSetLookup = displayerSettings.getDataSetLookup();

        List<String> columnIds = new ArrayList<String>();
        if (dataSet != null) {
            for (DataColumn dataColumn : dataSet.getColumns()) {
                columnIds.add(dataColumn.getId());
            }
        } else if (dataSetLookup != null) {
            int idx = dataSetLookup.getLastGroupOpIndex(0);
            if (idx != -1) {
                DataSetGroup groupOp = dataSetLookup.getOperation(idx);
                for (GroupFunction groupFunction : groupOp.getGroupFunctions()) {
                    columnIds.add(groupFunction.getColumnId());
                }
            }
        }
        return columnIds;
    }

    void removeStaleSettings() {
        List<String> columnIds = getExistingDataColumnIds();

        // Remove the settings for non existing columns
        Iterator<ColumnSettings> it = displayerSettings.getColumnSettingsList().iterator();
        while (it.hasNext()) {
            ColumnSettings columnSettings = it.next();
            if (!columnIds.contains(columnSettings.getColumnId())) {
                it.remove();
            }
        }
        // Reset table sort column
        if (!columnIds.contains(displayerSettings.getTableDefaultSortColumnId())) {
            displayerSettings.setTableDefaultSortColumnId(null);
        }
    }

    void onComponentPropertiesUpdate(Map<String, String> updatedProperties) {
        updatedProperties.forEach(displayerSettings::setComponentProperty);
        displayerSettingsChangedEvent.fire(new DisplayerSettingsChangedEvent(displayerSettings));
    }
}
