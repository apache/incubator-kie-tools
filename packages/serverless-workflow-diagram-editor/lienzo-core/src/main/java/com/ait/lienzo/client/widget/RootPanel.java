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

package com.ait.lienzo.client.widget;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;

public class RootPanel {

    static RootPanel INSTANCE = new RootPanel();

    private JsArray<Element> elements = new JsArray<>();

    public static RootPanel get() {
        return INSTANCE;
    }

    public void add(Element child) {
        elements.push(child);
        DomGlobal.document.body.appendChild(child);
    }

    public void remove(Element child) {
        DomGlobal.document.body.removeChild(child);
        child.remove();
    }
}
