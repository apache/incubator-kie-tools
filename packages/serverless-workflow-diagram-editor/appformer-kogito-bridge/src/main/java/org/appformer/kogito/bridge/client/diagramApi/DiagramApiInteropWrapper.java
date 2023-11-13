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


package org.appformer.kogito.bridge.client.diagramApi;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Javascript bridge to access actual DiagramApi available in the envelope namespace
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "envelope")
public class DiagramApiInteropWrapper {

    /**
     * Move the cursor in the text editor to a specified node
     * @param nodeName the name of the target node
     */
    public native void onNodeSelected(String nodeName);

    /**
     * Notify that the diagram finished setting its content
     */
    public native void setContentSuccess();

    @JsProperty(name = "diagramService")
    public static native DiagramApiInteropWrapper get();
}
