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

package org.uberfire.client.views.pfly.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.HTMLElement;

@Dependent
public class Modal implements IsElement {

    @Inject
    private Document document;

    @Inject
    private Div div;

    @Override
    public HTMLElement getElement() {
        return div;
    }

    public void show() {
        document.getBody().appendChild(div);
        show(div);
    }

    public void hide() {
        hide(div);
        document.getBody().removeChild(div);
    }

    protected native void show(final Element e) /*-{
        $wnd.jQuery(e).modal('show')
    }-*/;

    protected native void hide(final Element e) /*-{
        $wnd.jQuery(e).modal('hide')
    }-*/;
}