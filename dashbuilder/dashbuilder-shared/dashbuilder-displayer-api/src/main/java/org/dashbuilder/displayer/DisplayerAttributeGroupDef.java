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

import java.util.HashSet;
import java.util.Set;

public class DisplayerAttributeGroupDef extends DisplayerAttributeDef {

    // ROOT-GROUPS
    public static final DisplayerAttributeGroupDef GENERAL_GROUP = new DisplayerAttributeGroupDef( "general" );

    public static final DisplayerAttributeGroupDef COLUMNS_GROUP = new DisplayerAttributeGroupDef( "columns" );

    public static final DisplayerAttributeGroupDef REFRESH_GROUP = new DisplayerAttributeGroupDef( "refresh" );

    public static final DisplayerAttributeGroupDef FILTER_GROUP = new DisplayerAttributeGroupDef( "filter" );

    public static final DisplayerAttributeGroupDef SELECTOR_GROUP = new DisplayerAttributeGroupDef( "selector" );

    public static final DisplayerAttributeGroupDef CHART_GROUP = new DisplayerAttributeGroupDef( "chart" );

    public static final DisplayerAttributeGroupDef TABLE_GROUP = new DisplayerAttributeGroupDef( "table" );

    public static final DisplayerAttributeGroupDef AXIS_GROUP = new DisplayerAttributeGroupDef( "axis" );

    public static final DisplayerAttributeGroupDef METER_GROUP = new DisplayerAttributeGroupDef( "meter" );

    public static final DisplayerAttributeGroupDef DONUT_GROUP = new DisplayerAttributeGroupDef( "donut" );

    public static final DisplayerAttributeGroupDef HTML_GROUP = new DisplayerAttributeGroupDef( "html" );

    public static final DisplayerAttributeGroupDef EXPORT_GROUP = new DisplayerAttributeGroupDef( "export" );
    
    public static final DisplayerAttributeGroupDef MAP_GROUP = new DisplayerAttributeGroupDef( "map" );


    // SUBGROUPS
    public static final DisplayerAttributeGroupDef CHART_MARGIN_GROUP =
            new DisplayerAttributeGroupDef( "margin", DisplayerAttributeGroupDef.CHART_GROUP);

    public static final DisplayerAttributeGroupDef CHART_LEGEND_GROUP =
            new DisplayerAttributeGroupDef( "legend", DisplayerAttributeGroupDef.CHART_GROUP );

    public static final DisplayerAttributeGroupDef TABLE_SORT_GROUP =
            new DisplayerAttributeGroupDef( "sort", DisplayerAttributeGroupDef.TABLE_GROUP );

    public static final DisplayerAttributeGroupDef XAXIS_GROUP =
            new DisplayerAttributeGroupDef( "x", DisplayerAttributeGroupDef.AXIS_GROUP );

    public static final DisplayerAttributeGroupDef YAXIS_GROUP =
            new DisplayerAttributeGroupDef( "y", DisplayerAttributeGroupDef.AXIS_GROUP );

    private Set<DisplayerAttributeDef> children = new HashSet<DisplayerAttributeDef>();

    public DisplayerAttributeGroupDef() {
    }

    public DisplayerAttributeGroupDef( String id ) {
        super( id );
    }

    public DisplayerAttributeGroupDef( String id, DisplayerAttributeGroupDef parent ) {
        super( id, parent );
    }

    public Set<DisplayerAttributeDef> getChildren() {
        return children;
    }

    public DisplayerAttributeGroupDef addChild(DisplayerAttributeDef member) {
        children.add(member);
        member.setParent(this);
        return this;
    }
}
