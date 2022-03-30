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

import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;

@Dependent
public class Modal implements IsElement {

    private Document document = DomGlobal.document;

    @Inject
    private HTMLDivElement div;

    @Override
    public HTMLElement getElement() {
        return div;
    }

    public void show() {
        DomGlobal.document.body.appendChild(div);
        show(div);
    }

    public void hide() {
        hide(div);
        DomGlobal.document.body.removeChild(div);
    }

    protected void show(final Element e) {
        throw new Error(getClass().getCanonicalName()+".show");
    }/*-{
        $wnd.jQuery(e).modal('show')
    }-*/;

    protected void hide(final Element e) {
        throw new Error(getClass().getCanonicalName()+".show");

    }/*-{
        $wnd.jQuery(e).modal('hide')
    }-*/;
}