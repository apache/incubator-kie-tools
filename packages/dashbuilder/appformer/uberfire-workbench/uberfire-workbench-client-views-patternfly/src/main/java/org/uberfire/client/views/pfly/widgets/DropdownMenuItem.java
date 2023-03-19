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

package org.uberfire.client.views.pfly.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class DropdownMenuItem implements IsElement {

    @Inject
    @DataField("dropdown-item")
    HTMLLIElement item;

    @Inject
    @DataField("anchor")
    HTMLAnchorElement anchor;

    Command clickHandler;

    @Override
    public HTMLElement getElement() {
        return item;
    }

    public void setText(final String text) {
        anchor.textContent = text;
    }

    public void setClickHandler(final Command clickHandler) {
        this.clickHandler = clickHandler;
    }

    @EventHandler("anchor")
    public void onClick(@ForEvent("click") Event event) {
        if (clickHandler != null) {
            clickHandler.execute();
        }
    }
}
