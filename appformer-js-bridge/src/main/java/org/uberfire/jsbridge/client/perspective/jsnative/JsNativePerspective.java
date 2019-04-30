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

package org.uberfire.jsbridge.client.perspective.jsnative;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JsNativePerspective {

    private final JavaScriptObject self;
    private final JsNativeContextDisplay contextDisplay;
    private final JsNativeView view;

    public JsNativePerspective(final JavaScriptObject self) {
        this.self = self;
        this.contextDisplay = new JsNativeContextDisplay(self, "af_displayInfo");
        this.view = new JsNativeView(self, "af_parts", "af_panels");
    }

    public String componentId() {
        return (String) get("af_componentId");
    }

    public String name() {
        return (String) get("af_name");
    }

    public boolean isDefault() {
        return (boolean) get("af_isDefault");
    }

    public boolean isTransient() {
        return (boolean) get("af_isTransient");
    }

    public boolean isTemplated() {
        return (boolean) get("af_isTemplated");
    }

    public Menus menus() {
        return (Menus) get("af_menus");
    }

    public ToolBar toolbar() {
        return (ToolBar) get("af_toolbar");
    }

    public String defaultPanelType() {
        return (String) get("af_defaultPanelType");
    }

    public JsNativeView view() {
        return this.view;
    }

    public JsNativeContextDisplay contextDisplay() {
        return this.contextDisplay;
    }

    public void onStartup() {
        run("af_onStartup");
    }

    public void onOpen() {
        run("af_onOpen");
    }

    public Set<HTMLElement> getContainerComponents(final HTMLElement container) {
        final JsArray<JavaScriptObject> jsComponents = nativeGetAfComponents(container);

        final Set<HTMLElement> components = new HashSet<>();
        for (int i = 0; i < jsComponents.length(); i++) {
            components.add(Js.cast(jsComponents.get(i)));
        }

        return components;
    }

    public void onClose() {
        run("af_onClose");
    }

    public void onClose(final HTMLElement container) {
        onClose();
        unmount(container);
    }

    public void onShutdown() {
        run("af_onShutdown");
    }

    private native void unmount(final HTMLElement container) /*-{
        if (this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective::self.af_isReact) {
            $wnd.ReactDOM.unmountComponentAtNode(container);
        }
    }-*/;

    private native JsArray<JavaScriptObject> nativeGetAfComponents(final HTMLElement container) /*-{
        return $wnd._AppFormerUtils.findChildContainers(container);
    }-*/;

    public native void renderNative(final HTMLElement container) /*-{
        $wnd.AppFormer.render(
                this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective::self.af_componentRoot(),
                container);
    }-*/;

    private native Object get(final String fieldToInvoke)   /*-{
        return this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective::self[fieldToInvoke];
    }-*/;

    private native Object run(final String method)   /*-{
        return this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective::self[method]();
    }-*/;
}
