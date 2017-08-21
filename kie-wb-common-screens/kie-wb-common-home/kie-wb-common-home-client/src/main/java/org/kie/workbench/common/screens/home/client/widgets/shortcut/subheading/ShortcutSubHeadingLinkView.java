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

package org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ShortcutSubHeadingLinkView implements ShortcutSubHeadingLinkPresenter.View,
                                                   IsElement {

    private ShortcutSubHeadingLinkPresenter presenter;

    @Inject
    @DataField("link")
    Anchor link;

    @Override
    public void init(final ShortcutSubHeadingLinkPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLabel(final String label) {
        link.setTextContent(label);
    }

    @Override
    public void disable() {
        link.getClassList().add("disabled");
    }

    @EventHandler("link")
    public void linkClick(final ClickEvent clickEvent) {
        clickEvent.stopPropagation();
        presenter.goToPerspective();
    }
}
