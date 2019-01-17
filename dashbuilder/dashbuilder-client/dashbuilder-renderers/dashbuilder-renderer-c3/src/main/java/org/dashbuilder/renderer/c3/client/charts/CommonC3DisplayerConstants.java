package org.dashbuilder.renderer.c3.client.charts;

import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;

/**
 * A common class for displayer constraints supported by a C3 displayer. 
 */
public class CommonC3DisplayerConstants {
    
    private DataSetLookupConstraints lookupConstraints;

    public CommonC3DisplayerConstants(DataSetLookupConstraints lookupConstraints) {
        this.lookupConstraints = lookupConstraints;
    }
    
    public DisplayerConstraints create() {
        return new DisplayerConstraints(lookupConstraints).supportsAttribute(DisplayerAttributeDef.TYPE)
                                                          .supportsAttribute(DisplayerAttributeDef.RENDERER)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_RESIZABLE)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_WIDTH)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_HEIGHT)
                                                          .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                                                          .supportsAttribute(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP);
                                                            
    }

}
