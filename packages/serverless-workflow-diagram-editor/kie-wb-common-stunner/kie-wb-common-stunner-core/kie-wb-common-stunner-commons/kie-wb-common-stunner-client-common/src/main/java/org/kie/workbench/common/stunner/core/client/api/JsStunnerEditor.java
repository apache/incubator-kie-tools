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
import org.kie.workbench.common.stunner.core.api.JsDefinitionManager;

public class JsStunnerEditor {

    private static final J2clStunnerEditor jreStunnerEditor = new JreStunnerEditor();

    public JsDefinitionManager getDefinitions() {
        return jreStunnerEditor.getDefinitions();
    }

    public void setDefinitions(JsDefinitionManager definitions) {
        jreStunnerEditor.setDefinitions(definitions);
    }

    public JsStunnerSession getSession() {
        return jreStunnerEditor.getSession();
    }

    public void setSession(JsStunnerSession session) {
        jreStunnerEditor.setSession(session);
    }

    public Object getCanvas() {
        return jreStunnerEditor.getCanvas();
    }

    public void setCanvas(Object canvas) {
        jreStunnerEditor.setCanvas(canvas);
    }



    private static class JreStunnerEditor extends J2clStunnerEditor {

        private JsDefinitionManager definitions;
        private JsStunnerSession session;
        private Object canvas;

        @GwtIncompatible
        @Override
        public JsDefinitionManager getDefinitions() {
            return definitions;
        }

        @GwtIncompatible
        @Override
        public void setDefinitions(JsDefinitionManager definitions) {
            this.definitions = definitions;
        }

        @GwtIncompatible
        @Override
        public JsStunnerSession getSession() {
            return session;
        }

        @GwtIncompatible
        @Override
        public void setSession(JsStunnerSession session) {
            this.session = session;
        }

        @GwtIncompatible
        @Override
        public Object getCanvas() {
            return canvas;
        }

        @GwtIncompatible
        @Override
        public void setCanvas(Object canvas) {
            this.canvas = canvas;
        }
    }

    private static class J2clStunnerEditor {

        public JsDefinitionManager getDefinitions() {
            return Js.uncheckedCast(Reflect.get(Reflect.get(DomGlobal.window, "editor"), "definitions"));
        }

        public void setDefinitions(JsDefinitionManager definitions) {
            Reflect.set(Reflect.get(DomGlobal.window, "editor"), "definitions", definitions);
        }

        public JsStunnerSession getSession() {
            return Js.uncheckedCast(Reflect.get(Reflect.get(DomGlobal.window, "editor"), "session"));
        }

        public void setSession(JsStunnerSession session) {
            Reflect.set(Reflect.get(DomGlobal.window, "editor"), "session", session);
        }

        public Object getCanvas() {
            return Js.uncheckedCast(Reflect.get(Reflect.get(DomGlobal.window, "editor"), "canvas"));
        }

        public void setCanvas(Object canvas) {
            Reflect.set(Reflect.get(DomGlobal.window, "editor"), "canvas", canvas);
        }
    }
}
