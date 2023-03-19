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

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.dashbuilder.renderer.client.resources.i18n.MetricConstants;

@Dependent
public class MetricView extends AbstractGwtDisplayerView<MetricDisplayer> implements MetricDisplayer.View {

    FlowPanel container = new FlowPanel();
    HTMLPanel htmlPanel = null;
    String uniqueId = Document.get().createUniqueId();

    @Override
    public void init(MetricDisplayer presenter) {
        container.getElement().setAttribute("id", uniqueId);
        super.setPresenter(presenter);
        super.setVisualization(container);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void clear() {
        super.clear();
        htmlPanel = null;
        container.clear();
        DefaultRenderer.closeDisplayer(uniqueId);
    }

    @Override
    public void setHtml(String html) {
        htmlPanel = new HTMLPanel(html);
        container.clear();
        container.add(htmlPanel);
    }

    @Override
    public String getNoDataString() {
        return MetricConstants.INSTANCE.metricDisplayer_noDataAvailable();
    }

    @Override
    public void eval(String js) {
        Scheduler.get().scheduleFixedDelay(() -> {
            Element el = Document.get().getElementById(uniqueId);
            if (el != null) {
                ScriptInjector.fromString(js).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
                return false;
            }
            return true;
        }, 100);
    }

    @Override
    public String getColumnsTitle() {
        return MetricConstants.INSTANCE.metricDisplayer_columnsTitle();
    }
}
