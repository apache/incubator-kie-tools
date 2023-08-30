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
public class ResourceListOptions {

    /**
     * Sets the search type of the request. When "traversal" the engine will search the resource list on the whole space. </br>
     * When "asset-folder" the engine will only search on the opened asset folder.
     * @param type
     */
    @JsProperty
    native void setType(String type);

    /**
     * Creates an Options instance with traversal search.
     * 
     * @return
     * 
     * Options with traversal type
     */
    @JsOverlay
    public static ResourceListOptions traversal() {
        ResourceListOptions options = new ResourceListOptions();
        options.setType("traversal");
        return options;
    }

    /**
     * Creates an Options instance with asset-folder type
     * 
     * @return
     * 
     * Options with asset-folder type
     */
    @JsOverlay
    public static ResourceListOptions assetFolder() {
        ResourceListOptions options = new ResourceListOptions();
        options.setType("asset-folder");
        return options;
    }

}
