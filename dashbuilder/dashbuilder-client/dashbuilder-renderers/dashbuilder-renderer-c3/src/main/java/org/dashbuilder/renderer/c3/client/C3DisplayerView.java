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
package org.dashbuilder.renderer.c3.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.MutationObserver;
import elemental2.dom.MutationObserverInit;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.dashbuilder.renderer.c3.client.jsbinding.C3;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Chart;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.resources.i18n.C3DisplayerConstants;
import org.dashbuilder.renderer.c3.mutationobserver.MutationObserverFactory;

public abstract class C3DisplayerView<P extends C3Displayer>
        extends C3AbstractDisplayerView<P>
        implements C3Displayer.View<P> {

    protected C3Chart chart;

    @Override
    public void init(P presenter) {
        super.init(presenter);
    }

    @Override
    public void updateChart(C3ChartConf conf) {
        displayerPanel.clear();
        conf.setBindto(displayerPanel.getElement());
        chart = C3.generate(conf);
    }

    @Override
    public String getGroupsTitle() {
        return C3DisplayerConstants.INSTANCE.common_Categories();
    }

    @Override
    public String getColumnsTitle() {
        return C3DisplayerConstants.INSTANCE.common_Series();
    }

    @Override
    public void setBackgroundColor(String color) {
        chart.getElement().getElementsByTagName("svg")
                .getItem(0).getStyle()
                .setBackgroundColor(color);
    }

    public void setResizable(int maxWidth, int maxHeight) {
        displayerPanel.setWidth("100%");
        displayerPanel.getElement().getStyle().setProperty("maxWidth", maxWidth + "px");
        displayerPanel.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
        registerMutationObserver();
    }

    private void registerMutationObserver() {
        MutationObserver observer = new MutationObserver((records, obs) -> {
            Node elementalNode = Js.cast(displayerPanel.getElement());
            if (DomGlobal.document.body.contains((elementalNode))) {
                if (chart != null) {
                    chart.flush();
                }
                obs.disconnect();
            }
            return null;
        });
        MutationObserverInit options = new MutationObserverFactory().mutationObserverInit();
        options.setChildList(true);
        observer.observe(DomGlobal.document.body, options);
    }
}