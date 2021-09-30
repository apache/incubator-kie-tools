/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.container;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.css.CssValue;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

import java.util.List;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated
public class ContainerView
        implements UberElement<Container>,
                   Container.View,
                   IsElement {

    @Inject
    PlaceManager placeManager;

    @Inject
    @DataField
    Div container;

    @Inject
    @DataField
    Div header;

    @Inject
    @DataField
    Div layout;

    private Container presenter;

    @Inject
    private Event<ContainerResizeEvent> resizeEvent;

    @Override
    public void init(Container presenter) {
        this.presenter = presenter;
        this.setupEvents();
    }

    private void setupEvents() {
        header.setOnclick(e -> {
            e.preventDefault();
            presenter.onSelected();
        });
        header.setOnmouseover(e -> {
            e.preventDefault();
            if (!presenter.isSelected()) {
                addCSSClass(layout, "container-selected");
            }
        });
        header.setOnmouseout(e -> {
            e.preventDefault();
            if (!presenter.isSelected()) {
                removeCSSClass(layout, "container-selected");
            }
        });
    }
    @Override
    public void addRow(UberElement<Row> view) {
        if (!hasCSSClass(layout,
                         "container-canvas")) {
            addCSSClass(layout,
                        "container-canvas");
        }
        removeCSSClass(layout,
                       "container-empty");
        layout.appendChild(view.getElement());
    }

    @Override
    public void clear() {
        removeAllChildren(layout);
        layout.appendChild(header);
    }

    @Override
    public void addEmptyRow(UberElement<EmptyDropRow> emptyDropRow) {
        removeCSSClass(layout,
                       "container-canvas");
        addCSSClass(layout,
                    "container-empty");
        layout.appendChild(emptyDropRow.getElement());
    }

    @Override
    public void pageMode() {
        addCSSClass(layout,
                    "page-container");
    }

    @Override
    public void setSelectEnabled(boolean enabled) {
        header.setTitle(enabled ? CommonConstants.INSTANCE.SelectContainerHint() : "");
    }

    @Override
    public void setSelected(boolean selected) {
        removeCSSClass(layout, "container-selected");
        header.setTitle(CommonConstants.INSTANCE.SelectContainerHint());
        if (selected) {
            addCSSClass(layout, "container-selected");
            header.setTitle(CommonConstants.INSTANCE.UnselectContainerHint());
        }
    }

    @Override
    public void applyCssValues(List<CssValue> cssValues) {
        layout.getStyle().setCssText("");
        cssValues.forEach(cssValue -> {
            String prop = cssValue.getProperty();
            String val = cssValue.getValue();
            layout.getStyle().setProperty(prop, val);
        });
    }
}
