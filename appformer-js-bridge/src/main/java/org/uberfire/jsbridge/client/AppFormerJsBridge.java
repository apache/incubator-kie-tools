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

package org.uberfire.jsbridge.client;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import elemental2.dom.DomGlobal;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static java.util.Arrays.stream;

@Dependent
public class AppFormerJsBridge {

    private final Workbench workbench;
    private final AppFormerJsActivityLoader appFormerJsLoader;
    private final Promises promises;

    @Inject
    public AppFormerJsBridge(final Workbench workbench,
                             final AppFormerJsActivityLoader appFormerJsLoader,
                             final Promises promises) {

        this.workbench = workbench;
        this.appFormerJsLoader = appFormerJsLoader;
        this.promises = promises;
    }

    public void init(final String gwtModuleName) {

        workbench.addStartupBlocker(AppFormerJsBridge.class);

        exposeBridgeAsNativeJs();

        loadAppFormerJsAndReactScripts(gwtModuleName).then(i -> {
            try {
                appFormerJsLoader.init(gwtModuleName);
            } finally {
                workbench.removeStartupBlocker(AppFormerJsBridge.class);
            }

            return promises.resolve();
        }).catch_(e -> {
            workbench.removeStartupBlocker(AppFormerJsBridge.class);
            return promises.resolve();
        });
    }

    public void exposeBridgeAsNativeJs() {
        exposeBridge();
    }

    public Promise<Void> loadAppFormerJsAndReactScripts(final String gwtModuleName) {
        return promises.create((res, rej) -> {

            final Consumer<Exception> onError = ex -> {
                workbench.removeStartupBlocker(AppFormerJsBridge.class);
                rej.onInvoke(null);
            };

            final CallbackProducer<Void> callback = new CallbackProducer<>(onError);
            ScriptInjector.fromUrl(gwtModuleName + "/react.production.min.js")
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .setCallback(callback.withSuccess((v) -> ScriptInjector.fromUrl(gwtModuleName + "/react-dom.production.min.js")
                            .setWindow(ScriptInjector.TOP_WINDOW)
                            .setCallback(callback.withSuccess((v1) -> ScriptInjector.fromUrl(gwtModuleName + "/appformer.js")
                                    .setWindow(ScriptInjector.TOP_WINDOW)
                                    .setCallback(callback.withSuccess((v2) -> ScriptInjector.fromUrl(gwtModuleName + "/AppFormerComponentsRegistry.js")
                                            .setWindow(ScriptInjector.TOP_WINDOW)
                                            .setCallback(callback.withSuccess((v3) -> {
                                                res.onInvoke((IThenable<Void>) null);
                                            }))
                                            .inject()))
                                    .inject()))
                            .inject()))
                    .inject();
        });
    }

    private native void exposeBridge() /*-{
        $wnd.appformerGwtBridge = {
            registerScreen: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::registerScreen(Ljava/lang/Object;),
            registerPerspective: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::registerPerspective(Ljava/lang/Object;),
            goTo: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::goTo(Ljava/lang/String;),
            goToPath: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::goToPath(Ljava/lang/String;),
            rpc: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::rpc(Ljava/lang/String;[Ljava/lang/Object;),
            translate: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::translate(Ljava/lang/String;[Ljava/lang/Object;),
            fireEvent: this.@org.uberfire.jsbridge.client.AppFormerJsBridge::fireEvent(Ljava/lang/String;),
            render: function (component, container, callback) {
                if (component instanceof HTMLElement) {
                    container.innerHTML = "";
                    container.appendChild(component);
                    callback();
                } else if (typeof component === "string") {
                    container.innerHTML = component;
                    callback();
                } else {
                    $wnd.ReactDOM.render(component, container, callback);
                }
            }
        };
    }-*/;

    public void fireEvent(final String eventJson) {
        CDI.fireEvent(Marshalling.fromJSON(eventJson));
    }

    public void goTo(final String place) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final PlaceManager placeManager = beanManager.lookupBean(PlaceManager.class).getInstance();
        placeManager.goTo(new DefaultPlaceRequest(place));
    }

    public void goToPath(final String uri) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final PlaceManager placeManager = beanManager.lookupBean(PlaceManager.class).getInstance();
        final PathFactory.PathImpl path = new PathFactory.PathImpl(uri.split("//")[uri.split("//").length - 1], uri); //TODO: Un-expose PathImpl constructor?
        placeManager.goTo(path);
    }

    public String translate(final String key, final Object[] args) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final TranslationService translationService = beanManager.lookupBean(TranslationService.class).getInstance();
        return translationService.format(key, args);
    }

    public void registerPerspective(final Object jsObject) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final AppFormerJsActivityLoader jsLoader = beanManager.lookupBean(AppFormerJsActivityLoader.class).getInstance();
        jsLoader.onComponentLoaded(jsObject);
    }

    public void registerScreen(final Object jsObject) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final AppFormerJsActivityLoader jsLoader = beanManager.lookupBean(AppFormerJsActivityLoader.class).getInstance();
        jsLoader.onComponentLoaded(jsObject);
    }

    public Promise<Object> rpc(final String path, final Object[] params) {

        //TODO: Marshall/unmarshall is happening twice?

        final String[] parts = path.split("\\|");
        final String serviceFqcn = parts[0];
        final String method = parts[1];
        final Annotation[] qualifiers = {}; //TODO: Support qualifiers?

        final Function<Object, Object> jsonToGwt = object -> {
            try {
                return Marshalling.fromJSON((String) object);
            } catch (final Exception e) {
                DomGlobal.console.info("Error converting JS obj to GWT obj", e);
                throw e;
            }
        };

        final Function<Object, Object> gwtToJson = value -> value != null
                ? Marshalling.toJSON(value)
                : null;

        final Object[] gwtParams = stream(params).map(jsonToGwt).toArray();

        return new Promise<>((res, rej) -> MessageBuilder.createCall()
                .call(serviceFqcn)
                .endpoint(method, qualifiers, gwtParams)
                .respondTo(Object.class, value -> res.onInvoke(gwtToJson.apply(value)))
                .errorsHandledBy((e, a) -> {
                    rej.onInvoke(e);
                    return true;
                })
                .sendNowWith(ErraiBus.get()));
    }

    private static class CallbackProducer<T> {

        private final Consumer<Exception> onFailure;

        CallbackProducer(final Consumer<Exception> onFailure) {
            this.onFailure = onFailure;
        }

        Callback<T, Exception> withSuccess(final Consumer<T> onSuccess) {
            return new Callback<T, Exception>() {
                @Override
                public void onFailure(Exception e) {
                    onFailure.accept(e);
                }

                @Override
                public void onSuccess(T t) {
                    onSuccess.accept(t);
                }
            };
        }
    }
}
