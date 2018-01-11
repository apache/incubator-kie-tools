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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponent;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
@Templated
public class LiveSearchDropDownView<TYPE> extends Composite
        implements LiveSearchDropDown.View<TYPE> {

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

    @Inject
    @DataField
    UnorderedList resetMenu;

    @Inject
    @DataField
    Anchor resetAnchor;

    @Inject
    private NoItemsComponent noItemsComponent;

    LiveSearchDropDown presenter;

    @Override
    public void init(LiveSearchDropDown presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void initialize() {
        setSearchHint(getDefaultSearchHintI18nMessage());
        setDropDownText(getDefaultSelectorHintI18nMessage());
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
    public void setClearSelectionEnabled(boolean enabled) {
        resetMenu.getStyle().removeProperty("display");
        if (!enabled) {
            resetMenu.getStyle().setProperty("display",
                                             "none");
        }
    }

    @Override
    public void clearItems() {
        DOMUtil.removeAllChildren(dropDownMenu);
    }

    @Override
    public void noItems(String msg) {
        DOMUtil.removeAllChildren(dropDownMenu);
        noItemsComponent.setMessage(msg);
        dropDownMenu.appendChild(noItemsComponent.getElement());
    }

    @Override
    public void addItem(LiveSearchSelectorItem<TYPE> item) {
        dropDownMenu.appendChild(item.getElement());
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
    public void setEnabled(boolean enabled) {
        dropDownButton.setDisabled(!enabled);
    }

    @Override
    public void setClearSelectionMessage(boolean multipleSelection) {
        String message = multipleSelection ? getDefaultClearSelectionI18nMessage() : getDefaultResetSelectionI18nMessage();

        resetAnchor.setTextContent(message);
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

    @Override
    public String getDefaultResetSelectionI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchResetSelectionMessage();
    }

    @Override
    public String getDefaultClearSelectionI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchClearSelectionMessage();
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

    @EventHandler("searchInput")
    void onSearchOver(MouseOverEvent event) {
        searchInput.focus();
    }

    @EventHandler("searchInput")
    void onSearchOver(KeyDownEvent event) {
        // Capture and ignore in order to avoid the js errors
        event.stopPropagation();
    }

    @EventHandler("dropDownButton")
    void onDropDownClick(ClickEvent event) {
        presenter.onItemsShown();
    }

    @EventHandler("resetAnchor")
    void onResetAnchorClick(ClickEvent event) {
        presenter.clearSelection();
    }
}
