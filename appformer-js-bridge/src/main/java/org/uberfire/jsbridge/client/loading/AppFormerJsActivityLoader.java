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
package org.uberfire.jsbridge.client.loading;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Qualifier;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.mvp.jsbridge.JsWorkbenchLazyActivity;
import org.uberfire.client.promise.Promises;
import org.uberfire.jsbridge.client.cdi.EditorActivityBeanDefinition;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;
import org.uberfire.jsbridge.client.editor.JsNativeEditor;
import org.uberfire.jsbridge.client.editor.JsWorkbenchEditorActivity;
import org.uberfire.jsbridge.client.screen.JsNativeScreen;
import org.uberfire.jsbridge.client.screen.JsWorkbenchScreenActivity;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@EntryPoint
public class AppFormerJsActivityLoader implements PlaceManagerImpl.AppFormerActivityLoader {

    private final Promises promises;
    private final ActivityManager activityManager;
    private final ActivityBeansCache activityBeansCache;
    private final PlaceManager placeManager;
    private final LazyLoadingScreen lazyLoadingScreen;
    private final Event<ActivityLazyLoaded> activityLazyLoadedEvent;
    private final Instance<JsWorkbenchEditorActivity> jsWorkbenchEditorActivityInstance;
    private final AppFormerComponentsRegistry appFormerComponentsRegistry;

    private final Map<String, String> components = new HashMap<>();
    private final Set<String> loadedScripts = new HashSet<>();
    final Map<String, AppFormerComponentsRegistry.Entry> editors = new HashMap<>();

    private String gwtModuleName;

    @Inject
    public AppFormerJsActivityLoader(final Promises promises,
                                     final ActivityManager activityManager,
                                     final ActivityBeansCache activityBeansCache,
                                     final PlaceManager placeManager,
                                     final LazyLoadingScreen lazyLoadingScreen,
                                     final Event<ActivityLazyLoaded> activityLazyLoadedEvent,
                                     final @Shadowed Instance<JsWorkbenchEditorActivity> jsWorkbenchEditorActivityInstance,
                                     final AppFormerComponentsRegistry appFormerComponentsRegistry) {

        this.promises = promises;
        this.activityManager = activityManager;
        this.activityBeansCache = activityBeansCache;
        this.placeManager = placeManager;
        this.lazyLoadingScreen = lazyLoadingScreen;
        this.activityLazyLoadedEvent = activityLazyLoadedEvent;
        this.jsWorkbenchEditorActivityInstance = jsWorkbenchEditorActivityInstance;
        this.appFormerComponentsRegistry = appFormerComponentsRegistry;
    }

    public void init(final String gwtModuleName) {
        this.gwtModuleName = gwtModuleName;

        stream(appFormerComponentsRegistry.keys())
                .map(this::newRegistryEntry)
                .forEach(this::registerComponent);
    }

    AppFormerComponentsRegistry.Entry newRegistryEntry(final String componentId) {
        return new AppFormerComponentsRegistry.Entry(componentId, appFormerComponentsRegistry.get(componentId));
    }

    public void onComponentLoaded(final Object jsObject) {

        final String componentId = extractComponentId(jsObject);

        if (editors.containsKey(componentId)) {
            registerEditor(jsObject, componentId);
            return;
        }

        if (!components.containsKey(componentId)) {
            throw new IllegalArgumentException("Cannot find component " + componentId);
        }

        final Activity activity = updateRealContent((JavaScriptObject) jsObject, componentId);

        activityLazyLoadedEvent.fire(new ActivityLazyLoaded(componentId, activity));
    }

    Activity updateRealContent(final JavaScriptObject jsObject,
                               final String componentId) {

        //FIXME: Get activity bean from BeanManager to prevent onStartup to be invoked.
        final Activity activity = activityManager.getActivity(new DefaultPlaceRequest(componentId));

        final JsWorkbenchLazyActivity lazyActivity = (JsWorkbenchLazyActivity) activity;
        lazyActivity.updateRealContent(jsObject);
        return activity;
    }

    public native String extractComponentId(final Object object)  /*-{
        return object['af_componentId'];
    }-*/;

    Promise<Void> loadScriptFor(final String componentId) {

        final Optional<String> scriptFilename = getScriptFileName(componentId);

        //Script not found
        if (!scriptFilename.isPresent()) {
            throw new RuntimeException("No script found for " + componentId);
        }

        //Already loaded
        if (loadedScripts.contains(scriptFilename.get())) {
            return promises.resolve();
        }

        loadedScripts.add(scriptFilename.get());

        return loadScript(gwtModuleName + "/" + scriptFilename.get()).catch_(e -> {
            DomGlobal.console.info("Error loading script for " + componentId);
            loadedScripts.remove(scriptFilename.get());
            return promises.reject(e);
        });
    }

    Optional<String> getScriptFileName(final String componentId) {

        final Optional<String> editorScriptUrl = ofNullable(editors.get(componentId))
                .map(AppFormerComponentsRegistry.Entry::getSource);

        return editorScriptUrl.isPresent()
                ? editorScriptUrl
                : ofNullable(components.get(componentId));
    }

    Promise<Void> loadScript(final String scriptUrl) {
        return promises.create((res, rej) -> ScriptInjector.fromUrl(scriptUrl)
                .setWindow(TOP_WINDOW)
                .setCallback(getScriptInjectionCallback(res, rej))
                .inject());
    }

    private Callback<Void, Exception> getScriptInjectionCallback(final ResolveCallbackFn<Void> res,
                                                                 final RejectCallbackFn rej) {
        return new Callback<Void, Exception>() {
            @Override
            public void onFailure(final Exception e1) {
                rej.onInvoke(e1);
            }

            @Override
            public void onSuccess(final Void v) {
                res.onInvoke(v);
            }
        };
    }

    void registerComponent(final AppFormerComponentsRegistry.Entry registryEntry) {
        switch (registryEntry.getType()) {
            case PERSPECTIVE:
                registerPerspective(registryEntry);
                components.put(registryEntry.getComponentId(), registryEntry.getSource());
                break;
            case SCREEN:
                registerScreen(registryEntry);
                components.put(registryEntry.getComponentId(), registryEntry.getSource());
                break;
            case EDITOR:
                registerEditor(registryEntry);
                break;
        }
    }

    public boolean triggerLoadOfMatchingEditors(final Path path,
                                                final Runnable successCallback) {

        if (path == null || path.toURI() == null) {
            return false;
        }

        final List<Promise<Void>> loadingMatchingEditors = loadMatchingEditors(path.toURI());

        if (loadingMatchingEditors.size() <= 0) {
            return false;
        }

        finishLoadingMatchingEditors(loadingMatchingEditors, successCallback);
        return true;
    }

    List<Promise<Void>> loadMatchingEditors(final String uri) {
        return editors.values().stream()
                .filter(e -> e.matches(uri))
                .filter(e -> !loadedScripts.contains(e.getSource()))
                .map(e -> this.loadScriptFor(e.getComponentId()))
                .collect(toList());
    }

    protected void finishLoadingMatchingEditors(final List<Promise<Void>> loadingMatchingEditors,
                                                final Runnable successCallback) {

        this.promises.resolve().then(i -> promises.all(loadingMatchingEditors, identity()).then(s -> {
            successCallback.run();
            return this.promises.resolve();
        })).catch_(e -> {
            //If something goes wrong, it's a no-op.
            return this.promises.resolve();
        });
    }

    void registerEditor(final AppFormerComponentsRegistry.Entry registryEntry) {
        this.editors.put(registryEntry.getComponentId(), registryEntry);
    }

    @SuppressWarnings("unchecked")
    void registerScreen(final AppFormerComponentsRegistry.Entry registryEntry) {

        final JsNativeScreen newScreen = new JsNativeScreen(registryEntry.getComponentId(), this::loadScriptFor, lazyLoadingScreen);
        final JsWorkbenchScreenActivity activity = new JsWorkbenchScreenActivity(newScreen, placeManager);

        //FIXME: Check if this bean is being registered correctly. Startup/Shutdown is begin called as if they were Open/Close.
        final SingletonBeanDefinition activityBean = new SingletonBeanDefinition<>(
                activity,
                JsWorkbenchScreenActivity.class,
                new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                activity.getIdentifier(),
                true,
                WorkbenchScreenActivity.class,
                JsWorkbenchLazyActivity.class,
                Activity.class);

        activityBeansCache.addNewScreenActivity(activityBean);

        final SyncBeanManager beanManager = IOC.getBeanManager();
        beanManager.registerBean(activityBean);
        beanManager.registerBeanTypeAlias(activityBean, WorkbenchScreenActivity.class);
        beanManager.registerBeanTypeAlias(activityBean, JsWorkbenchLazyActivity.class);
        beanManager.registerBeanTypeAlias(activityBean, Activity.class);
    }

    @SuppressWarnings("unchecked")
    void registerPerspective(final AppFormerComponentsRegistry.Entry registryEntry) {

        final SyncBeanManager beanManager = IOC.getBeanManager();
        final ActivityBeansCache activityBeansCache = beanManager.lookupBean(ActivityBeansCache.class).getInstance();

        final PlaceManager placeManager = beanManager.lookupBean(PlaceManager.class).getInstance();
        final ActivityManager activityManager = beanManager.lookupBean(ActivityManager.class).getInstance();

        final JsWorkbenchLazyPerspectiveActivity activity = new JsWorkbenchLazyPerspectiveActivity(registryEntry,
                                                                                                   placeManager,
                                                                                                   activityManager,
                                                                                                   this::loadScriptFor);

        final SingletonBeanDefinition<JsWorkbenchLazyPerspectiveActivity, JsWorkbenchLazyPerspectiveActivity> activityBean = new SingletonBeanDefinition<>(
                activity,
                JsWorkbenchLazyPerspectiveActivity.class,
                new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                activity.getIdentifier(),
                true,
                PerspectiveActivity.class,
                JsWorkbenchLazyActivity.class,
                Activity.class);

        beanManager.registerBean(activityBean);
        beanManager.registerBeanTypeAlias(activityBean, PerspectiveActivity.class);
        beanManager.registerBeanTypeAlias(activityBean, JsWorkbenchLazyActivity.class);
        beanManager.registerBeanTypeAlias(activityBean, Activity.class);

        activityBeansCache.addNewPerspectiveActivity(beanManager.lookupBeans(((PerspectiveActivity) activity).getIdentifier()).iterator().next());
    }

    @Qualifier
    public @interface Shadowed {

    }

    @SuppressWarnings("unchecked")
    void registerEditor(final Object jsObject,
                        final String componentId) {

        final JsNativeEditor editor = new JsNativeEditor(componentId, jsObject);

        final SyncBeanManager beanManager = IOC.getBeanManager();
        final EditorActivityBeanDefinition activityBean = new EditorActivityBeanDefinition<>(
                () -> jsWorkbenchEditorActivityInstance.get().withEditor(new JsNativeEditor(componentId, jsObject))
        );

        beanManager.registerBean(activityBean);
        beanManager.registerBeanTypeAlias(activityBean, WorkbenchEditorActivity.class);
        beanManager.registerBeanTypeAlias(activityBean, Activity.class);

        activityBeansCache.addNewEditorActivity(activityBean,
                                                editor.af_priority(),
                                                Arrays.asList(editor.af_resourceTypes()));
    }
}
