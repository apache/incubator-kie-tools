/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.jsinterop;

import java.util.List;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.JSIUtils;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JavaClass {

    /** Java Class Name (eg. java.lang.String OR com.mypackage.Test) */
    @JsProperty(name = "name")
    public native String getName();

    @JsProperty(name = "name")
    public native void setName(String name);

    /** Java Fields of the class */
    @JsOverlay
    public final List<JavaField> getFields() {
        if (getNativeFields() == null) {
            setNativeFields(JSIUtils.getNativeArray());
        }
        return JSIUtils.toList(JSIUtils.getUnwrappedElementsArray(getNativeFields()));
    }

    @JsProperty(name = "fields")
    public native JsArrayLike<JavaField> getNativeFields();

    @JsProperty(name = "fields")
    public native void setNativeFields(JsArrayLike<JavaField> models);

}
