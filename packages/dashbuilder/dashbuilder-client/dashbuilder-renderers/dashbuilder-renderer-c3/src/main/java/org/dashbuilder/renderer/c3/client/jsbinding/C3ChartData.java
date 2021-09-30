/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.jsbinding;

import elemental2.core.JsObject;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3ChartData {
    
    @JsOverlay
    static C3ChartData create(String[][] columns, 
                                     String type, 
                                     String[][] groups, 
                                     JsObject xs, 
                                     C3Selection selection) {
        C3ChartData data = new C3ChartData();
        data.setColumns(columns);
        data.setType(type);
        data.setGroups(groups);
        data.setXs(xs);
        data.setSelection(selection);
        return data;
    }
    
    @JsProperty
    public native void setColumns(String columns[][]);

    @JsProperty
    public native void setType(String type);
    
    @JsProperty
    public native void setGroups(String groups[][]); 
    
    @JsProperty
    public native void setXs(JsObject xs);
    
    @JsProperty
    public native void setOrder(String order);
    
    @JsProperty
    public native void setOnselected(SelectCallback callback);
    
    @JsProperty
    public native void setOnunselected(SelectCallback callback);
    
    @JsProperty
    public native void setSelection(C3Selection selection);
    
    @JsFunction
    @FunctionalInterface
    public static interface SelectCallback {
        
        void callback(C3DataInfo data);
    
    }
    

}