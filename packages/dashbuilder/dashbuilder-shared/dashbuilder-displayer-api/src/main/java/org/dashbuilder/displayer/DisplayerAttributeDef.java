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

import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.BUBBLE_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_GRID_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_LEGEND_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.CHART_MARGIN_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.COLUMNS_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.DONUT_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.EXPORT_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.EXTERNAL_COMPONENT_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.FILTER_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.GENERAL_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.HTML_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.MAP_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.METER_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.REFRESH_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.SELECTOR_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.TABLE_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.TABLE_SORT_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.XAXIS_GROUP;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.YAXIS_GROUP;

public class DisplayerAttributeDef {

    public static final DisplayerAttributeDef TYPE = new DisplayerAttributeDef("type");
    public static final DisplayerAttributeDef SUBTYPE = new DisplayerAttributeDef("subtype");
    public static final DisplayerAttributeDef RENDERER = new DisplayerAttributeDef("renderer");
    public static final DisplayerAttributeDef EXTRA_CONFIGURATION = new DisplayerAttributeDef("extraConfiguration");

    public static final DisplayerAttributeDef COLUMN_EXPRESSION = new DisplayerAttributeDef("expression", COLUMNS_GROUP);
    public static final DisplayerAttributeDef COLUMN_PATTERN = new DisplayerAttributeDef("pattern", COLUMNS_GROUP);
    public static final DisplayerAttributeDef COLUMN_EMPTY = new DisplayerAttributeDef("empty", COLUMNS_GROUP);

    public static final DisplayerAttributeDef TITLE = new DisplayerAttributeDef("title", GENERAL_GROUP);
    public static final DisplayerAttributeDef SUB_TITLE = new DisplayerAttributeDef("subtitle", GENERAL_GROUP);
    public static final DisplayerAttributeDef TITLE_VISIBLE = new DisplayerAttributeDef("visible", GENERAL_GROUP);
    public static final DisplayerAttributeDef MODE = new DisplayerAttributeDef("mode", GENERAL_GROUP);
    public static final DisplayerAttributeDef ALLOW_EDIT = new DisplayerAttributeDef("allowEdit", GENERAL_GROUP);

    /**
     * @deprecated Replaced by {@link #EXPORT_TO_CSV}. (Kept for backward compatibility)
     * @since 0.6
     */
    public static final DisplayerAttributeDef ALLOW_EXPORT_CSV = new DisplayerAttributeDef("allow_csv", GENERAL_GROUP);

    /**
     * @deprecated Replaced by {@link #EXPORT_TO_XLS}. (Kept for backward compatibility)
     * @since 0.6
     */
    public static final DisplayerAttributeDef ALLOW_EXPORT_EXCEL = new DisplayerAttributeDef("allow_excel", GENERAL_GROUP);

    public static final DisplayerAttributeDef EXPORT_TO_CSV = new DisplayerAttributeDef("export_csv", EXPORT_GROUP);
    public static final DisplayerAttributeDef EXPORT_TO_XLS = new DisplayerAttributeDef("export_xls", EXPORT_GROUP);
    public static final DisplayerAttributeDef EXPORT_TO_PNG = new DisplayerAttributeDef("png", EXPORT_GROUP);

    public static final DisplayerAttributeDef REFRESH_STALE_DATA = new DisplayerAttributeDef("staleData", REFRESH_GROUP);
    public static final DisplayerAttributeDef REFRESH_INTERVAL = new DisplayerAttributeDef("interval", REFRESH_GROUP);

    public static final DisplayerAttributeDef FILTER_ENABLED = new DisplayerAttributeDef("enabled", FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_SELFAPPLY_ENABLED = new DisplayerAttributeDef("selfapply", FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_NOTIFICATION_ENABLED = new DisplayerAttributeDef("notification", FILTER_GROUP);
    public static final DisplayerAttributeDef FILTER_LISTENING_ENABLED = new DisplayerAttributeDef("listening", FILTER_GROUP);

    public static final DisplayerAttributeDef SELECTOR_WIDTH = new DisplayerAttributeDef("width", SELECTOR_GROUP);
    public static final DisplayerAttributeDef SELECTOR_MULTIPLE = new DisplayerAttributeDef("multiple", SELECTOR_GROUP);
    public static final DisplayerAttributeDef SELECTOR_SHOW_INPUTS = new DisplayerAttributeDef("inputs_show", SELECTOR_GROUP);

    public static final DisplayerAttributeDef CHART_WIDTH = new DisplayerAttributeDef("width", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_HEIGHT = new DisplayerAttributeDef("height", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_RESIZABLE = new DisplayerAttributeDef("resizable", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MAX_WIDTH = new DisplayerAttributeDef("maxWidth", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MAX_HEIGHT = new DisplayerAttributeDef("maxHeight", CHART_GROUP);
    
    public static final DisplayerAttributeDef CHART_BGCOLOR = new DisplayerAttributeDef("bgColor", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_3D = new DisplayerAttributeDef("3d", CHART_GROUP);
    public static final DisplayerAttributeDef ZOOM_ENABLED = new DisplayerAttributeDef("zoom", CHART_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_TOP = new DisplayerAttributeDef("top", CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_BOTTOM = new DisplayerAttributeDef("bottom", CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_LEFT = new DisplayerAttributeDef("left", CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_MARGIN_RIGHT = new DisplayerAttributeDef("right", CHART_MARGIN_GROUP);
    public static final DisplayerAttributeDef CHART_SHOWLEGEND = new DisplayerAttributeDef("show", CHART_LEGEND_GROUP);
    public static final DisplayerAttributeDef CHART_LEGENDPOSITION = new DisplayerAttributeDef("position", CHART_LEGEND_GROUP);
    public static final DisplayerAttributeDef CHART_GRIDX = new DisplayerAttributeDef("x", CHART_GRID_GROUP);
    public static final DisplayerAttributeDef CHART_GRIDY = new DisplayerAttributeDef("y", CHART_GRID_GROUP);

    public static final DisplayerAttributeDef TABLE_PAGESIZE = new DisplayerAttributeDef("pageSize", TABLE_GROUP);
    public static final DisplayerAttributeDef TABLE_WIDTH = new DisplayerAttributeDef("width", TABLE_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTENABLED = new DisplayerAttributeDef("enabled", TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTCOLUMNID = new DisplayerAttributeDef("columnId", TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_SORTORDER = new DisplayerAttributeDef("order", TABLE_SORT_GROUP);
    public static final DisplayerAttributeDef TABLE_COLUMN_PICKER_ENABLED = new DisplayerAttributeDef("show_column_picker", TABLE_GROUP);

    public static final DisplayerAttributeDef XAXIS_SHOWLABELS = new DisplayerAttributeDef("labels_show", XAXIS_GROUP);
    public static final DisplayerAttributeDef XAXIS_TITLE = new DisplayerAttributeDef("title", XAXIS_GROUP);
    public static final DisplayerAttributeDef XAXIS_LABELSANGLE = new DisplayerAttributeDef("labels_angle", XAXIS_GROUP);
    public static final DisplayerAttributeDef YAXIS_SHOWLABELS = new DisplayerAttributeDef("labels_show", YAXIS_GROUP);
    public static final DisplayerAttributeDef YAXIS_TITLE = new DisplayerAttributeDef("title", YAXIS_GROUP);

    public static final DisplayerAttributeDef METER_START = new DisplayerAttributeDef("start", METER_GROUP);
    public static final DisplayerAttributeDef METER_WARNING = new DisplayerAttributeDef("warning", METER_GROUP);
    public static final DisplayerAttributeDef METER_CRITICAL = new DisplayerAttributeDef("critical", METER_GROUP);
    public static final DisplayerAttributeDef METER_END = new DisplayerAttributeDef("end", METER_GROUP);

    public static final DisplayerAttributeDef DONUT_HOLE_TITLE = new DisplayerAttributeDef("hole_title", DONUT_GROUP);

    public static final DisplayerAttributeDef HTML_TEMPLATE = new DisplayerAttributeDef("html", HTML_GROUP);
    public static final DisplayerAttributeDef JS_TEMPLATE = new DisplayerAttributeDef("javascript", HTML_GROUP);
    
    public static final DisplayerAttributeDef MAP_COLOR_SCHEME = new DisplayerAttributeDef("color_scheme", MAP_GROUP);
    
    
    public static final DisplayerAttributeDef BUBBLE_MIN_RADIUS = new DisplayerAttributeDef("minRadius", BUBBLE_GROUP);
    public static final DisplayerAttributeDef BUBBLE_MAX_RADIUS = new DisplayerAttributeDef("maxRadius", BUBBLE_GROUP);
    public static final DisplayerAttributeDef BUBBLE_COLOR = new DisplayerAttributeDef("color", BUBBLE_GROUP);

    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_ID_DEPRECATED = new DisplayerAttributeDef("external_component_id");
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_ID = new DisplayerAttributeDef("component");
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_PARTITION = new DisplayerAttributeDef("external_component_partition");
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_WIDTH_DEPRECATED = new DisplayerAttributeDef("external_component_width", EXTERNAL_COMPONENT_GROUP);
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_HEIGHT_DEPRECATED = new DisplayerAttributeDef("external_component_height", EXTERNAL_COMPONENT_GROUP);
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_WIDTH = new DisplayerAttributeDef("width", EXTERNAL_COMPONENT_GROUP);
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_HEIGHT = new DisplayerAttributeDef("height", EXTERNAL_COMPONENT_GROUP);
    public static final DisplayerAttributeDef EXTERNAL_COMPONENT_BASE_URL = new DisplayerAttributeDef("baseUrl", EXTERNAL_COMPONENT_GROUP);

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
    
    public String getId() {
        return id;
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
