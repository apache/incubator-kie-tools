/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.dropdown;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
@Templated
public class LiveSearchDropDownView extends Composite
        implements LiveSearchDropDown.View {

    @Inject
    @DataField
    Div mainPanel;

    @Inject
    @DataField
    Div dropDownPanel;

    @Inject
    @DataField
    Button dropDownButton;

    @Inject
    @DataField
    Span dropDownText;

    @Inject
    @DataField
    Div searchPanel;

    @Inject
    @DataField
    Input searchInput;

    @Inject
    @DataField
    UnorderedList dropDownMenu;

    @Inject
    @DataField
    Div spinnerPanel;

    @Inject
    @DataField
    Span spinnerText;

    LiveSearchDropDown presenter;

    @Override
    public void init(LiveSearchDropDown presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        dropDownMenu.getStyle().setProperty("max-height",
                                            maxHeight + "px");
    }

    @Override
    public void setWidth(int minWidth) {
        dropDownButton.getStyle().setProperty("width",
                                              minWidth + "px");
        dropDownPanel.getStyle().setProperty("width",
                                             minWidth + "px");
    }

    @Override
    public void setSearchEnabled(boolean enabled) {
        searchPanel.getStyle().removeProperty("display");
        if (!enabled) {
            searchPanel.getStyle().setProperty("display",
                                               "none");
        }
    }

    @Override
    public void clearItems() {
        removeAllChildren(dropDownMenu);
    }

    @Override
    public void noItems(String msg) {
        removeAllChildren(dropDownMenu);
        SpanElement span = Document.get().createSpanElement();
        span.setInnerText(msg);
        span.getStyle().setPropertyPx("marginLeft",
                                      10);
        dropDownMenu.appendChild((Node) span);
    }

    @Override
    public void addItem(String key, String value) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(value);

        Event.sinkEvents(anchor,
                         Event.ONCLICK);
        Event.setEventListener(anchor,
                               event -> {
                                   if (Event.ONCLICK == event.getTypeInt()) {
                                       presenter.onItemSelected(key, value);
                                   }
                               });

        LIElement li = Document.get().createLIElement();
        li.appendChild(anchor);
        dropDownMenu.appendChild((Node) li);
    }

    @Override
    public void setSelectedValue(String selectedItem) {
        dropDownText.setTextContent(selectedItem);
    }

    @Override
    public void setDropDownText(String text) {
        dropDownText.setTextContent(text);
    }

    @Override
    public void clearSearch() {
        searchInput.setValue("");
    }

    @Override
    public void setSearchHint(String text) {
        searchInput.setAttribute("placeholder",
                                 text);
    }

    @Override
    public void searchInProgress(String msg) {
        spinnerText.setTextContent(msg);
        spinnerPanel.getStyle().removeProperty("display");
        dropDownMenu.getStyle().setProperty("display",
                                            "none");
    }

    @Override
    public void searchFinished() {
        spinnerPanel.getStyle().setProperty("display",
                                            "none");
        dropDownMenu.getStyle().removeProperty("display");
    }

    @Override
    public String getDefaultSearchHintI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchHint();
    }

    @Override
    public String getDefaultSelectorHintI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchSelectorHint();
    }

    @Override
    public String getDefaultNotFoundI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchNotFoundMessage();
    }

    private void removeAllChildren(org.jboss.errai.common.client.dom.Element element) {
        NodeList nodeList = element.getChildNodes();
        int lenght = nodeList.getLength();
        for (int i = 0; i < lenght; i++) {
            element.removeChild(nodeList.item(0));
        }
    }

    @EventHandler("searchInput")
    void onSearchChanged(KeyUpEvent event) {
        String pattern = searchInput.getValue();
        presenter.search(pattern);
    }

    @EventHandler("searchInput")
    void onSearchClick(ClickEvent event) {
        // Capture and ignore in order to avoid the drop-down to hide
        event.stopPropagation();
    }

    @EventHandler("dropDownButton")
    void onDropDownClick(ClickEvent event) {
        presenter.onItemsShown();
    }

    @EventHandler("searchInput")
    void onSearchOver(MouseOverEvent event) {
        searchInput.focus();
    }
}
