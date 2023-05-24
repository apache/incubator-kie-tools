/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.echarts.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Chart;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Option;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;
import org.dashbuilder.renderer.echarts.client.resources.i18n.EChartsDisplayerConstants;
import org.gwtbootstrap3.client.ui.Label;

@Dependent
public class EChartsDisplayerView<P extends EChartsAbstractDisplayer<?>>
                                 extends AbstractGwtDisplayerView<P>
                                 implements EChartsAbstractDisplayer.View<P> {

    protected Panel displayerPanel = GWT.create(FlowPanel.class);

    private Chart chart;

    ChartBootstrapParams bootstrapParams;

    @Inject
    EChartsTypeFactory echartsFactory;

    @Inject
    EChartsResizeHandlerRegister eChartsResizeHandlerRegister;

    @Override
    public void init(P presenter) {
        super.setPresenter(presenter);
        super.setVisualization(displayerPanel);
    }

    @Override
    public void noData() {
        FlowPanel noDataPanel = GWT.create(FlowPanel.class);
        Label lblNoData = GWT.create(Label.class);
        lblNoData.setText(EChartsDisplayerConstants.INSTANCE.common_noData());
        noDataPanel.add(lblNoData);

        disposeChart();
        chart = null;

        displayerPanel.clear();
        displayerPanel.add(noDataPanel);
    }

    @Override
    public void applyOption(Option option) {
        if (chart == null) {
            initChart();
        }
        Scheduler.get().scheduleDeferred(() -> {
            chart.setOption(option);
            chart.resize();
        });
        // timeout reinforcement, some parent may not be completed resized
        DomGlobal.setTimeout(e -> chart.resize(), 100);
    }

    @Override
    public void configureChart(ChartBootstrapParams params) {
        if (this.bootstrapParams == null || !this.bootstrapParams.equals(params)) {
            this.bootstrapParams = params;
            initChart();
        }
    }

    private void initChart() {
        var initParams = echartsFactory.newChartInitParams();

        if (!bootstrapParams.isResizable()) {
            initParams.setWidth(bootstrapParams.getWidth());
            initParams.setHeight(bootstrapParams.getHeight());
        } else {
            displayerPanel.getElement().getStyle().setWidth(100, Unit.PCT);
            displayerPanel.getElement().getStyle().setHeight(bootstrapParams.getHeight(), Unit.PX);
        }

        initParams.setRenderer(bootstrapParams.getRenderer().name());

        disposeChart();

        chart = ECharts.Builder
                .get()
                .init(Js.cast(displayerPanel.getElement()),
                        bootstrapParams.getMode().name().toLowerCase(),
                        initParams);
        if (bootstrapParams.isResizable()) {
            eChartsResizeHandlerRegister.add(chart);
        }
    }

    private void disposeChart() {
        if (chart != null) {
            chart.dispose();
        }
    }

    @Override
    public void close() {
        if (chart != null) {
            eChartsResizeHandlerRegister.remove(chart);
        }
    }
}
