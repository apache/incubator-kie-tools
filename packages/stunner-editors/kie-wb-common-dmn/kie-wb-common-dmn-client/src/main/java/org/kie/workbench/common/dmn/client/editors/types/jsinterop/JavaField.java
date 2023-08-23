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

package org.kie.workbench.common.dmn.client.editors.types.jsinterop;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JavaField {

    /** Field Name */
    @JsProperty(name = "name")
    public native String getName();

    /** Java Class Name (eg. java.lang.String OR com.mypackage.Test) */
    @JsProperty(name = "type")
    public native String getType();

    /** List Type */
    @JsProperty(name = "isList")
    public native boolean isList();

    /** The assigned DMN Type of the field (eg. Simple DMN Type (ANY, NUMBER, STRING, .. ) OR custom one */
    @JsProperty(name = "dmnTypeRef")
    public native String getDmnTypeRef();

    @JsProperty(name = "dmnTypeRef")
    public native void setDmnTypeRef(String dmnTypeRef);

}
