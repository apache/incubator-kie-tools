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
import com.google.gwt.event.dom.client.ClickEvent;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectorDropDownDisplayerView extends AbstractErraiDisplayerView<SelectorDropDownDisplayer>
        implements SelectorDropDownDisplayer.View {

    @Inject
    @DataField
    Div containerDiv;

    @Inject
    @DataField
    Span titleSpan;

    @Inject
    @DataField
    Div dropDownDiv;

    @Inject
    @DataField
    Button dropDownButton;

    @Inject
    @DataField
    Div dropDownText;

    @Inject
    @DataField
    UnorderedList resetMenu;

    @Inject
    @DataField
    UnorderedList dropDownMenu;

    @Inject
    @DataField
    Anchor resetAnchor;

    @Override
    public void init(SelectorDropDownDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization((Element) containerDiv);
    }

    @Override
    public void showTitle(String title) {
        titleSpan.setTextContent(title);
    }

    @Override
    public void margins(int top, int bottom, int left, int right) {
        containerDiv.getStyle().setProperty("margin-top", top + "px");
        containerDiv.getStyle().setProperty("margin-bottom", bottom + "px");
        containerDiv.getStyle().setProperty("margin-left", left + "px");
        containerDiv.getStyle().setProperty("margin-right", right + "px");
    }

    @Override
    public void setWidth(int width) {
        if (width > 0) {
            dropDownButton.getStyle().setProperty("width", width + "px");
            dropDownMenu.getStyle().setProperty("width", width + "px");
            resetMenu.getStyle().setProperty("width", width + "px");
            dropDownText.getStyle().setProperty("max-width", (width - 30) + "px");
        } else {
            dropDownButton.getStyle().removeProperty("width");
            dropDownMenu.getStyle().removeProperty("width");
            resetMenu.getStyle().removeProperty("width");
            dropDownText.getStyle().removeProperty("max-width");
        }
    }

    @Override
    public void showSelectHint(String column, boolean multiple) {
        String hint = "- " + SelectorConstants.INSTANCE.selectorDisplayer_select() + " " + column + " - ";
        dropDownText.setTextContent(hint);
        resetMenu.getStyle().setProperty("display", "none");
    }

    @Override
    public void showResetHint(String column, boolean multiple) {
        String resetAction = multiple ? SelectorConstants.INSTANCE.selectorDisplayer_clearAll() : SelectorConstants.INSTANCE.selectorDisplayer_reset();
        resetAnchor.setTextContent(resetAction);
        resetMenu.getStyle().removeProperty("display");
        int n = dropDownMenu.getChildNodes().getLength() * 25;
        resetMenu.getStyle().setProperty("margin-top", (n > 250 ? 250 : n) + "px");
    }

    @Override
    public void showCurrentSelection(String text, String hint) {
        dropDownText.setTextContent(text);
        dropDownButton.setTitle(hint);
    }

    @Override
    public void clearItems() {
        DOMUtil.removeAllChildren(dropDownMenu);
    }

    @Override
    public void addItem(SelectorDropDownItem item) {
        dropDownMenu.appendChild(item.getView().getElement());
    }

    @Override
    public String getGroupsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_columnsTitle();
    }

    @EventHandler("resetAnchor")
    private void onResetClicked(ClickEvent event) {
        presenter.onResetSelections();
    }
}