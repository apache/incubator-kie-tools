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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.MapColorScheme;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.resources.i18n.MapColorSchemeConstants;
import org.dashbuilder.displayer.client.resources.i18n.PositionConstants;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

import static org.uberfire.ext.properties.editor.model.PropertyEditorType.*;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.*;

@Dependent
public class DisplayerSettingsEditorView extends Composite implements DisplayerSettingsEditor.View {

    interface Binder extends UiBinder<Widget, DisplayerSettingsEditorView> {}
    private static final Binder uiBinder = GWT.create( Binder.class );

    @UiField
    Panel mainPanel;

    @UiField
    PropertyEditorWidget propertyEditor;

    DisplayerSettingsEditor presenter;
    List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();
    Map<DisplayerAttributeDef,String> attrMapI18n = new HashMap<DisplayerAttributeDef, String>();
    PropertyEditorCategory category = null;

    public static final String PROPERTY_EDITOR_ID = "displayerSettingsEditor";

    @Override
    public void init(DisplayerSettingsEditor presenter) {
        this.presenter = presenter;

        attrMapI18n.put(GENERAL_GROUP, CommonConstants.INSTANCE.common_group());
        attrMapI18n.put(TITLE, CommonConstants.INSTANCE.common_title());
        attrMapI18n.put(TITLE_VISIBLE, CommonConstants.INSTANCE.common_showTitle());
        attrMapI18n.put(ALLOW_EXPORT_CSV, CommonConstants.INSTANCE.common_allowCSV());
        attrMapI18n.put(ALLOW_EXPORT_EXCEL, CommonConstants.INSTANCE.common_allowExcel());
        attrMapI18n.put(EXPORT_GROUP, CommonConstants.INSTANCE.common_export());
        attrMapI18n.put(EXPORT_TO_CSV, CommonConstants.INSTANCE.common_allowCSV());
        attrMapI18n.put(EXPORT_TO_XLS, CommonConstants.INSTANCE.common_allowExcel());
        attrMapI18n.put(RENDERER, CommonConstants.INSTANCE.common_renderer());
        attrMapI18n.put(CHART_GROUP, CommonConstants.INSTANCE.chart_group());
        attrMapI18n.put(CHART_WIDTH, CommonConstants.INSTANCE.chart_width());
        attrMapI18n.put(CHART_HEIGHT, CommonConstants.INSTANCE.chart_height());
        attrMapI18n.put(CHART_RESIZABLE, CommonConstants.INSTANCE.chart_resizable());
        attrMapI18n.put(CHART_BGCOLOR, CommonConstants.INSTANCE.chart_bgColor());
        attrMapI18n.put(CHART_3D, CommonConstants.INSTANCE.chart_3d());
        attrMapI18n.put(CHART_MARGIN_GROUP, CommonConstants.INSTANCE.chart_marginGroup());
        attrMapI18n.put(CHART_MARGIN_TOP, CommonConstants.INSTANCE.chart_topMargin());
        attrMapI18n.put(CHART_MARGIN_BOTTOM, CommonConstants.INSTANCE.chart_bottomMargin());
        attrMapI18n.put(CHART_MARGIN_LEFT, CommonConstants.INSTANCE.chart_leftMargin());
        attrMapI18n.put(CHART_MARGIN_RIGHT, CommonConstants.INSTANCE.chart_rightMargin());
        attrMapI18n.put(CHART_LEGEND_GROUP, CommonConstants.INSTANCE.chart_legendGroup());
        attrMapI18n.put(CHART_SHOWLEGEND, CommonConstants.INSTANCE.chart_legendShow());
        attrMapI18n.put(CHART_LEGENDPOSITION, CommonConstants.INSTANCE.chart_legendPosition());
        attrMapI18n.put(XAXIS_GROUP, CommonConstants.INSTANCE.axis_group());
        attrMapI18n.put(YAXIS_GROUP, CommonConstants.INSTANCE.axis_group());
        attrMapI18n.put(XAXIS_SHOWLABELS, CommonConstants.INSTANCE.xaxis_showLabels());
        attrMapI18n.put(XAXIS_TITLE, CommonConstants.INSTANCE.xaxis_title());
        attrMapI18n.put(XAXIS_LABELSANGLE, CommonConstants.INSTANCE.xaxis_angle());
        attrMapI18n.put(YAXIS_SHOWLABELS, CommonConstants.INSTANCE.yaxis_showLabels());
        attrMapI18n.put(YAXIS_TITLE, CommonConstants.INSTANCE.yaxis_title());
        attrMapI18n.put(TABLE_GROUP, CommonConstants.INSTANCE.table_group());
        attrMapI18n.put(TABLE_PAGESIZE, CommonConstants.INSTANCE.table_pageSize());
        attrMapI18n.put(TABLE_WIDTH, CommonConstants.INSTANCE.table_width());
        attrMapI18n.put(TABLE_SORTENABLED, CommonConstants.INSTANCE.table_sortEnabled());
        attrMapI18n.put(TABLE_SORTCOLUMNID, CommonConstants.INSTANCE.table_sortColumn());
        attrMapI18n.put(TABLE_SORTORDER, CommonConstants.INSTANCE.table_sortOrder());
        attrMapI18n.put(TABLE_COLUMN_PICKER_ENABLED, CommonConstants.INSTANCE.table_columnPicker());
        attrMapI18n.put(METER_GROUP, CommonConstants.INSTANCE.meter_group());
        attrMapI18n.put(METER_START, CommonConstants.INSTANCE.meter_start());
        attrMapI18n.put(METER_WARNING, CommonConstants.INSTANCE.meter_warning());
        attrMapI18n.put(METER_CRITICAL, CommonConstants.INSTANCE.meter_critical());
        attrMapI18n.put(METER_END, CommonConstants.INSTANCE.meter_end());
        attrMapI18n.put(FILTER_GROUP, CommonConstants.INSTANCE.filter_group());
        attrMapI18n.put(FILTER_ENABLED, CommonConstants.INSTANCE.filter_enabled());
        attrMapI18n.put(FILTER_SELFAPPLY_ENABLED, CommonConstants.INSTANCE.filter_self());
        attrMapI18n.put(FILTER_LISTENING_ENABLED, CommonConstants.INSTANCE.filter_listening());
        attrMapI18n.put(FILTER_NOTIFICATION_ENABLED, CommonConstants.INSTANCE.filter_notifications());
        attrMapI18n.put(SELECTOR_GROUP, CommonConstants.INSTANCE.selector_group());
        attrMapI18n.put(SELECTOR_WIDTH, CommonConstants.INSTANCE.selector_width());
        attrMapI18n.put(SELECTOR_MULTIPLE, CommonConstants.INSTANCE.selector_multiple());
        attrMapI18n.put(SELECTOR_SHOW_INPUTS, CommonConstants.INSTANCE.selector_show_inputs());
        attrMapI18n.put(REFRESH_GROUP, CommonConstants.INSTANCE.refresh_group());
        attrMapI18n.put(REFRESH_INTERVAL, CommonConstants.INSTANCE.refresh_interval());
        attrMapI18n.put(REFRESH_STALE_DATA, CommonConstants.INSTANCE.refresh_stale_data());
        attrMapI18n.put(COLUMNS_GROUP, CommonConstants.INSTANCE.common_columns());
        attrMapI18n.put(MAP_GROUP, CommonConstants.INSTANCE.map_group());
        attrMapI18n.put(MAP_COLOR_SCHEME, CommonConstants.INSTANCE.color_scheme());

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void clear() {
        categories.clear();
    }

    @Override
    public void show() {
        propertyEditor.handle(new PropertyEditorEvent(PROPERTY_EDITOR_ID, categories));
    }

    @Override
    public void dataSetNotFound() {
        mainPanel.add(new Label(LabelType.WARNING, CommonConstants.INSTANCE.error() + CommonConstants.INSTANCE.displayer_editor_dataset_notfound()));
    }

    @Override
    public void error(String message) {
        mainPanel.add(new Label(LabelType.WARNING, CommonConstants.INSTANCE.error() + message));
    }

    @Override
    public void addCategory(DisplayerAttributeDef attributeDef) {
        String catName = attrMapI18n.get(attributeDef);
        category = new PropertyEditorCategory(catName);
        categories.add(category);
    }

    @Override
    public void addTextProperty(DisplayerAttributeDef attributeDef, String value, PropertyFieldValidator... validators) {
        String propName = attrMapI18n.get(attributeDef);
        category.withField(new PropertyEditorFieldInfo(propName, value, TEXT)
                .withValidators(validators)
                .withKey(attributeDef.getFullId()));
    }

    @Override
    public void addTextProperty(String propId, String propName, String value, PropertyFieldValidator... validators) {
        category.withField(new PropertyEditorFieldInfo(propName, value, TEXT)
                .withValidators(validators)
                .withKey(propId));
    }

    @Override
    public void addBooleanProperty(DisplayerAttributeDef attributeDef, boolean value) {
        String propName = attrMapI18n.get(attributeDef);
        category.withField(new PropertyEditorFieldInfo(propName, Boolean.toString(value), BOOLEAN)
                .withKey(attributeDef.getFullId()));
    }

    @Override
    public void addColorProperty(DisplayerAttributeDef attributeDef, String color) {
        String propName = attrMapI18n.get(attributeDef);
        category.withField(new PropertyEditorFieldInfo(propName, color, COLOR)
                .withKey(attributeDef.getFullId()));
    }

    @Override
    public void addListProperty(DisplayerAttributeDef attributeDef, List<String> optionList, String selectedValue) {
        String propName = attrMapI18n.get(attributeDef);
        category.withField(new PropertyEditorFieldInfo(propName, selectedValue, COMBO)
                .withComboValues(optionList)
                .withKey(attributeDef.getFullId()));
    }

    @Override
    public String getColumnNameI18n() {
        return CommonConstants.INSTANCE.columns_name();
    }

    @Override
    public String getColumnExpressionI18n() {
        return CommonConstants.INSTANCE.columns_expression();
    }

    @Override
    public String getColumnPatternI18n() {
        return CommonConstants.INSTANCE.columns_pattern();
    }

    @Override
    public String getPositionLiteralI18n(Position position) {
        return PositionConstants.INSTANCE.getString("POSITION_" + position.toString());
    }

    @Override
    public String getIntegerValidationFailedI18n() {
        return CommonConstants.INSTANCE.settings_validation_integer();
    }

    @Override
    public String getDoubleValidationFailedI18n() {
        return CommonConstants.INSTANCE.settings_validation_double();
    }

    @Override
    public String getMeterStartI18n() {
        return CommonConstants.INSTANCE.meter_start();
    }

    @Override
    public String getMeterWarningI18n() {
        return CommonConstants.INSTANCE.meter_warning();
    }

    @Override
    public String getMeterCriticalI18n() {
        return CommonConstants.INSTANCE.meter_critical();
    }

    @Override
    public String getMeterEndI18n() {
        return CommonConstants.INSTANCE.meter_end();
    }

    @Override
    public String getMeterUnknownI18n() {
        return CommonConstants.INSTANCE.settings_validation_meter_unknown();
    }

    @Override
    public String getMeterValidationHigherI18n(String level) {
        return CommonConstants.INSTANCE.settings_validation_meter_higher(level);
    }

    @Override
    public String getMeterValidationLowerI18n(String level) {
        return CommonConstants.INSTANCE.settings_validation_meter_lower(level);
    }

    @Override
    public String getMeterValidationInvalidI18n() {
        return CommonConstants.INSTANCE.settings_validation_meter_invalid();
    }
    
    @Override
    public String getMapColorSchemeI18n(MapColorScheme colorScheme) {
        return MapColorSchemeConstants.INSTANCE.getString("COLOR_SCHEME_" + colorScheme.toString());
    }

    /**
     * Capture & process the modification events sent by the property editor
     */
    protected void onPropertyEditorChange(@Observes PropertyEditorChangeEvent event) {
        PropertyEditorFieldInfo property = event.getProperty();
        if (property.getEventId().equalsIgnoreCase(PROPERTY_EDITOR_ID)) {
            String attrKey = property.getKey();
            String attrValue = event.getNewValue();
            presenter.onAttributeChanged(attrKey, attrValue);
        }
    }

}
