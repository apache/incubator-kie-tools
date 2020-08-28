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
package org.dashbuilder.displayer;

public class DisplayerAttributeDef {

    public static final DisplayerAttributeDef TYPE = new DisplayerAttributeDef("type");
    public static final DisplayerAttributeDef SUBTYPE = new DisplayerAttributeDef("subtype");
    public static final DisplayerAttributeDef RENDERER = new DisplayerAttributeDef("renderer");

    public static final DisplayerAttributeDef COLUMN_EXPRESSION = new DisplayerAttributeDef("expression", DisplayerAttributeGroupDef.COLUMNS_GROUP);
    public static final DisplayerAttributeDef COLUMN_PATTERN = new DisplayerAttributeDef("pattern", DisplayerAttributeGroupDef.COLUMNS_GROUP);
    public static final DisplayerAttributeDef COLUMN_EMPTY = new DisplayerAttributeDef("empty", DisplayerAttributeGroupDef.COLUMNS_GROUP);

    public static final DisplayerAttributeDef TITLE = new DisplayerAttributeDef("title", DisplayerAttributeGroupDef.GENERAL_GROUP);
    public static final DisplayerAttributeDef TITLE_VISIBLE = new DisplayerAttributeDef("visible", DisplayerAttributeGroupDef.GENERAL_GROUP);

    /**
     * @deprecated Replaced by {@link #EXPORT_TO_CSV}. (Kept for backward compatibility)
     * @since 0.6
     */
    public static final DisplayerAttributeDef ALLOW_EXPORT_CSV = new DisplayerAttributeDef("allow_csv", DisplayerAttributeGroupDef.GENERAL_GROUP);

    /**
     * @deprecated Replaced by {@link #EXPORT_TO_XLS}. (Kept for backward compatibility)
     * @since 0.6
     */
    public static final DisplayerAttributeDef ALLOW_EXPORT_EXCEL = new DisplayerAttributeDef("allow_excel", DisplayerAttributeGroupDef.GENERAL_GROUP);

    public static final DisplayerAttributeDef EXPORT_TO_CSV = new DisplayerAttributeDef("export_csv", DisplayerAttributeGroupDef.EXPORT_GROUP);
    public static final DisplayerAttributeDef EXPORT_TO_XLS = new DisplayerAttributeDef("export_xls", DisplayerAttributeGroupDef.EXPORT_GROUP);

    public static final DisplayerAttributeDef REFRESH_STALE_DATA = new DisplayerAttributeDef("staleData", DisplayerAttributeGroupDef.REFRESH_GROUP);
    public static final DisplayerAttributeDef REFRESH_INTERVAL = new DisplayerAttributeDef("interval", DisplayerAttributeGroupDef.REFRESH_GROUP);

    public static final DisplayerAttributeDef FILTER_ENABLED = new DisplayerAttributeDef("enabled", DisplayerAttributeGroupDef.FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_SELFAPPLY_ENABLED = new DisplayerAttributeDef("selfapply", DisplayerAttributeGroupDef.FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_NOTIFICATION_ENABLED = new DisplayerAttributeDef("notification", DisplayerAttributeGroupDef.FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_LISTENING_ENABLED = new DisplayerAttributeDef("listening", DisplayerAttributeGroupDef.FILTER_GROUP);

    public static final DisplayerAttributeDef SELECTOR_WIDTH = new DisplayerAttributeDef("width", DisplayerAttributeGroupDef.SELECTOR_GROUP);
    public static final DisplayerAttributeDef SELECTOR_MULTIPLE = new DisplayerAttributeDef("multiple", DisplayerAttributeGroupDef.SELECTOR_GROUP);
    public static final DisplayerAttributeDef SELECTOR_SHOW_INPUTS = new DisplayerAttributeDef("inputs_show", DisplayerAttributeGroupDef.SELECTOR_GROUP);

    public static final DisplayerAttributeDef CHART_WIDTH = new DisplayerAttributeDef("width", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_HEIGHT = new DisplayerAttributeDef("height", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_RESIZABLE = new DisplayerAttributeDef("resizable", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MAX_WIDTH = new DisplayerAttributeDef("maxWidth", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MAX_HEIGHT = new DisplayerAttributeDef("maxHeight", DisplayerAttributeGroupDef.CHART_GROUP);
    
    public static final DisplayerAttributeDef CHART_BGCOLOR = new DisplayerAttributeDef("bgColor", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_3D = new DisplayerAttributeDef("3d", DisplayerAttributeGroupDef.CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_TOP = new DisplayerAttributeDef("top", DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_BOTTOM = new DisplayerAttributeDef("bottom", DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_LEFT = new DisplayerAttributeDef("left", DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_RIGHT = new DisplayerAttributeDef("right", DisplayerAttributeGroupDef.CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_SHOWLEGEND = new DisplayerAttributeDef("show", DisplayerAttributeGroupDef.CHART_LEGEND_GROUP);
    public static final DisplayerAttributeDef CHART_LEGENDPOSITION = new DisplayerAttributeDef("position", DisplayerAttributeGroupDef.CHART_LEGEND_GROUP);

    public static final DisplayerAttributeDef TABLE_PAGESIZE = new DisplayerAttributeDef("pageSize", DisplayerAttributeGroupDef.TABLE_GROUP);
    public static final DisplayerAttributeDef TABLE_WIDTH = new DisplayerAttributeDef("width", DisplayerAttributeGroupDef.TABLE_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTENABLED = new DisplayerAttributeDef("enabled", DisplayerAttributeGroupDef.TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTCOLUMNID = new DisplayerAttributeDef("columnId", DisplayerAttributeGroupDef.TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTORDER = new DisplayerAttributeDef("order", DisplayerAttributeGroupDef.TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_COLUMN_PICKER_ENABLED = new DisplayerAttributeDef("show_column_picker", DisplayerAttributeGroupDef.TABLE_GROUP);

    public static final DisplayerAttributeDef XAXIS_SHOWLABELS = new DisplayerAttributeDef("labels_show", DisplayerAttributeGroupDef.XAXIS_GROUP);
    public static final DisplayerAttributeDef XAXIS_TITLE = new DisplayerAttributeDef("title", DisplayerAttributeGroupDef.XAXIS_GROUP);
    public static final DisplayerAttributeDef XAXIS_LABELSANGLE = new DisplayerAttributeDef("labels_angle", DisplayerAttributeGroupDef.XAXIS_GROUP);
    public static final DisplayerAttributeDef YAXIS_SHOWLABELS = new DisplayerAttributeDef("labels_show", DisplayerAttributeGroupDef.YAXIS_GROUP);
    public static final DisplayerAttributeDef YAXIS_TITLE = new DisplayerAttributeDef("title", DisplayerAttributeGroupDef.YAXIS_GROUP);

    public static final DisplayerAttributeDef METER_START = new DisplayerAttributeDef("start", DisplayerAttributeGroupDef.METER_GROUP);
    public static final DisplayerAttributeDef METER_WARNING = new DisplayerAttributeDef("warning", DisplayerAttributeGroupDef.METER_GROUP);
    public static final DisplayerAttributeDef METER_CRITICAL = new DisplayerAttributeDef("critical", DisplayerAttributeGroupDef.METER_GROUP);
    public static final DisplayerAttributeDef METER_END = new DisplayerAttributeDef("end", DisplayerAttributeGroupDef.METER_GROUP);

    public static final DisplayerAttributeDef DONUT_HOLE_TITLE = new DisplayerAttributeDef("hole_title", DisplayerAttributeGroupDef.DONUT_GROUP);

    public static final DisplayerAttributeDef HTML_TEMPLATE = new DisplayerAttributeDef("html", DisplayerAttributeGroupDef.HTML_GROUP);
    public static final DisplayerAttributeDef JS_TEMPLATE = new DisplayerAttributeDef("javascript", DisplayerAttributeGroupDef.HTML_GROUP);
    
    public static final DisplayerAttributeDef MAP_COLOR_SCHEME = new DisplayerAttributeDef("color_scheme", DisplayerAttributeGroupDef.MAP_GROUP);

    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_ID = new DisplayerAttributeDef("external_component_id");
    
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_PARTITION = new DisplayerAttributeDef("external_component_partition");

    
    protected String id;
    protected DisplayerAttributeGroupDef parent;

    public DisplayerAttributeDef() {
    }

    public DisplayerAttributeDef( String id ) {
        this( id, null );
    }

    public DisplayerAttributeDef( String id, DisplayerAttributeGroupDef parent ) {
        this.id = id;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public String getFullId() {
        return parent != null ? parent.getFullId() + "." + id : id;
    }

    public DisplayerAttributeDef getParent() {
        return parent;
    }

    public void setParent(DisplayerAttributeGroupDef parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( !( obj.getClass().getName().equalsIgnoreCase(this.getClass().getName()) ) ) {
            return false;
        }
        DisplayerAttributeDef that = (DisplayerAttributeDef) obj;
        return that.getFullId().equalsIgnoreCase( this.getFullId() );
    }

    @Override
    public int hashCode() {
        int result = 23;
        result = 31 * result + getFullId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getFullId();
    }
}
