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

package org.uberfire.ext.widgets.common.client.dropdown.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.DOMUtil;

@Templated
@Dependent
public class LiveSearchFooterViewImpl implements LiveSearchFooterView,
                                                 IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private HTMLDivElement container;

    @Inject
    @DataField
    private HTMLDivElement footer;

    @Inject
    @DataField
    private HTMLAnchorElement newEntryAnchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement newEntryAnchorLabel;

    @Inject
    @DataField
    private HTMLAnchorElement resetAnchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement resetAnchorLabel;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setResetLabel(String resetLabel) {
        resetAnchorLabel.textContent = (resetLabel);
    }

    @Override
    public void setNewEntryLabel(String newEntryLabel) {
        newEntryAnchorLabel.textContent = (newEntryLabel);
    }

    @Override
    public void showReset(boolean show) {
        if (show) {
            resetAnchor.style.display = "inline";
        } else {
            resetAnchor.style.display = "none";
        }
    }

    @Override
    public void showAddNewEntry(boolean show) {
        if (show) {
            resetAnchor.style.display = "inline";
        } else {
            resetAnchor.style.display = "none";
        }
    }

    @Override
    public void show(HTMLElement element) {
        DOMUtil.removeAllChildren(container);
        container.appendChild(element);
    }

    @Override
    public void restore() {
        DOMUtil.removeAllChildren(container);
        container.appendChild(footer);
    }

    @EventHandler("newEntryAnchor")
    public void onNewEntryPresed(@ForEvent("click") Event clickEvent) {
        clickEvent.stopPropagation();
        presenter.onNewEntryPressed();
    }

    @EventHandler("resetAnchor")
    public void onResetPressed(@ForEvent("click") Event clickEvent) {
        presenter.onResetPressed();
    }
}
