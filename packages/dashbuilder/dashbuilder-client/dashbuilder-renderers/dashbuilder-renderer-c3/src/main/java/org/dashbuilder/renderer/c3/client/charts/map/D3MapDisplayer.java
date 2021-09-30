/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.charts.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.MapColorScheme;
import org.dashbuilder.renderer.c3.client.C3AbstractDisplayer;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.CountriesGeoJsonService;

@Dependent
public class D3MapDisplayer extends C3AbstractDisplayer<D3MapDisplayer.View>  {
    
    View view;
    private boolean markers;
    private boolean regions;
    private CountriesGeoJsonService geoService;
    private D3MapConf conf;

    public interface View extends C3AbstractDisplayer.View<D3MapDisplayer> {

        String getColumnsTitle();

        String getGroupsTitle();
        
        void createMap(D3MapConf conf);

    }
    
    @Override
    protected void createVisualization() {
        getView().setFilterLabelSet(filterLabelSet);
        super.createVisualization();
    }
    
    @Inject
    public D3MapDisplayer(FilterLabelSet filterLabelSet, View view, CountriesGeoJsonService countriesGeoJsonService) {
        super(filterLabelSet);
        view.init(this);
        this.view = view;
        this.geoService = countriesGeoJsonService;
    }
    
    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMinColumns(2)
                .setMaxColumns(3)
                .setExtraColumnsAllowed(true)
                .setGroupsTitle(getView().getGroupsTitle())
                .setColumnsTitle(getView().getColumnsTitle())
                .setColumnTypes(new ColumnType[] {
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                   .supportsAttribute(DisplayerAttributeDef.TYPE)
                   .supportsAttribute(DisplayerAttributeDef.SUBTYPE)
                   .supportsAttribute(DisplayerAttributeDef.RENDERER)
                   .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                   .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                   .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                   .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                   .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                   .supportsAttribute(DisplayerAttributeDef.CHART_SHOWLEGEND)
                   .supportsAttribute(DisplayerAttributeGroupDef.MAP_GROUP);        
    }

    @Override
    protected void updateVisualizationWithData() {
        Map<String, Double> data = retrieveData(dataSet);
        String backgroundColor = displayerSettings.getChartBackgroundColor();
        String columnName = getDataColumnName();
        MapColorScheme colorScheme = displayerSettings.getMapColorScheme();
        conf = D3MapConf.of(columnName, 
                            data, 
                            markers, 
                            regions, 
                            backgroundColor, 
                            geoService, 
                            this::format, 
                            this::selectLocation, 
                            colorScheme,
                            isShowLegend());
        getView().createMap(conf);
    }

    private String getDataColumnName() {
        String name = "Data";
        if (dataSet.getColumns().size() > 1) {
            DataColumn column = dataSet.getColumns().get(1);
            name = displayerSettings.getColumnSettings(column).getColumnName();
        }
        return name;
    }

    protected Map<String, Double> retrieveData(DataSet dataSet) {
        Map<String, Double> data = new HashMap<>();
        List<DataColumn> columns = dataSet.getColumns();
        List<?> locations  = columns.get(0).getValues();
        List<?> numbers = columns.get(1).getValues();
        
        int total = locations.size();
        
        for (int i = 0; i < total; i++) {
            Object locationValue = locations.get(i);
            Object numberValue = numbers.get(i);
            String numberStr = columnValueToString(numberValue);
            String location = columnValueToString(locationValue);
            Double value = Double.parseDouble(numberStr);
            data.put(location, value);
        }
        
        return data;
    }
    
    public void selectLocation(String location) {
        if (displayerSettings.isFilterNotificationEnabled()) {
            List<DataColumn> columns = dataSet.getColumns();
            List<?> locations  = columns.get(0).getValues();
            int rowIndex = locations.indexOf(location);
            if (rowIndex != -1) {
                addToSelection(rowIndex);
            }
        }
    }
    
    public String format(Double value) {
        String textValue = value.toString();
        List<DataColumn> columns = dataSet.getColumns();
        if (columns.size() > 1) {
            textValue  = super.formatValue(value, columns.get(1));
        }
        return textValue;
    }

    public D3MapDisplayer markers() {
        this.markers = true;
        return this;
    }

    public D3MapDisplayer regions() {
        this.regions = true;
        return this;
    }

    public D3MapConf getConf() {
        return conf;
    }
    
    public boolean isShowLegend() {
        return displayerSettings.isChartShowLegend();
    }

}