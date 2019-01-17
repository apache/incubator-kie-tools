package org.dashbuilder.renderer.c3.client.charts.area;

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
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf.RenderedCallback;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;


@Dependent
public class C3AreaChartDisplayer extends C3XYDisplayer<C3AreaChartDisplayer.View> {
    
    
    public interface View extends C3Displayer.View<C3AreaChartDisplayer> {
        
        public void fixAreaOpacity();
    }
    
    private View view;
    
    RenderedCallback fixAreaOpacityCallback = () -> getView().fixAreaOpacity();
    
    @Inject
    public C3AreaChartDisplayer(View view, FilterLabelSet filterLabelSet, C3JsTypesFactory factory) {
        super(filterLabelSet, factory);
        this.view = view;
        this.view.init(this);
    }
    
    public C3AreaChartDisplayer stacked() {
        this.setStacked(true);
        return this;
    }
    
    @Override
    protected C3ChartConf buildConfiguration() {
        C3ChartConf conf = super.buildConfiguration();
        conf.setOnrendered(fixAreaOpacityCallback);
        return conf;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxGroups(1)
                .setMinColumns(2)
                .setMaxColumns(10)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType(ColumnType.NUMBER)
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
    public View getView() {
        return view;
    }
    
}