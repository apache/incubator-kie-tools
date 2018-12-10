package org.dashbuilder.renderer.c3.client;

import java.util.List;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisLabel;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick;

public abstract class C3XYDisplayer<V extends C3Displayer.View> extends C3Displayer {
    
    private static final String DEFAULT_LABEL_POS = "outer-center";

    public C3XYDisplayer(FilterLabelSet filterLabelSet, C3JsTypesFactory builder) {
        super(filterLabelSet, builder);
    }

    @Override
    protected C3ChartConf buildConfiguration() {
         C3ChartConf conf = super.buildConfiguration();
         applyPropertiesToAxes(conf.getAxis());
         return conf;
    }
    
    protected C3Tick createTickY() {
        return factory.createC3Tick(f -> {
            List<DataColumn> columns = dataSet.getColumns();
            if (columns.size() > 1) {
                DataColumn dataColumn = columns.get(1);
                f = super.formatValue(f, dataColumn);
            }
            return f;
        });
    }
    
    private void applyPropertiesToAxes(C3AxisInfo axis) {
        axis.getX().getTick().setRotate(displayerSettings.getXAxisLabelsAngle());
        if (displayerSettings.isXAxisShowLabels()) {
            C3AxisLabel xLabel = factory.createC3Label(displayerSettings.getXAxisTitle(), 
                                                       DEFAULT_LABEL_POS);
            axis.getX().setLabel(xLabel);
        }
        if (displayerSettings.isYAxisShowLabels()) {
            C3AxisLabel yLabel = factory.createC3Label(displayerSettings.getYAxisTitle(), 
                                                       DEFAULT_LABEL_POS);
            axis.getY().setLabel(yLabel);
        }
    }

}
