/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor;

import java.util.HashMap;
import java.util.Map;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.kie.j2cl.tools.di.core.IsElement;

public class FlowPanel implements IsElement {

    private final Map<IsElement, HTMLElement> children = new HashMap<>();

    private final HTMLDivElement root = (HTMLDivElement) DomGlobal.document.createElement("div");

    public void add(IsElement element) {
        HTMLElement child = element.getElement();
        children.put(element, child);
        root.appendChild(child);
    }

    public void remove(IsElement element) {
        HTMLElement child = children.remove(element);
        if (child != null) {
            root.removeChild(child);
        }
    }

    public void clear() {
        children.forEach((element, htmlElement) -> root.removeChild(htmlElement));
        children.clear();
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }

    public boolean isVisible() {
        return !root.style.display.equals("none");
    }
}
