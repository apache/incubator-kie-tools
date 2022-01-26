/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.dropdown;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLUListElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtproject.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooter;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponent;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
@Templated
public class LiveSearchDropDownView<TYPE> extends Composite
        implements LiveSearchDropDown.View<TYPE> {

    @Inject
    @DataField
    HTMLDivElement mainPanel;

    @Inject
    @DataField
    HTMLDivElement dropDownPanel;

    @Inject
    @DataField
    HTMLButtonElement dropDownButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement dropDownText;

    @Inject
    @DataField
    HTMLDivElement searchPanel;

    @Inject
    @DataField
    HTMLInputElement searchInput;

    @Inject
    @DataField
    HTMLUListElement dropDownMenu;

    @Inject
    @DataField
    HTMLDivElement spinnerPanel;

    @Inject
    @DataField
    Span spinnerText;

    @Inject
    @DataField
    private LiveSearchFooter liveSearchFooter;

    @Inject
    @DataField
    private NoItemsComponent noItems;

    private boolean resetEnabled = true;
    private boolean newItemEnabled = true;

    private LiveSearchDropDown presenter;

    @Override
    public void init(LiveSearchDropDown presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void initialize() {
        setSearchHint(getDefaultSearchHintI18nMessage());
        setDropDownText(getDefaultSelectorHintI18nMessage());
        setNewInstanceEnabled(false);
        setNewEntryI18nMessage(getDefaultNewEntryI18nMessage());
        liveSearchFooter.init(this::showNewItem, this::clearSelection);
    }

    private void showNewItem() {
        presenter.showNewItem();
    }


    private void clearSelection() {
        presenter.clearSelection();
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        dropDownMenu.style.setProperty("max-height",
                                            maxHeight + "px");
    }

    @Override
    public void setWidth(int minWidth) {
        dropDownButton.style.setProperty("width",
                                              minWidth + "px");
        dropDownPanel.style.setProperty("width",
                                             minWidth + "px");
    }

    @Override
    public void setSearchEnabled(boolean enabled) {
        searchPanel.hidden = !enabled;
    }

    @Override
    public void setClearSelectionEnabled(boolean enabled) {
        resetEnabled = enabled;
        liveSearchFooter.showReset(enabled);
        refreshFooter();
    }

    @Override
    public void setNewInstanceEnabled(boolean enabled) {
        newItemEnabled = enabled;
        liveSearchFooter.showAddNewEntry(enabled);
        refreshFooter();
    }

    private void refreshFooter() {
        liveSearchFooter.getElement().hidden = (!resetEnabled && !newItemEnabled);
    }

    @Override
    public void showNewItemEditor(InlineCreationEditor editor) {
        editor.clear();
        liveSearchFooter.showEditor(editor);
    }

    @Override
    public void restoreFooter() {
        liveSearchFooter.restore();
    }

    @Override
    public void clearItems() {
        DOMUtil.removeAllChildren(dropDownMenu);
        noItems.hide();
    }

    @Override
    public void noItems(String msg) {
        DOMUtil.removeAllChildren(dropDownMenu);
        noItems.setMessage(msg);
        noItems.show();
    }

    @Override
    public void addItem(LiveSearchSelectorItem<TYPE> item) {
        dropDownMenu.appendChild(item.getElement());
    }

    @Override
    public void setSelectedValue(String selectedItem) {
        dropDownText.textContent = (selectedItem);
    }

    @Override
    public void setDropDownText(String text) {
        dropDownText.textContent = (text);
    }

    @Override
    public void clearSearch() {
        searchInput.value = ("");
    }

    @Override
    public void setSearchHint(String text) {
        searchInput.setAttribute("placeholder",
                                 text);
    }

    @Override
    public void searchInProgress(String msg) {
        spinnerText.setText(msg);
        spinnerPanel.style.removeProperty("display");
        dropDownMenu.style.setProperty("display",
                                            "none");
    }

    @Override
    public void searchFinished() {
        spinnerPanel.style.setProperty("display",
                                            "none");
        dropDownMenu.style.removeProperty("display");
    }

    @Override
    public void setEnabled(boolean enabled) {
        dropDownButton.disabled = (!enabled);
    }

    @Override
    public void setClearSelectionMessage(boolean multipleSelection) {
        String message = multipleSelection ? getDefaultClearSelectionI18nMessage() : getDefaultResetSelectionI18nMessage();
        liveSearchFooter.setResetLabel(message);
    }

    @Override
    public void setNewEntryI18nMessage(String message) {
        liveSearchFooter.setNewEntryLabel(message);
    }

    @Override
    public String getDefaultNewEntryI18nMessage() {
        return CommonConstants.INSTANCE.liveSearchNewEntry();
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
    void onSearchChanged(@ForEvent("keyup") Event event) {
        String pattern = searchInput.value;
        presenter.search(pattern);
    }

    @EventHandler("searchInput")
    void onSearchClick(@ForEvent("click") Event event) {
        // Capture and ignore in order to avoid the drop-down to hide
        event.stopPropagation();
    }

    @EventHandler("searchInput")
    void onSearchOverMouseOverEvent(@ForEvent("mouseover") Event event) {
        searchInput.focus();
    }

    @EventHandler("searchInput")
    void onSearchOverKeyDownEvent(@ForEvent("keydown") Event event) {
        // Capture and ignore in order to avoid the js errors
        event.stopPropagation();
    }

    @EventHandler("dropDownButton")
    void onDropDownClick(@ForEvent("click") Event event) {
        presenter.onItemsShown();
    }
}
