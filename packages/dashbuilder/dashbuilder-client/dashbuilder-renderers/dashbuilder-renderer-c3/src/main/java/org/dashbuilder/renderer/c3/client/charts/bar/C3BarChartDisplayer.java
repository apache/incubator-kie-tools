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
package org.dashbuilder.renderer.c3.client.charts.bar;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.renderer.c3.client.C3Displayer;
import org.dashbuilder.renderer.c3.client.C3XYDisplayer;
import org.dashbuilder.renderer.c3.client.charts.CommonC3DisplayerConstants;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick;

import com.google.gwt.i18n.client.NumberFormat;

@Dependent
public class C3BarChartDisplayer extends C3XYDisplayer<C3BarChartDisplayer.View> {
    
    
    public interface View extends C3Displayer.View<C3BarChartDisplayer> {
    }
    
    private boolean rotated;
    private View view;
    
    
    @Inject
    public C3BarChartDisplayer(View view, FilterLabelSet filterLabelSet, C3JsTypesFactory factory) {
        super(filterLabelSet, factory);
        this.view = view;
        this.view.init(this);
    }
    
    public C3BarChartDisplayer notRotated() {
        this.setRotated(false);
        this.setStacked(false);
        return this;
    }
    
    public C3BarChartDisplayer rotated() {
        this.setRotated(true);
        this.setStacked(false);
        return this;
    }
    
    public C3BarChartDisplayer stacked() {
        this.setRotated(false);
        this.setStacked(true);
        return this;
    }
    
    public C3BarChartDisplayer stackedAndRotated() {
        this.setRotated(true);
        this.setStacked(true);
        return this;
    }
    
    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(10)
                .setMinColumns(2)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType( ColumnType.NUMBER)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setColumnTypes(new ColumnType[] {
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new CommonC3DisplayerConstants(lookupConstraints).create()
                        .supportsAttribute(DisplayerAttributeDef.SUBTYPE)
                        .supportsAttribute(DisplayerAttributeGroupDef.AXIS_GROUP);
    }
    
    
    @Override
    protected C3AxisInfo createAxis() {
        C3AxisInfo axis = super.createAxis();
        axis.setRotated(isRotated());
        axis.getY().getTick().setRotate(30);
        return axis;
    }
    
    public boolean isRotated() {
        return rotated;
    }
    
    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    @Override
    public View getView() {
        return view;
    }

}