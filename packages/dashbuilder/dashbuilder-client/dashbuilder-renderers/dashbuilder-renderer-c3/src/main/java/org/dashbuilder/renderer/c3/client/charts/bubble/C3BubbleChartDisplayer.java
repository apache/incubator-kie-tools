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
package org.dashbuilder.renderer.c3.client.charts.bubble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import elemental2.core.JsObject;
import jsinterop.base.Any;
import jsinterop.base.Js;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.renderer.c3.client.C3Displayer;
import org.dashbuilder.renderer.c3.client.C3XYDisplayer;
import org.dashbuilder.renderer.c3.client.charts.CommonC3DisplayerConstants;
import org.dashbuilder.renderer.c3.client.jsbinding.C3DataInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Point;

@Dependent
public class C3BubbleChartDisplayer extends C3XYDisplayer<C3BubbleChartDisplayer.View> {

    public interface View extends C3Displayer.View<C3BubbleChartDisplayer> {
    }

    private static final int X_INDEX = 1;
    private static final int Y_INDEX = 2;
    private static final int R_INDEX = 3;

    private View view;

    @Inject
    public C3BubbleChartDisplayer(View view, FilterLabelSet filterLabelSet, C3JsTypesFactory factory) {
        super(filterLabelSet, factory);
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setMaxColumns(4)
                .setMinColumns(2)
                .setExtraColumnsAllowed(false)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setColumnTitle(1, "X")
                .setColumnTitle(2, "Y")
                .setColumnTitle(3, "Bubble Size")
                .setColumnTypes(new ColumnType[]{
                                                 ColumnType.LABEL,
                                                 ColumnType.NUMBER,
                                                 ColumnType.NUMBER,
                                                 ColumnType.LABEL});

        return new CommonC3DisplayerConstants(lookupConstraints).create()
                .supportsAttribute(DisplayerAttributeGroupDef.AXIS_GROUP);
    }

    protected String[][] createSeries() {
        List<DataColumn> columns = dataSet.getColumns();
        String[] categories = createCategories();
        String[][] data = new String[categories.length * 2][];
        List<BubbleData> bubbleData = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            String x = columns.get(X_INDEX).getValues().get(i).toString();
            String y = columns.get(Y_INDEX).getValues().get(i).toString();
            bubbleData.add(new BubbleData(Double.valueOf(x),
                    Double.valueOf(y),
                    categories[i]));
        }
        Collections.sort(bubbleData, Comparator.comparingDouble(BubbleData::getX));
        for (int i = 0, y = 0; i < bubbleData.size(); i++) {
            BubbleData bData = bubbleData.get(i);
            String catY = bData.getCategory();
            String catX = bData.getCategory() + "_x";
            Double xVal = bData.getX();
            Double yVal = bData.getY();
            data[y] = new String[]{catX, xVal.toString()};
            data[y + 1] = new String[]{catY, yVal.toString()};
            y += 2;
        }
        return data;
    }

    @Override
    protected JsObject createXs() {
        JsObject xs = JsObject.create(null);
        String[] categories = createCategories();
        for (String category : categories) {
            Js.<Any> cast(xs).asPropertyMap().set(category, category + "_x");
        }
        return xs;
    }

    @Override
    protected C3Point createPoint() {
        var bubbleMinRadius = displayerSettings.getBubbleMinRadius();
        var bubbleMaxRadius = displayerSettings.getBubbleMaxRadius();
        List<DataColumn> columns = dataSet.getColumns();
        String[] categories = createCategories();
        int n = categories.length;
        Map<String, Double> rValues = new HashMap<>();
        List<Double> valuesBeforeMap = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String valueStr = columns.get(R_INDEX).getValues().get(i).toString();
            try {
                valuesBeforeMap.add(Double.parseDouble(valueStr));
            } catch (NumberFormatException e) {
                GWT.log("Not able to retrieve radius values. Exiting radius calculation.", e);
                return super.createPoint();
            }
        }
        double min = Collections.min(valuesBeforeMap);
        double max = Collections.max(valuesBeforeMap);
        for (int i = 0; i < n; i++) {
            String cat = categories[i];
            double r;
            if (min == max) {
                r = (bubbleMinRadius + bubbleMaxRadius) / 2;
            } else {
                r = map(valuesBeforeMap.get(i), min, max, bubbleMinRadius, bubbleMaxRadius);
            }
            rValues.put(cat, r);
        }
        return factory.c3Point(d -> rValues.get(d.getId()));
    }

    @Override
    protected String getSelectedCategory(C3DataInfo info) {
        return info.getName();
    }

    @Override
    protected int getSelectedRowIndex(C3DataInfo info) {
        String[] categories = createCategories();
        return Arrays.asList(categories).indexOf(info.getName());
    }

}
