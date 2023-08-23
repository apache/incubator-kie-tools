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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Element;
import elemental2.dom.NodeList;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

@JsType(isNative = true)
public abstract class JQueryTooltip {

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQueryTooltip $(final Element selector);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQueryTooltip $(final NodeList<Element> selector);

    public native JQueryTooltip tooltip(final JavaScriptObject properties);

    public native JQueryTooltip tooltip();
}
