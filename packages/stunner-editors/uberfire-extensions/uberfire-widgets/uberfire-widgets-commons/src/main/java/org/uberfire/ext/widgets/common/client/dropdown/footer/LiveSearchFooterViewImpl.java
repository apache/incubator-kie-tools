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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class LiveSearchFooterViewImpl implements LiveSearchFooterView,
                                                 IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private Div container;

    @Inject
    @DataField
    private Div footer;

    @Inject
    @DataField
    private Anchor newEntryAnchor;

    @Inject
    @DataField
    private Span newEntryAnchorLabel;

    @Inject
    @DataField
    private Anchor resetAnchor;

    @Inject
    @DataField
    private Span resetAnchorLabel;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setResetLabel(String resetLabel) {
        resetAnchorLabel.setTextContent(resetLabel);
    }

    @Override
    public void setNewEntryLabel(String newEntryLabel) {
        newEntryAnchorLabel.setTextContent(newEntryLabel);
    }

    @Override
    public void showReset(boolean show) {
        resetAnchor.setHidden(!show);
    }

    @Override
    public void showAddNewEntry(boolean show) {
        newEntryAnchor.setHidden(!show);
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
    public void onNewEntryPresed(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
        presenter.onNewEntryPressed();
    }

    @EventHandler("resetAnchor")
    public void onResetPressed(ClickEvent clickEvent) {
        presenter.onResetPressed();
    }
}
