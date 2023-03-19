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

package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
public class NoItemsComponent implements IsElement {

    private NoItemsComponentView view;

    @Inject
    public NoItemsComponent(NoItemsComponentView view) {
        this.view = view;
    }

    public void setMessage(String message) {
        if(message == null || message.isEmpty()) {
            message = CommonConstants.INSTANCE.liveSearchNotFoundMessage();
        }

        view.setMessage(message);
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
