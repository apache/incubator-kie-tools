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
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import jsinterop.base.Js;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectorDropDownDisplayerView extends AbstractErraiDisplayerView<SelectorDropDownDisplayer>
                                           implements SelectorDropDownDisplayer.View {

    private static final String MIN_WIDTH_ATTR = "min-width";
    private static final String MAX_WIDTH_ATTR = "max-width";

    @Inject
    @DataField
    HTMLDivElement containerDiv;

    @Inject
    @DataField
    @Named("span")
    HTMLElement titleSpan;

    @Inject
    @DataField
    HTMLDivElement dropDownDiv;

    @Inject
    @DataField
    HTMLButtonElement dropDownButton;

    @Inject
    @DataField
    HTMLDivElement dropDownText;

    @Inject
    @DataField
    HTMLUListElement dropDownMenu;

    @Inject
    Elemental2DomUtil domUtil;

    private boolean menuVisible;

    @Override
    public void init(SelectorDropDownDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(Js.cast(containerDiv));
        DomGlobal.document.addEventListener("click", e -> {
            if (!containerDiv.contains(Js.cast(e.target))) {
                hideItems();
            }
        });
    }

    @Override
    public void showTitle(String title) {
        titleSpan.textContent = title;
    }

    @Override
    public void margins(int top, int bottom, int left, int right) {
        containerDiv.style.setProperty("margin-top", top + "px");
        containerDiv.style.setProperty("margin-bottom", bottom + "px");
        containerDiv.style.setProperty("margin-left", left + "px");
        containerDiv.style.setProperty("margin-right", right + "px");
    }

    @Override
    public void setWidth(int width) {
        var widthStr = width + "px";
        containerDiv.style.setProperty(MAX_WIDTH_ATTR, widthStr);
        dropDownMenu.style.setProperty(MAX_WIDTH_ATTR, widthStr);
        containerDiv.style.setProperty(MIN_WIDTH_ATTR, widthStr);
        dropDownMenu.style.setProperty(MIN_WIDTH_ATTR, widthStr);
    }

    @Override
    public void showSelectHint(String column, boolean multiple) {
        String hint = "- " + SelectorConstants.INSTANCE.selectorDisplayer_select() + " " + column + " - ";
        dropDownText.textContent = hint;
    }

    @Override
    public void showResetHint(String column, boolean multiple) {
        // empty
    }

    @Override
    public void showCurrentSelection(String text, String hint) {
        dropDownText.textContent = text;
        dropDownButton.title = hint;
    }

    @Override
    public void clearItems() {
        domUtil.removeAllElementChildren(dropDownMenu);
    }

    @Override
    public void addItem(SelectorDropDownItem item) {
        dropDownMenu.appendChild(Js.cast(item.getView().getElement()));
    }

    @Override
    public String getGroupsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_columnsTitle();
    }

    @Override
    public void hideItems() {
        menuVisible = false;
        updateMenuVisibility();
    }

    @EventHandler("dropDownButton")
    public void onMenuClicked(ClickEvent event) {
        menuVisible = !menuVisible;
        updateMenuVisibility();
        dropDownMenu.focus();
    }

    private void updateMenuVisibility() {
        dropDownMenu.style.display = menuVisible ? "inline" : "none";
    }

}
