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

package org.uberfire.client.screens.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class WidgetsShowcaseView implements UberElement<WidgetsShowcasePresenter> {

    @Inject
    @DataField
    private Div root;

    @Inject
    @DataField
    private Popover popover;

    @Inject
    @DataField("dynamic-popover")
    private Div dynamicPopover;

    @Inject
    @DataField("popover-override")
    private Popover popoverOverride;

    @Inject
    private ManagedInstance<Popover> popoversProvider;

    @Override
    public void init(final WidgetsShowcasePresenter presenter) {
        //Ex 2
        final Popover newPopover = popoversProvider.get();
        newPopover.setContent("dynamic text");
        newPopover.setTrigger("click");
        newPopover.setPlacement("bottom");
        newPopover.setContainer("body");
        final Anchor anchor = (Anchor) getDocument().createElement("a");
        anchor.setAttribute("data-toggle", "popover");
        anchor.setTextContent("View popover");
        newPopover.getElement().appendChild(anchor);
        dynamicPopover.appendChild(newPopover.getElement());
        //Ex 3
        popoverOverride.setContent("New content!");
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }
}
