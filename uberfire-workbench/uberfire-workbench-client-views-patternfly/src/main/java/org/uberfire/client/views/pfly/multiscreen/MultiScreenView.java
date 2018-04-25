/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.multiscreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class MultiScreenView implements IsElement,
                                        RequiresResize {

    @Inject
    @DataField("screen")
    HTMLDivElement screen;

    @Inject
    @DataField("content")
    ResizeFlowPanel content;

    @Inject
    @DataField("title")
    HTMLDivElement title;

    @Inject
    @DataField("actions")
    HTMLDivElement actions;

    HTMLDivElement actionsMenu;

    @Inject
    @DataField("close")
    Button close;

    @Inject
    @DataField("close-group")
    HTMLDivElement closeGroup;

    @Inject
    HTMLDocument document;

    @Override
    public HTMLElement getElement() {
        return screen;
    }

    public void setContent(final IsWidget widget) {
        content.add(widget);
    }

    public void setCloseHandler(final Command closeHandler) {
        this.close.setClickHandler(closeHandler);
    }

    public void setTitle(final String title) {
        this.title.textContent = title;
    }

    public void setTitleWidget(final IsWidget widget) {
        final HTMLElement element = Js.cast(widget.asWidget().getElement());
        HTMLDivElement div = (HTMLDivElement) document.createElement("div");
        div.style.setProperty("display",
                              "inline-block");
        div.style.setProperty("padding-left",
                              "10px");
        div.style.setProperty("vertical-align",
                              "middle");
        div.appendChild(element);
        this.title.appendChild(div);
    }

    public void show() {
        screen.classList.remove("hidden");
    }

    public boolean isVisible(){
        return screen.classList.contains("hidden") == false;
    }

    public void hide() {
        screen.classList.add("hidden");
    }

    public void addMenus(final HTMLElement element) {
        if (actionsMenu == null) {
            actionsMenu = (HTMLDivElement) document.createElement("div");
            actionsMenu.classList.add("form-group");
            if (actions.hasChildNodes()) {
                actions.insertBefore(actionsMenu,
                                     actions.childNodes.item(0));
            } else {
                actions.appendChild(actionsMenu);
            }
        }
        actionsMenu.appendChild(element);
    }

    public void disableClose() {
        actions.removeChild(closeGroup);
    }

    @Override
    public void onResize() {
        content.onResize();
    }
}
