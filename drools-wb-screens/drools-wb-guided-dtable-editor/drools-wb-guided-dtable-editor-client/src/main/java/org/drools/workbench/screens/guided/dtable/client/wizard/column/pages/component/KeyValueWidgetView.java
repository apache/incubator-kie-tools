/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.component;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class KeyValueWidgetView implements IsElement,
                                           KeyValueWidget.View {

    @DataField("keyTextLabel")
    private final HTMLLabelElement keyTextLabel;

    @DataField("gdtColumnDefaultvalueWidget")
    private final HTMLDivElement gdtColumnDefaultvalueWidget;

    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    public KeyValueWidgetView(final HTMLLabelElement keyTextLabel,
                              final HTMLDivElement gdtColumnDefaultvalueWidget,
                              final Elemental2DomUtil elemental2DomUtil) {
        this.keyTextLabel = keyTextLabel;
        this.gdtColumnDefaultvalueWidget = gdtColumnDefaultvalueWidget;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @Override
    public void setKey(final String key) {
        keyTextLabel.textContent = key;
    }

    @Override
    public void setValueWidget(final IsWidget value) {

        Widget widget = value.asWidget();

        elemental2DomUtil.removeAllElementChildren(gdtColumnDefaultvalueWidget);
        elemental2DomUtil.appendWidgetToElement(gdtColumnDefaultvalueWidget, widget);
    }

    @Override
    public void init(final KeyValueWidget presenter) {
        // Do nothing
    }
}
