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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Subscription;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.marshalling.client.Marshalling;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.jsbridge.JsWorkbenchLazyActivity;
import org.uberfire.jsbridge.client.JsPlaceRequest;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JsWorkbenchScreenActivity extends AbstractWorkbenchScreenActivity implements JsWorkbenchLazyActivity {

    private InvocationPostponer invocationsPostponer;

    private PlaceRequest place;
    private JsNativeScreen screen;
    List<Subscription> subscriptions;

    public JsWorkbenchScreenActivity(final JsNativeScreen screen,
                                     final PlaceManager placeManager) {

        super(placeManager);
        this.screen = screen;
        this.subscriptions = new ArrayList<>();
        this.invocationsPostponer = new InvocationPostponer();
    }

    @Override
    public void updateRealContent(final JavaScriptObject jsObject) {
        this.screen.updateRealContent(jsObject);
        this.invocationsPostponer.executeAll();
    }

    // Lifecycle

    @Override
    public void onStartup(final PlaceRequest place) {

        this.place = place;

        if (!isScreenLoaded()) {
            invocationsPostponer.postpone(() -> this.onStartup(place));
            return;
        }

        this.registerSubscriptions();
        screen.onStartup(JsPlaceRequest.fromPlaceRequest(place));
    }

    public boolean isScreenLoaded() {
        return screen.screenLoaded();
    }

    @Override
    public void onOpen() {

        // render no matter if the script was loaded or not, even if the call results in a blank screen being rendered.
        screen.render();

        if (!isScreenLoaded()) {
            invocationsPostponer.postpone(this::onOpen);
            return;
        }

        screen.onOpen();
        placeManager.executeOnOpenCallbacks(place);
    }

    @Override
    public void onClose() {

        if (isScreenLoaded()) {
            screen.onClose();
        }

        placeManager.executeOnCloseCallbacks(place);
    }

    @Override
    public boolean onMayClose() {

        if (isScreenLoaded()) {
            return screen.onMayClose();
        }

        return true;
    }

    @Override
    public void onShutdown() {

        this.invocationsPostponer.clear();

        if (isScreenLoaded()) {
            this.unsubscribeFromAllEvents();
            screen.onShutdown();
        }
    }

    @Override
    public void onFocus() {
        if (isScreenLoaded()) {
            screen.onFocus();
        }
    }

    @Override
    public void onLostFocus() {
        if (isScreenLoaded()) {
            screen.onLostFocus();
        }
    }

    // Properties

    @Override
    public String getTitle() {
        return screen.componentTitle();
    }

    @Override
    public Position getDefaultPosition() {
        return CompassPosition.ROOT;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String getIdentifier() {
        return screen.getComponentId();
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public void getMenus(final Consumer<Menus> consumer) {
        consumer.accept(null);
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public PlaceRequest getOwningPlace() {
        return null;
    }

    @Override
    public IsWidget getWidget() {
        return ElementWrapperWidget.getWidget(screen.getElement());
    }

    @Override
    public String contextId() {
        return screen.componentContextId();
    }

    @Override
    public int preferredHeight() {
        return -1;
    }

    @Override
    public int preferredWidth() {
        return -1;
    }

    // CDI Events Subscriptions

    void registerSubscriptions() {
        screen.subscriptions().forEach(this::registerSubscription);
    }

    //TODO: Parent classes of "eventFqcn" should be subscribed to as well?
    //FIXME: Marshall/unmarshall is happening twice
    Void registerSubscription(final Object callback, final String eventFqcn, final Object obj) {
        subscriptions.add(getSubscription(callback, eventFqcn));
        subscribeOnErraiBus(eventFqcn);
        return null;
    }

    void subscribeOnErraiBus(final String eventFqcn) {
        ErraiBus.get().subscribe("cdi.event:" + eventFqcn, CDI.ROUTING_CALLBACK);
    }

    Subscription getSubscription(final Object callback, final String eventFqcn) {
        return CDI.subscribe(eventFqcn, new AbstractCDIEventCallback<Object>() {
            public void fireEvent(final Object event) {
                callWithParsedJsonObject(callback, Marshalling.toJSON(event));
            }
        });
    }

    public native void callWithParsedJsonObject(final Object func, final String jsonArg) /*-{
        func(JSON.parse(jsonArg));
    }-*/;

    private void unsubscribeFromAllEvents() {
        this.subscriptions.forEach(Subscription::remove);
        this.subscriptions = new ArrayList<>();
    }
}
