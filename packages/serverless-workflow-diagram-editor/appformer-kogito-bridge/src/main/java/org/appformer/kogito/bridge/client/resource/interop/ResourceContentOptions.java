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


package org.appformer.kogito.bridge.client.resource.interop;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ResourceContentOptions {

    /**
     * The a content type. When "text" the resource content is returned in text format. </br>
     * When "binary" then the resource content is returned in a base64 encoded format.
     * @param type
     */
    @JsProperty
    native void setType(String type);

    /**
     * Creates an Options instance with binary type (base64 encoded binary content)
     * 
     * @return
     * 
     * Options with binary type (base64 encoded binary content)
     */
    @JsOverlay
    public static ResourceContentOptions binary() {
        ResourceContentOptions options = new ResourceContentOptions();
        options.setType("binary");
        return options;
    }

    /**
     * Creates an Options instance with text type
     * 
     * @return
     * 
     * Options with text type
     */
    @JsOverlay
    public static ResourceContentOptions text() {
        ResourceContentOptions options = new ResourceContentOptions();
        options.setType("text");
        return options;
    }

}
