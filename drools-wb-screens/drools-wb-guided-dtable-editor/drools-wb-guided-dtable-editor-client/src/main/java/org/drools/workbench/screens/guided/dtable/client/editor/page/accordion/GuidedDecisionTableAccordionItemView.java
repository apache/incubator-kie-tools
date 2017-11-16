/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.page.accordion;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class GuidedDecisionTableAccordionItemView implements GuidedDecisionTableAccordionItem.View,
                                                             IsElement {

    @DataField("title")
    private HTMLAnchorElement title;

    @DataField("content")
    private HTMLDivElement content;

    private Elemental2DomUtil elemental2DomUtil;

    private GuidedDecisionTableAccordionItem presenter;

    @Inject
    public GuidedDecisionTableAccordionItemView(final HTMLAnchorElement title,
                                                final HTMLDivElement content,
                                                final Elemental2DomUtil elemental2DomUtil) {
        this.title = title;
        this.content = content;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @Override
    public void init(final GuidedDecisionTableAccordionItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTitle(final String title) {
        this.title.textContent = title;
    }

    @Override
    public void setContent(final Widget widget) {
        elemental2DomUtil.appendWidgetToElement(content, widget);
    }

    @Override
    public void setItemId(final String itemId) {
        title.href = "#" + itemId;
        content.id = itemId;
    }

    @Override
    public void setOpen(final boolean isOpen) {

        final DOMTokenList classList = content.classList;
        final String opened = "in";

        if (isOpen) {
            classList.add(opened);
        } else {
            classList.remove(opened);
        }
    }

    @Override
    public void setParentId(final String parentId) {
        title.setAttribute("data-parent", "#" + parentId);
    }
}
