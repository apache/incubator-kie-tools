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


package org.kie.workbench.common.stunner.bpmn.documentation.model.element;

import java.util.List;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ElementTotal {

    private Element[] elements;
    private int quantity;
    private String type;//category
    private String typeIcon;//category icon

    private ElementTotal() {

    }

    @JsOverlay
    public static final ElementTotal create(List<Element> elements, String type, String typeIcon) {
        final ElementTotal instance = new ElementTotal();
        instance.quantity = elements.size();
        instance.elements = elements.toArray(new Element[elements.size()]);
        instance.type = type;
        instance.typeIcon = typeIcon;
        return instance;
    }

    @JsOverlay
    public final String getType() {
        return type;
    }

    @JsOverlay
    public final Element[] getElements() {
        return elements;
    }

    @JsOverlay
    public final int getQuantity() {
        return quantity;
    }

    @JsOverlay
    public final String getTypeIcon() {
        return typeIcon;
    }
}