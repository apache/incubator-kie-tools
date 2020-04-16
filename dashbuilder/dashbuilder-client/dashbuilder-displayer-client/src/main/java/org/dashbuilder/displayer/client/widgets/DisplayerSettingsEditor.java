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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.MapColorScheme;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.dashbuilder.displayer.client.events.DisplayerSettingsChangedEvent;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_3D;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_BGCOLOR;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_HEIGHT;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_LEGENDPOSITION;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_MARGIN_BOTTOM;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_MARGIN_LEFT;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_MARGIN_RIGHT;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_MARGIN_TOP;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_RESIZABLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_SHOWLEGEND;
import static org.dashbuilder.displayer.DisplayerAttributeDef.CHART_WIDTH;
import static org.dashbuilder.displayer.DisplayerAttributeDef.EXPORT_TO_CSV;
import static org.dashbuilder.displayer.DisplayerAttributeDef.EXPORT_TO_XLS;
import static org.dashbuilder.displayer.DisplayerAttributeDef.FILTER_ENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.FILTER_LISTENING_ENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.FILTER_NOTIFICATION_ENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.FILTER_SELFAPPLY_ENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.MAP_COLOR_SCHEME;
import static org.dashbuilder.displayer.DisplayerAttributeDef.METER_CRITICAL;
import static org.dashbuilder.displayer.DisplayerAttributeDef.METER_END;
import static org.dashbuilder.displayer.DisplayerAttributeDef.METER_START;
import static org.dashbuilder.displayer.DisplayerAttributeDef.METER_WARNING;
import static org.dashbuilder.displayer.DisplayerAttributeDef.REFRESH_INTERVAL;
import static org.dashbuilder.displayer.DisplayerAttributeDef.REFRESH_STALE_DATA;
import static org.dashbuilder.displayer.DisplayerAttributeDef.RENDERER;
import static org.dashbuilder.displayer.DisplayerAttributeDef.SELECTOR_MULTIPLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.SELECTOR_SHOW_INPUTS;
import static org.dashbuilder.displayer.DisplayerAttributeDef.SELECTOR_WIDTH;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_COLUMN_PICKER_ENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_PAGESIZE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_SORTCOLUMNID;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_SORTENABLED;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_SORTORDER;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TABLE_WIDTH;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TITLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.TITLE_VISIBLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.XAXIS_LABELSANGLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.XAXIS_SHOWLABELS;
import static org.dashbuilder.displayer.DisplayerAttributeDef.XAXIS_TITLE;
import static org.dashbuilder.displayer.DisplayerAttributeDef.YAXIS_SHOWLABELS;
import static org.dashbuilder.displayer.DisplayerAttributeDef.YAXIS_TITLE;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_LEGEND_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_MARGIN_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.COLUMNS_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.EXPORT_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.FILTER_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.GENERAL_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.MAP_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.METER_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.REFRESH_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.SELECTOR_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.TABLE_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.XAXIS_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.YAXIS_GROUP;

@Dependent
public class DisplayerSettingsEditor implements IsWidget {

    public interface View extends UberView<DisplayerSettingsEditor> {

        void clear();

        void show();

        void dataSetNotFound();

        void error(String message);

        void addCategory(DisplayerAttributeDef attributeDef);

        void addTextProperty(DisplayerAttributeDef attributeDef, String value, PropertyFieldValidator... validators);

        void addTextProperty(String propId, String propName, String value, PropertyFieldValidator... validators);

        void addBooleanProperty(DisplayerAttributeDef attributeDef, boolean value);

        void addColorProperty(DisplayerAttributeDef attributeDef, String color);

        void addListProperty(DisplayerAttributeDef attributeDef, List<String> optionList, String selectedValue);

        String getColumnNameI18n();

        String getColumnExpressionI18n();

        String getColumnPatternI18n();

        String getPositionLiteralI18n(Position position);

        String getIntegerValidationFailedI18n();

        String getDoubleValidationFailedI18n();

        String getMeterStartI18n();

        String getMeterWarningI18n();

        String getMeterCriticalI18n();

        String getMeterEndI18n();

        String getMeterUnknownI18n();

        String getMeterValidationHigherI18n(String level);

        String getMeterValidationLowerI18n(String level);

        String getMeterValidationInvalidI18n();

        String getMapColorSchemeI18n(MapColorScheme colorScheme);
    }

    protected View view;
    protected DisplayerLocator displayerLocator;
    protected RendererManager rendererManager;
    protected Displayer displayer;
    protected DisplayerSettings displayerSettings;
    protected DisplayerConstraints displayerContraints;
    private Set<DisplayerAttributeDef> supportedAttributes;
    protected Event<DisplayerSettingsChangedEvent> settingsChangedEvent;

    public static final String COLUMNS_PREFFIX = "columns.";

    @Inject
    public DisplayerSettingsEditor(View view,
                                   DisplayerLocator displayerLocator,
                                   RendererManager rendererManager,
                                   Event<DisplayerSettingsChangedEvent> settingsChangedEvent) {
        this.view = view;
        this.displayerLocator = displayerLocator;
        this.rendererManager = rendererManager;
        this.settingsChangedEvent = settingsChangedEvent;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void init(Displayer displayer) {
        try {
            this.displayer = displayer;
            this.displayerSettings = displayer.getDisplayerSettings();
            this.displayerContraints = displayer.getDisplayerConstraints();
            this.supportedAttributes = displayerContraints.getSupportedAttributes();

            displayer.getDataSetHandler().lookupDataSet(new DataSetReadyCallback() {

                @Override
                public void callback(DataSet dataSet) {
                    show();
                }

                @Override
                public void notFound() {
                    view.dataSetNotFound();
                }

                @Override
                public boolean onError(final ClientRuntimeError error) {
                    console(error.getThrowable());
                    view.error(error.getMessage());
                    return false;
                }
            });
        } catch (Exception e) {
            console(e);
            view.error(e.toString());
        }
    }

    public boolean isSupported(DisplayerAttributeDef attributeDef) {
        return supportedAttributes.contains(attributeDef);
    }

    public boolean isSupported(DisplayerAttributeGroupDef groupDef) {
        if (supportedAttributes.contains(groupDef)) {

            for (DisplayerAttributeDef attrDef : groupDef.getChildren()) {
                if (attrDef instanceof DisplayerAttributeGroupDef) {
                    continue;
                }
                if (supportedAttributes.contains(attrDef)) {
                    return true;
                }
            }
        }
        return false;
    }

    void show() {
        view.clear();

        if (isSupported(GENERAL_GROUP)) {
            view.addCategory(GENERAL_GROUP);

            if (isSupported(TITLE)) {
                view.addTextProperty(TITLE, displayerSettings.getTitle());
            }
            if (isSupported(TITLE_VISIBLE)) {
                view.addBooleanProperty(TITLE_VISIBLE, displayerSettings.isTitleVisible());
            }
        }
        if (isSupported(RENDERER)) {
            view.addCategory(RENDERER);

            List<String> optionList = new ArrayList<String>();
            for (RendererLibrary option : rendererManager.getRenderersForType(displayerSettings.getType())) {
                optionList.add(option.getUUID());
            }
            if (optionList.size() > 1) {
                RendererLibrary renderer = rendererManager.getRendererForDisplayer(displayerSettings);
                view.addListProperty(RENDERER, optionList, renderer.getUUID());
            }
        }
        if (isSupported(CHART_GROUP)) {
            view.addCategory(CHART_GROUP);

            if (isSupported(CHART_RESIZABLE)) {
                view.addBooleanProperty(CHART_RESIZABLE, displayerSettings.isResizable());
            }
            if (isSupported(CHART_WIDTH)) {
                view.addTextProperty(CHART_WIDTH, String.valueOf(displayerSettings.getChartWidth()), createLongValidator());
            }
            if (isSupported(CHART_HEIGHT)) {
                view.addTextProperty(CHART_HEIGHT, String.valueOf(displayerSettings.getChartHeight()), createLongValidator());
            }
            if (isSupported(CHART_BGCOLOR)) {
                view.addColorProperty(CHART_BGCOLOR, displayerSettings.getChartBackgroundColor());
            }
            if (isSupported(CHART_3D)) {
                view.addBooleanProperty(CHART_3D, displayerSettings.isChart3D());
            }
        }
        if (isSupported(CHART_MARGIN_GROUP)) {
            view.addCategory(CHART_MARGIN_GROUP);

            if (isSupported(CHART_MARGIN_TOP)) {
                view.addTextProperty(CHART_MARGIN_TOP, String.valueOf(displayerSettings.getChartMarginTop()), createLongValidator());
            }
            if (isSupported(CHART_MARGIN_BOTTOM)) {
                view.addTextProperty(CHART_MARGIN_BOTTOM, String.valueOf(displayerSettings.getChartMarginBottom()), createLongValidator());
            }
            if (isSupported(CHART_MARGIN_LEFT)) {
                view.addTextProperty(CHART_MARGIN_LEFT, String.valueOf(displayerSettings.getChartMarginLeft()), createLongValidator());
            }
            if (isSupported(CHART_MARGIN_RIGHT)) {
                view.addTextProperty(CHART_MARGIN_RIGHT, String.valueOf(displayerSettings.getChartMarginRight()), createLongValidator());
            }
        }
        if (isSupported(CHART_LEGEND_GROUP)) {
            view.addCategory(CHART_LEGEND_GROUP);

            if (isSupported(CHART_SHOWLEGEND)) {
                view.addBooleanProperty(CHART_SHOWLEGEND, displayerSettings.isChartShowLegend());
            }
            if (isSupported(CHART_LEGENDPOSITION)) {
                List<String> optionList = new ArrayList<String>();
                for (Position position : Position.values()) {
                    String positionLabel = view.getPositionLiteralI18n(position);
                    optionList.add(positionLabel);
                }
                if (optionList.size() > 1) {
                    String positionLabel = view.getPositionLiteralI18n(displayerSettings.getChartLegendPosition());
                    view.addListProperty(CHART_LEGENDPOSITION, optionList, positionLabel);
                }
            }
        }
        if (isSupported(XAXIS_GROUP) || isSupported(YAXIS_GROUP)) {
            view.addCategory(XAXIS_GROUP);

            if (isSupported(XAXIS_SHOWLABELS)) {
                view.addBooleanProperty(XAXIS_SHOWLABELS, displayerSettings.isXAxisShowLabels());
            }
            if (isSupported(XAXIS_TITLE)) {
                view.addTextProperty(XAXIS_TITLE, displayerSettings.getXAxisTitle());
            }
            if (isSupported(XAXIS_LABELSANGLE)) {
                view.addTextProperty(XAXIS_LABELSANGLE, String.valueOf(displayerSettings.getXAxisLabelsAngle()));
            }
            if (isSupported(YAXIS_SHOWLABELS)) {
                view.addBooleanProperty(YAXIS_SHOWLABELS, displayerSettings.isXAxisShowLabels());
            }
            if (isSupported(YAXIS_TITLE)) {
                view.addTextProperty(YAXIS_TITLE, displayerSettings.getYAxisTitle());
            }
        }
        if (isSupported(TABLE_GROUP)) {
            view.addCategory(TABLE_GROUP);

            if (isSupported(TABLE_PAGESIZE)) {
                view.addTextProperty(TABLE_PAGESIZE, String.valueOf(displayerSettings.getTablePageSize()), createLongValidator());
            }
            if (isSupported(TABLE_WIDTH)) {
                view.addTextProperty(TABLE_WIDTH, String.valueOf(displayerSettings.getTableWidth()), createLongValidator());
            }
            if (isSupported(TABLE_SORTENABLED)) {
                view.addBooleanProperty(TABLE_SORTENABLED, displayerSettings.isTableSortEnabled());
            }
            if (isSupported(TABLE_SORTCOLUMNID)) {
                final List<String> optionList = new ArrayList<String>();
                DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
                List<DataColumn> dsColumns = dataSet.getColumns();
                optionList.add("");
                for (DataColumn column : dsColumns) {
                    optionList.add(column.getId());
                }
                view.addListProperty(TABLE_SORTCOLUMNID, optionList, displayerSettings.getTableDefaultSortColumnId());
            }
            if (isSupported(TABLE_SORTORDER)) {
                List<String> optionList = new ArrayList<String>();
                optionList.add(SortOrder.ASCENDING.toString());
                optionList.add(SortOrder.DESCENDING.toString());
                view.addListProperty(TABLE_SORTORDER, optionList, displayerSettings.getTableDefaultSortOrder().toString());
            }
            if (isSupported(TABLE_COLUMN_PICKER_ENABLED)) {
                view.addBooleanProperty(TABLE_COLUMN_PICKER_ENABLED, displayerSettings.isTableColumnPickerEnabled());
            }
        }
        if (isSupported(METER_GROUP)) {
            view.addCategory(METER_GROUP);

            if (isSupported(METER_START)) {
                view.addTextProperty(METER_START, String.valueOf(displayerSettings.getMeterStart()), createMeterValidator(displayerSettings, 0));
            }
            if (isSupported(METER_WARNING)) {
                view.addTextProperty(METER_WARNING, String.valueOf(displayerSettings.getMeterWarning()), createMeterValidator(displayerSettings, 1));
            }
            if (isSupported(METER_CRITICAL)) {
                view.addTextProperty(METER_CRITICAL, String.valueOf(displayerSettings.getMeterCritical()), createMeterValidator(displayerSettings, 2));
            }
            if (isSupported(METER_END)) {
                view.addTextProperty(METER_END, String.valueOf(displayerSettings.getMeterEnd()), createMeterValidator(displayerSettings, 3));
            }

        }
        if (isSupported(FILTER_GROUP)) {
            view.addCategory(FILTER_GROUP);

            if (isSupported(FILTER_ENABLED)) {
                view.addBooleanProperty(FILTER_ENABLED, displayerSettings.isFilterEnabled());
            }
            if (isSupported(FILTER_SELFAPPLY_ENABLED)) {
                view.addBooleanProperty(FILTER_SELFAPPLY_ENABLED, displayerSettings.isFilterSelfApplyEnabled());
            }
            if (isSupported(FILTER_LISTENING_ENABLED)) {
                view.addBooleanProperty(FILTER_LISTENING_ENABLED, displayerSettings.isFilterListeningEnabled());
            }
            if (isSupported(FILTER_NOTIFICATION_ENABLED)) {
                view.addBooleanProperty(FILTER_NOTIFICATION_ENABLED, displayerSettings.isFilterNotificationEnabled());
            }
        }
        if (isSupported(SELECTOR_GROUP)) {
            view.addCategory(SELECTOR_GROUP);

            if (isSupported(SELECTOR_WIDTH)) {
                view.addTextProperty(SELECTOR_WIDTH, String.valueOf(displayerSettings.getSelectorWidth()), createLongValidator());
            }
            if (isSupported(SELECTOR_MULTIPLE)) {
                view.addBooleanProperty(SELECTOR_MULTIPLE, displayerSettings.isSelectorMultiple());
            }
            if (isSupported(SELECTOR_SHOW_INPUTS)) {
                view.addBooleanProperty(SELECTOR_SHOW_INPUTS, displayerSettings.isSelectorInputsEnabled());
            }
        }
        if (isSupported(REFRESH_GROUP)) {
            view.addCategory(REFRESH_GROUP);

            if (isSupported(REFRESH_INTERVAL)) {
                view.addTextProperty(REFRESH_INTERVAL, String.valueOf(displayerSettings.getRefreshInterval()), createLongValidator());
            }
            if (isSupported(REFRESH_STALE_DATA)) {
                view.addBooleanProperty(REFRESH_STALE_DATA, displayerSettings.isRefreshStaleData());
            }
        }
        if (isSupported(COLUMNS_GROUP)) {
            view.addCategory(COLUMNS_GROUP);

            DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
            for (int i = 0; i < dataSet.getColumns().size(); i++) {

                DataColumn dataColumn = dataSet.getColumnByIndex(i);
                ColumnSettings cs = displayerSettings.getColumnSettings(dataColumn);
                String fieldSuffix = COLUMNS_PREFFIX + cs.getColumnId() + ".";
                String expression = cs.getValueExpression();
                String pattern = cs.getValuePattern();

                view.addTextProperty(fieldSuffix + "name", view.getColumnNameI18n() + (i + 1), cs.getColumnName());

                if (expression != null) {
                    view.addTextProperty(fieldSuffix + "expression", "     " + view.getColumnExpressionI18n(), expression);
                }
                if (pattern != null) {
                    view.addTextProperty(fieldSuffix + "pattern", "     " + view.getColumnPatternI18n(), pattern);
                }
                /* Non-critical. Disable for the time being.
                if (isSupported(COLUMN_EMPTY)) {
                    String empty = cs.getEmptyTemplate();
                    view.addTextProperty(fieldSuffix + "empty", "     " + view.getColumnEmptyI18n(), empty);
                }*/
            }
        }
        if (isSupported(EXPORT_GROUP)) {
            view.addCategory(EXPORT_GROUP);

            if (isSupported(EXPORT_TO_CSV)) {
                view.addBooleanProperty(EXPORT_TO_CSV, displayerSettings.isCSVExportAllowed());
            }
            if (isSupported(EXPORT_TO_XLS)) {
                view.addBooleanProperty(EXPORT_TO_XLS, displayerSettings.isExcelExportAllowed());
            }
        }

        if (isSupported(MAP_GROUP)) {
            view.addCategory(MAP_GROUP);

            if (isSupported(MAP_COLOR_SCHEME)) {
                List<String> colorsSchemes = Stream.of(MapColorScheme.values())
                                                   .map(view::getMapColorSchemeI18n)
                                                   .collect(Collectors.toList());

                String mapColorSchemePositionLabel = view.getMapColorSchemeI18n(displayerSettings.getMapColorScheme());
                view.addListProperty(MAP_COLOR_SCHEME, colorsSchemes, mapColorSchemePositionLabel);
            }
        }
        view.show();
    }

    void onAttributeChanged(String attrKey, String attrValue) {
        if (attrKey.startsWith(COLUMNS_PREFFIX)) {
            String[] strings = attrKey.split("\\.");
            if (strings.length == 3) {

                String columnId = strings[1];
                String setting = strings[2];

                if ("name".equals(setting)) {
                    displayerSettings.setColumnName(columnId, attrValue);
                } else if ("empty".equals(setting)) {
                    displayerSettings.setColumnEmptyTemplate(columnId, attrValue);
                } else if ("pattern".equals(setting)) {
                    displayerSettings.setColumnValuePattern(columnId, attrValue);
                } else if ("expression".equals(setting)) {
                    displayerSettings.setColumnValueExpression(columnId, attrValue);
                }
            }
        } else {
            displayerSettings.setDisplayerSetting(attrKey, attrValue);
        }

        settingsChangedEvent.fire(new DisplayerSettingsChangedEvent(displayerSettings));
    }

    // Custom property validators

    public LongValidator createLongValidator() {
        return new LongValidator();
    }

    public DoubleValidator createDoubleValidator() {
        return new DoubleValidator();
    }

    public MeterValidator createMeterValidator(DisplayerSettings settings, int level) {
        return new MeterValidator(settings, level);
    }

    /**
     * Property Editor Validator for integers
     */
    public class LongValidator implements PropertyFieldValidator {

        @Override
        public boolean validate(Object value) {
            try {
                Long.parseLong(value.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String getValidatorErrorMessage() {
            return view.getIntegerValidationFailedI18n();
        }
    }

    /**
     * Property Editor Validator for doubles
     */
    public class DoubleValidator implements PropertyFieldValidator {

        @Override
        public boolean validate(Object value) {
            try {
                Double.parseDouble(value.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String getValidatorErrorMessage() {
            return view.getDoubleValidationFailedI18n();
        }
    }

    /**
     * Property Editor Validator for meter intervals
     */
    public class MeterValidator extends LongValidator {

        private DisplayerSettings displayerSettings;
        private int level;
        private boolean lowerOk = true;
        private boolean upperOk = true;

        public MeterValidator(DisplayerSettings displayerSettings, int level) {
            this.displayerSettings = displayerSettings;
            this.level = level;
        }

        private long getLevelValue(int level) {
            switch (level) {
                case 0:
                    return displayerSettings.getMeterStart();
                case 1:
                    return displayerSettings.getMeterWarning();
                case 2:
                    return displayerSettings.getMeterCritical();
                case 3:
                    return displayerSettings.getMeterEnd();
            }
            return level < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
        }

        private String getLevelDescr(int level) {
            switch (level) {
                case 0:
                    return view.getMeterStartI18n();
                case 1:
                    return view.getMeterWarningI18n();
                case 2:
                    return view.getMeterCriticalI18n();
                case 3:
                    return view.getMeterEndI18n();
            }
            return view.getMeterUnknownI18n();
        }

        @Override
        public boolean validate(Object value) {
            if (!super.validate(value)) {
                return false;
            }
            long thisLevel = Long.parseLong(value.toString());
            long lowerLevel = getLevelValue(level - 1);
            long upperLevel = getLevelValue(level + 1);
            lowerOk = thisLevel >= lowerLevel;
            upperOk = thisLevel <= upperLevel;
            return lowerOk && upperOk;
        }

        @Override
        public String getValidatorErrorMessage() {
            if (!lowerOk) {
                return view.getMeterValidationHigherI18n(getLevelDescr(level - 1));
            }
            if (!upperOk) {
                return view.getMeterValidationLowerI18n(getLevelDescr(level + 1));
            }
            return view.getMeterValidationInvalidI18n();
        }
    }

    private void console(Throwable e) {
        if (DomGlobal.console != null) {
            DomGlobal.console.error("Error running displayer.");
            DomGlobal.console.error(e);
        }
    }
}