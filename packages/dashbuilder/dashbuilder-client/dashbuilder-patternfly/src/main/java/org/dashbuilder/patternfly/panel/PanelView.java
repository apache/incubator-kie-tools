/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.panel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PanelView implements Panel.View {

    private Panel presenter;

    @Inject
    Elemental2DomUtil util;

    @Inject
    @DataField
    HTMLDivElement pnlRoot;

    @Inject
    @DataField
    HTMLDivElement pnlHeader;

    @Inject
    @DataField
    HTMLButtonElement pnlExpandButton;

    @Inject
    @DataField
    HTMLDivElement pnlBody;

    @Inject
    @DataField
    @Named("i")
    HTMLElement pnlIcon;

    @Override
    public void init(Panel presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return pnlRoot;
    }

    public void collapse() {
        pnlRoot.classList.remove("pf-m-expanded");
        pnlBody.style.display = "none";
    }

    public void show() {
        pnlRoot.classList.add("pf-m-expanded");
        pnlBody.style.display = "block";
    }

    @EventHandler("pnlExpandButton")
    public void onExpandClick(ClickEvent e) {
        presenter.collapseAction();
    }

    @Override
    public void setContent(Element element) {
        pnlBody.appendChild(element);
    }

    @Override
    public void setTitle(String title) {
        pnlHeader.textContent = title;

    }

}
