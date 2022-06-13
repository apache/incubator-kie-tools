/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.setup;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Bridge to allow users setup
 *
 */
@JsType(isNative = true)
public interface RuntimeClientSetup {

    @JsProperty
    String getMode();

    @JsProperty
    String getPath();
    
    @JsProperty
    boolean getHideNavBar();

    @JsProperty
    String[] getDashboards();

    class Builder {

        @JsProperty(name = "dashbuilder", namespace = JsPackage.GLOBAL)
        public static native RuntimeClientSetup get();

    }

}
