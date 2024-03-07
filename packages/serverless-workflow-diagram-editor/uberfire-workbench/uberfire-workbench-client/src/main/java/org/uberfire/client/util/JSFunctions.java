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

package org.uberfire.client.util;

import elemental2.core.Function;
import elemental2.core.JsMap;
import elemental2.core.Reflect;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.kie.j2cl.tools.di.core.SyncBeanDef;
import org.uberfire.client.mvp.EditorActivity;

public class JSFunctions {

    private JSFunctions() {
        // Empty
    }
    public static void nativeRegisterGwtEditorProvider(){
        Reflect.set(DomGlobal.window, "gwtEditorBeans",  new JsMap<String, SyncBeanDef<?>>());

        Reflect.set(DomGlobal.window, "resolveEditor", (ResolveEditorFunction) id -> {
            JsMap<String, SyncBeanDef<?>> gwtEditorBeans = Js.uncheckedCast(Reflect.get(DomGlobal.window, "gwtEditorBeans"));
            return gwtEditorBeans.get(id);
        });
    }

    @JsType
    public static class GWTEditorSupplier {

        private final SyncBeanDef<EditorActivity> bean;

        public GWTEditorSupplier(SyncBeanDef<EditorActivity> bean) {
            this.bean = bean;
        }

        public final GWTEditor get() {
            return new GWTEditor(bean.newInstance());
        }

    }

    public static void nativeRegisterGwtClientBean(final String id, final SyncBeanDef<EditorActivity> bean) {
        JsMap<String, GWTEditorSupplier> gwtEditorBeans = Js.uncheckedCast(Reflect.get(DomGlobal.window, "gwtEditorBeans"));
        gwtEditorBeans.set(id, new GWTEditorSupplier(bean));
    }

    public static void notifyJSReady() {
        if(Reflect.has(DomGlobal.window, "appFormerGwtFinishedLoading")) {
            ((Function) Reflect.get(DomGlobal.window, "appFormerGwtFinishedLoading")).call(DomGlobal.window);
        }
    }


    @FunctionalInterface
    @JsFunction
    public interface ResolveEditorFunction {
        SyncBeanDef<?> get(String id);
    }

}
