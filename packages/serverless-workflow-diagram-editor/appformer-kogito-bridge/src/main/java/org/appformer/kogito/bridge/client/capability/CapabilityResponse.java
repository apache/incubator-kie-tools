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

package org.appformer.kogito.bridge.client.capability;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Wrapper object for capability responses.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CapabilityResponse<T> {

    /**
     * Status of the response.
     */
    @JsProperty
    public native String getStatus();

    /**
     * Optional body associated with the response.
     */
    @JsProperty
    public native T getBody();

    /**
     * Optional message associated with the response.
     */
    @JsProperty
    public native String getMessage();
}
