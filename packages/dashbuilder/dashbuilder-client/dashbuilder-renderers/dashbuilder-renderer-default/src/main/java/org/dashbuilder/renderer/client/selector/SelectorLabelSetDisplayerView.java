/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.selector;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectorLabelSetDisplayerView extends AbstractErraiDisplayerView<SelectorLabelSetDisplayer>
        implements SelectorLabelSetDisplayer.View {

    @Inject
    @DataField
    Div containerDiv;

    @Inject
    @DataField
    Span titleSpan;

    @Inject
    @DataField
    Span noDataSpan;

    @Inject
    @DataField
    Div labelSetDiv;

    @Override
    public void init(SelectorLabelSetDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization((Element) containerDiv);
    }

    @Override
    public void showTitle(String title) {
        titleSpan.setTextContent(title);
    }

    @Override
    public void setWidth(int width) {
        containerDiv.getStyle().setProperty("width", width + "px");
    }

    @Override
    public void margins(int top, int bottom, int left, int right) {
        containerDiv.getStyle().setProperty("margin-top", top + "px");
        containerDiv.getStyle().setProperty("margin-bottom", bottom + "px");
        containerDiv.getStyle().setProperty("margin-left", left + "px");
        containerDiv.getStyle().setProperty("margin-right", right + "px");
    }

    @Override
    public void clearItems() {
        DOMUtil.removeAllChildren(labelSetDiv);
        noDataSpan.setTextContent("");
    }

    @Override
    public void addItem(SelectorLabelItem item) {
        HTMLElement element = item.getView().getElement();
        element.getStyle().setProperty("margin", "3px");
        labelSetDiv.appendChild(element);
    }

    @Override
    public void noData() {
        noDataSpan.setTextContent(SelectorConstants.INSTANCE.selectorDisplayer_noDataAvailable());
    }

    @Override
    public String getGroupsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_columnsTitle();
    }
}