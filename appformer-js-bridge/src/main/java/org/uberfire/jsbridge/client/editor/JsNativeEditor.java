/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.editor;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.PlaceRequest;

public class JsNativeEditor {

    private final Object self;
    private final String componentId;
    private final HTMLElement container;

    public JsNativeEditor(final String componentId, final Object self) {
        this.componentId = componentId;
        this.self = self;
        this.container = (HTMLElement) DomGlobal.document.createElement("div");
    }

    public String getComponentId() {
        return componentId;
    }

    public native boolean af_isDirty() /*-{
        return this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_isDirty"]();
    }-*/;

    public native int af_priority() /*-{
        return this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_priority"];
    }-*/;

    public native String af_componentTitle() /*-{
        return this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_componentTitle"];
    }-*/;

    public native String[] af_resourceTypes() /*-{
        return this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_resourceTypes"];
    }-*/;

    public void af_onEditorStartup(final ObservablePath path, final PlaceRequest place) {
        this.native_af_onEditorStartup(path, place);
    }

    public native void native_af_onEditorStartup(final Object path, final Object place) /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onEditorStartup"](path, place);
    }-*/;

    public void af_onOpen() {
        this.mount();
        native_af_onOpen();
    }

    public native void native_af_onOpen() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onOpen"]();
    }-*/;

    public native void af_onSave() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onSave"]();
    }-*/;

    public native void af_onFocus() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onFocus"]();
    }-*/;

    public native void af_onLostFocus() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onLostFocus"]();
    }-*/;

    public native boolean af_onMayClose() /*-{
        return this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onMayClose"]();
    }-*/;

    public void af_onClose() {
        this.unmount();
        native_af_onClose();
    }

    public native void native_af_onClose() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onClose"]();
    }-*/;

    public native void af_onShutdown() /*-{
        this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_onShutdown"]();
    }-*/;

    public native void mount() /*-{
        $wnd.AppFormer.render(
                this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_componentRoot"](),
                this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::container);
    }-*/;

    public native void unmount() /*-{
        if (this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::self["af_isReact"]) {
            $wnd.ReactDOM.unmountComponentAtNode(this.@org.uberfire.jsbridge.client.editor.JsNativeEditor::container);
        }
    }-*/;

    public HTMLElement getElement() {
        return container;
    }
}
