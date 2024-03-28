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

package org.kie.workbench.common.stunner.core.client.api;

import elemental2.core.Reflect;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import org.kie.j2cl.tools.utils.GwtIncompatible;

public class JsWindow {

    private static final J2CLWindow jreWindow = new JreWindow();

    public static JsStunnerEditor getEditor() {
        return jreWindow.getEditor();
    }

    public static void setEditor(JsStunnerEditor editor) {
        jreWindow.setEditor(editor);
    }

    public static Object getCanvas() {
        return jreWindow.getCanvas();
    }

    public static void setCanvas(Object canvas) {
        jreWindow.setCanvas(canvas);
    }

    private static class JreWindow extends J2CLWindow {

        private JsStunnerEditor editor;
        private Object canvas;

        @GwtIncompatible
        @Override
        protected JsStunnerEditor getEditor() {
            return editor;
        }

        @GwtIncompatible
        @Override
        protected void setEditor(JsStunnerEditor editor) {
            this.editor = editor;
        }

        @GwtIncompatible
        @Override
        protected Object getCanvas() {
            return canvas;
        }

        @GwtIncompatible
        @Override
        protected void setCanvas(Object canvas) {
            this.canvas = canvas;
        }


    }

    private static class J2CLWindow {

        protected JsStunnerEditor getEditor() {
            return Js.uncheckedCast(Reflect.get(DomGlobal.window, "editor"));
        }

        protected void setEditor(JsStunnerEditor editor) {
            Reflect.set(DomGlobal.window, "editor", editor);
        }

        protected Object getCanvas() {
            return Reflect.get(DomGlobal.window, "canvas");
        }

        protected void setCanvas(Object canvas) {
            Reflect.set(DomGlobal.window, "canvas", canvas);
        }
    }
}
