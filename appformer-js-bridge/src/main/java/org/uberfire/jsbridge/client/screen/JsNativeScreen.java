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

package org.uberfire.jsbridge.client.screen;

import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.uberfire.jsbridge.client.JsPlaceRequest;
import org.uberfire.jsbridge.client.loading.LazyLoadingScreen;

public class JsNativeScreen {

    private JavaScriptObject self;
    private final String componentId;
    private final HTMLElement container;

    private final Consumer<String> lazyLoadParentScript;
    private boolean loaded;

    private final Elemental2DomUtil elemental2DomUtil;

    public JsNativeScreen(final String componentId,
                          final Consumer<String> lazyLoadParentScript,
                          final LazyLoadingScreen lazyScreen) {

        this.loaded = false;
        this.componentId = componentId;

        this.elemental2DomUtil = new Elemental2DomUtil();
        this.container = createContainerForLoadingScreen(lazyScreen.getElement());

        this.lazyLoadParentScript = lazyLoadParentScript;
    }

    public void updateRealContent(final JavaScriptObject jsObject) {
        loaded = true;
        self = jsObject;

        // reset container content's, removing the loading content
        elemental2DomUtil.removeAllElementChildren(container);
    }

    public HTMLElement getElement() {
        //This is just a placeholder. This empty div will passed to the JS component so it knows where to render at.
        return container;
    }

    public void render() {
        if (screenLoaded()) {
            renderNative();
        } else {
            lazyLoadParentScript.accept(componentId);
        }
    }

    public boolean screenLoaded() {
        return loaded;
    }

    private HTMLElement createContainerForLoadingScreen(final HTMLElement loadingWidget) {

        final HTMLElement container = (HTMLElement) DomGlobal.document.createElement("div");
        container.classList.add("js-screen-container");

        // while loading, this screen will render the loadingWidget's content
        container.appendChild(loadingWidget);

        return container;
    }

    // Properties

    public String componentTitle() {
        final String title = (String) get("af_componentTitle");
        return title != null ? title : getComponentId();
    }

    public String componentContextId() {
        return (String) get("af_componentContextId");
    }

    public elemental2.core.JsMap<String, Object> subscriptions() {
        return Js.cast(get("af_subscriptions"));
    }

    public String getComponentId() {
        return componentId;
    }

    // Lifecycle

    public void onStartup(final JsPlaceRequest placeRequest) {
        run("af_onStartup", placeRequest);
    }

    public void onOpen() {
        run("af_onOpen");
    }

    public void onClose() {
        run("af_onClose");
        unmount();
    }

    public boolean onMayClose() {
        return !defines("af_onMayClose") || (boolean) run("af_onMayClose");
    }

    public void onShutdown() {
        run("af_onShutdown");
    }

    public void onFocus() {
        run("af_onFocus");
    }

    public void onLostFocus() {
        run("af_onLostFocus");
    }

    private Object get(final String property) {
        if (!screenLoaded()) {
            return null;
        }
        return getNative(property);
    }

    private Object run(final String functionName) {
        if (!screenLoaded()) {
            return null;
        }
        return runNative(functionName);
    }

    private Object run(final String functionName, final Object arg1) {
        if (!screenLoaded()) {
            return null;
        }
        return runNative(functionName, arg1);
    }

    public boolean defines(final String property) {
        if (!screenLoaded()) {
            return false;
        }
        return definesNative(property);
    }

    private native Object getNative(final String property)  /*-{
        return this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[property];
    }-*/;

    private native Object runNative(final String functionName) /*-{
        return this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[functionName] && this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[functionName]();
    }-*/;

    private native Object runNative(final String functionName, final Object arg1) /*-{
        return this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[functionName] && this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[functionName](arg1);
    }-*/;

    private native boolean definesNative(final String property) /*-{
        return this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self[property] !== undefined;
    }-*/;

    public native void renderNative() /*-{
        $wnd.AppFormer.render(
                this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self.af_componentRoot(),
                this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::container);
    }-*/;

    private native void unmount() /*-{
        if (this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::self.af_isReact) {
            $wnd.ReactDOM.unmountComponentAtNode(this.@org.uberfire.jsbridge.client.screen.JsNativeScreen::container);
        }
    }-*/;
}
