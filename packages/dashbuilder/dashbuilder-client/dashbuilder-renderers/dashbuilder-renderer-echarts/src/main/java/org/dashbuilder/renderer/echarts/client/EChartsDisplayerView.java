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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.CSSProperties.HeightUnionType;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.displayer.client.AbstractDisplayerView;
import org.dashbuilder.patternfly.label.Label;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Chart;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Option;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;
import org.dashbuilder.renderer.echarts.client.resources.i18n.EChartsDisplayerConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.uberfire.ext.layout.editor.api.event.LayoutTemplateDisplayed;

@Dependent
public class EChartsDisplayerView<P extends EChartsAbstractDisplayer<?>>
                                 extends AbstractDisplayerView<P>
                                 implements EChartsAbstractDisplayer.View<P> {

    private static final String DARK_MODE_BG_COLOR = "rgb(27, 29, 33)";

    protected HTMLDivElement displayerPanel;

    private Chart chart;

    ChartBootstrapParams bootstrapParams;

    @Inject
    EChartsTypeFactory echartsFactory;

    @Inject
    Elemental2DomUtil domUtil;

    @Inject
    EChartsResizeHandlerRegister eChartsResizeHandlerRegister;

    @Inject
    Label lblNoData;

    @Override
    public void init(P presenter) {
        displayerPanel = (HTMLDivElement) DomGlobal.document.createElement("div");
        super.init(presenter);
        super.setVisualization(displayerPanel);
    }

    @Override
    public void noData() {
        lblNoData.setText(EChartsDisplayerConstants.INSTANCE.common_noData());

        disposeChart();
        chart = null;

        domUtil.removeAllElementChildren(displayerPanel);
        displayerPanel.appendChild(lblNoData.getElement());
    }

    @Override
    public void applyOption(Option option) {
        if (chart == null) {
            initChart();
        }
        Scheduler.get().scheduleDeferred(() -> {
            // Needs to differ the default dark theme background to match PF5
            // This is a workaround since a custom theme is failing 
            // possibly related https://github.com/chartjs/Chart.js/issues/7761
            if (bootstrapParams.getMode() == Mode.DARK && option.getBackgroundColor() == null) {
                option.setBackgroundColor(DARK_MODE_BG_COLOR);
            }
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
            displayerPanel.style.width = WidthUnionType.of("100%");
            displayerPanel.style.height = HeightUnionType.of(bootstrapParams.getHeight() + "px");
        }

        initParams.setRenderer(bootstrapParams.getRenderer().name());

        disposeChart();

        chart = ECharts.Builder
                .get()
                .init(displayerPanel,
                        bootstrapParams.getMode().name().toLowerCase(),
                        initParams);
        if (bootstrapParams.isResizable()) {
            eChartsResizeHandlerRegister.add(chart);
        }
    }

    @Override
    public void close() {
        disposeChart();
        if (chart != null) {
            eChartsResizeHandlerRegister.remove(chart);
        }
    }

    public void listenToDisplayedLayout(@Observes LayoutTemplateDisplayed layoutTemplateDisplayedEvent) {
        if (chart != null && bootstrapParams != null && bootstrapParams.isResizable()) {
            chart.resize();
        }
    }

    private void disposeChart() {
        if (chart != null) {
            chart.dispose();
        }
    }

}
