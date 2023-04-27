/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.metric;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringTemplateBuilder;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;

@Dependent
public class MetricDisplayer extends AbstractGwtDisplayer<MetricDisplayer.View> {

    public interface View extends AbstractGwtDisplayer.View<MetricDisplayer> {

        String getUniqueId();

        void setHtml(String html);

        void eval(String js);

        String getNoDataString();

        String getColumnsTitle();
    }

    public static final List<String> TEMPLATE_KEYS = Arrays.asList("value.raw", "value", "title",
            "width", "height", "marginTop", "marginBottom", "marginRight", "marginLeft", "bgColor",
            "isFilterEnabled", "isFilterOn", "isEmpty", "doFilter");

    public static final String DEFAULT_HTML_TEMPLATE = "<div id=\"${this}\" class=\"card-pf card-pf-accented card-pf-aggregate-status\" " +
            "style=\"background-color:${bgColor}; width:${width}px; height:${height}px; " +
            "margin-top:${marginTop}px; margin-right:${marginRight}px; margin-bottom:${marginBottom}px; margin-left:${marginLeft}px;\">\n" +
            "  <h3>${title}</h3>\n" +
            "  <h2>${value}</h2>\n" +
            "</div>";

    public static final String DEFAULT_JS_TEMPLATE = "if (${isFilterEnabled}) {  \n" +
            "  var filterOn = false;\n" +
            "  ${this}.style.cursor=\"pointer\";\n" +
            "\n" +
            "  ${this}.onmouseover = function() {\n" +
            "    if (!filterOn) ${this}.style.backgroundColor = \"lightblue\";\n" +
            "  };\n" +
            "  ${this}.onmouseout = function() {\n" +
            "    if (!filterOn) ${this}.style.backgroundColor = \"${bgColor}\";\n" +
            "  };\n" +
            "  ${this}.onclick = function() {\n" +
            "    filterOn = !filterOn;\n" +
            "    ${this}.style.backgroundColor = filterOn ? \"lightblue\" : \"${bgColor}\";\n" +
            "    ${doFilter};\n" +
            "  };\n" +
            "}";
    
    private static final String DARK_MODE_DEFAULT_COLOR = "#211d3b";

    protected View view;
    protected boolean filterOn = false;
    protected StringTemplateBuilder codeBuilder = new StringTemplateBuilder();

    @Inject
    public MetricDisplayer(View view) {
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
                .setGroupAllowed(true)
                .setMinColumns(1)
                .setExtraColumnsAllowed(false)
                .setColumnsTitle(view.getColumnsTitle());

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.METER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.HTML_GROUP);
    }

    @Override
    protected void createVisualization() {
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        if (displayerSettings.getMode() == Mode.DARK &&
            "".equals(displayerSettings.getChartBackgroundColor()) ) {
            displayerSettings.setChartBackgroundColor(DARK_MODE_DEFAULT_COLOR);
        }
        String template = getHtmlTemplate();
        String html = parseHtmlTemplate(template);
        view.setHtml(html);

        // Invoke the onDraw JS callback if defined
        String onDrawJs = getJsTemplate();
        if (onDrawJs != null) {
            onDrawJs = parseJsTemplate(onDrawJs);
            view.eval(onDrawJs);
        }
    }

    public String parseHtmlTemplate(String str) {
        parseTemplate(str);

        // Replace the extra keys with a reference to a displayer-bounded identifier
        for (String key : codeBuilder.keys()) {
            if (!TEMPLATE_KEYS.contains(key)) {
                String id = getExtraKeyId(key);
                codeBuilder.replace(key, id);
            }
        }
        return codeBuilder.build();
    }

    public String parseJsTemplate(String str) {
        parseTemplate(str);

        // Replace the extra keys with a reference to its DOM element
        for (String key : codeBuilder.keys()) {
            if (!TEMPLATE_KEYS.contains(key)) {
                String id = getExtraKeyId(key);
                codeBuilder.replace(key, "document.getElementById(\"" + id + "\")");
            }
        }
        return codeBuilder.build();
    }

    protected void parseTemplate(String template) {
        boolean isEmpty = dataSet.getRowCount() == 0;
        Double valueRaw = isEmpty ? 0 : (Double) dataSet.getValueAt(0, 0);
        String valueStr = isEmpty ? view.getNoDataString() : super.formatValue(0, 0);
        String title = displayerSettings.isTitleVisible() ? displayerSettings.getTitle() : "";
        String bgcolor = displayerSettings.getChartBackgroundColor();
        bgcolor = !StringUtils.isBlank(bgcolor) ? bgcolor : "white";

        try {
            Integer.parseInt(bgcolor, 16);
            bgcolor = "#" + bgcolor;
        } catch (NumberFormatException e) {
            // No hash prefix needed
        }

        // Replace the core keys by their corresponding displayer settings.
        codeBuilder.setTemplate(template);
        codeBuilder.replace("value.raw", Double.toString(valueRaw))
                .replace("value", valueStr != null ? valueStr : "")
                .replace("title", title)
                .replace("width", Integer.toString(displayerSettings.getChartWidth()))
                .replace("height", Integer.toString(displayerSettings.getChartHeight()))
                .replace("marginTop", Integer.toString(displayerSettings.getChartMarginTop()))
                .replace("marginBottom", Integer.toString(displayerSettings.getChartMarginBottom()))
                .replace("marginRight", Integer.toString(displayerSettings.getChartMarginRight()))
                .replace("marginLeft", Integer.toString(displayerSettings.getChartMarginLeft()))
                .replace("value.start", Long.toString(displayerSettings.getMeterStart()))
                .replace("value.warning", Long.toString(displayerSettings.getMeterWarning()))
                .replace("value.critical", Long.toString(displayerSettings.getMeterCritical()))
                .replace("value.end", Long.toString(displayerSettings.getMeterEnd()))
                .replace("bgColor", bgcolor)
                .replace("isFilterEnabled", Boolean.toString(isFilterEnabled()))
                .replace("isFilterOn", Boolean.toString(isFilterOn()))
                .replace("isEmpty", Boolean.toString(isEmpty))
                .replace("doFilter", "window.metricDisplayerDoFilter('" + view.getUniqueId() + "')");
    }

    public String getExtraKeyId(String key) {
        return view.getUniqueId()  + "_" + key;
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    public void setFilterOn(boolean on) {
        filterOn = on;
    }

    public boolean isFilterEnabled() {
        return displayerSettings.isFilterEnabled() && fetchFilter() != null;
    }

    public void updateFilter() {
        if (isFilterEnabled()) {

            if (filterOn) {
                filterReset();
            } else {
                if (displayerSettings.isFilterEnabled()) {
                    filterApply();
                }
            }
        }
    }

    public DataSetFilter fetchFilter() {
        if (displayerSettings.getDataSetLookup() == null) {
            return null;
        }
        List<DataSetFilter> filterOps = displayerSettings.getDataSetLookup().getOperationList(DataSetFilter.class);
        if (filterOps == null || filterOps.isEmpty()) {
            return null;
        }
        DataSetFilter filter = new DataSetFilter();
        for (DataSetFilter filterOp : filterOps) {
            filter.getColumnFilterList().addAll(filterOp.getColumnFilterList());
        }
        return filter;
    }

    public void filterApply() {
        DataSetFilter filter = fetchFilter();
        if (displayerSettings.isFilterEnabled() && filter != null) {
            filterOn = true;
            super.filterApply(filter);
        }
    }

    @Override
    public void filterReset() {
        DataSetFilter filter = fetchFilter();
        if (filterOn && filter != null) {
            filterOn = false;
            super.filterReset();
        }
    }

    public String getHtmlTemplate() {
        String template = displayerSettings.getHtmlTemplate();
        if (StringUtils.isBlank(template)) {
            return DEFAULT_HTML_TEMPLATE;
        }
        return template;
    }

    public String getJsTemplate() {
        String template = displayerSettings.getJsTemplate();
        if (StringUtils.isBlank(template)) {
            return DEFAULT_JS_TEMPLATE;
        }
        return template;

    }

    protected String asVar(String key) {
        return codeBuilder.asVar(key);
    }
}
