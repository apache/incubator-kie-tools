/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.util;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableRowElement;

/**
 * Create DOM Elements. Can be mocked in tests.
 *
 */
@ApplicationScoped
public class DomFactory {

    public Element element(String elementType) {
        return DomGlobal.document.createElement(elementType);
    }
    
    public Element listItem(String text) {
        Element li = DomGlobal.document.createElement("li");
        li.textContent = text;
        return li;
    }

    public HTMLInputElement input() {
        return (HTMLInputElement) DomGlobal.document.createElement("input");
    }

    public HTMLTableCellElement tableCell() {
        return (HTMLTableCellElement) DomGlobal.document.createElement("td");
    }

    public HTMLTableRowElement tableRow() {
        return (HTMLTableRowElement) DomGlobal.document.createElement("tr");
    }

}
