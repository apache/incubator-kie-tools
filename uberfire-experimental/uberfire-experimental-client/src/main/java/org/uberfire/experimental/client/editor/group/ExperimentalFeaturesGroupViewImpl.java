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

package org.uberfire.experimental.client.editor.group;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Document;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.util.ExperimentalUtils;

@Templated
public class ExperimentalFeaturesGroupViewImpl implements ExperimentalFeaturesGroupView,
                                                          IsElement {

    @Inject
    @DataField
    private HTMLDivElement panel;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement caret;

    @Inject
    @DataField
    private HTMLLabelElement header;

    @Inject
    @DataField
    private HTMLUListElement featuresContainer;

    @Inject
    @DataField
    private HTMLAnchorElement enableAll;

    @Inject
    private Elemental2DomUtil util;

    @Inject
    private Document document;

    private Presenter presenter;

    @PostConstruct
    public void init() {
        String id = ExperimentalUtils.createUniqueId();
        header.setAttribute("data-target", "#" + id);
        panel.setAttribute("id", id);
    }

    @Override
    public void setLabel(String label) {
        header.textContent = label;
    }

    @Override
    public void setEnableAllLabel(String label) {
        enableAll.textContent = label;
    }

    @Override
    public void render(ExperimentalFeatureEditor editor) {
        HTMLLIElement li = (HTMLLIElement) document.createElement("li");
        li.setAttribute("class", "list-group-item");
        li.appendChild(editor.getElement());
        featuresContainer.appendChild(li);
    }

    @Override
    public void clear() {
        util.removeAllElementChildren(header);
        util.removeAllElementChildren(featuresContainer);
        collapse();
    }

    @Override
    public void expand() {
        header.classList.remove("collapsed");
        header.setAttribute("aria-expanded", "true");
        panel.classList.add("in");
        panel.setAttribute("aria-expanded", "true");
        arrangeCaret();
    }

    @Override
    public void collapse() {
        header.classList.add("collapsed");
        header.setAttribute("aria-expanded", "false");
        panel.classList.remove("in");
        panel.setAttribute("aria-expanded", "false");
        arrangeCaret();
    }

    @Override
    public void arrangeCaret() {
        if (presenter.isExpanded()) {
            caret.className = "fa fa-caret-down";
        } else {
            caret.className = "fa fa-caret-right";
        }
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("enableAll")
    public void onEnableAll(ClickEvent clickEvent) {
        presenter.doEnableAll();
        enableAll.blur();
    }

    @EventHandler("header")
    public void onExpand(ClickEvent clickEvent) {
        presenter.notifyExpand();
    }
}
