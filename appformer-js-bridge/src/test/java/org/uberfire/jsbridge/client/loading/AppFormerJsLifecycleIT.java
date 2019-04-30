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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.mvp.jsbridge.JsWorkbenchLazyActivity;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.jsbridge.client.AppFormerJsBridge;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;
import org.uberfire.jsbridge.client.screen.JsWorkbenchScreenActivity;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.Type.EDITOR;
import static org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.Type.PERSPECTIVE;
import static org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.Type.SCREEN;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, CDI.class, Marshalling.class})
public class AppFormerJsLifecycleIT {

    @Mock
    private ActivityManager activityManager;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private TranslationService translationService;

    @Mock
    private LazyLoadingScreen lazyLoadingScreen;

    @Mock
    private EventSourceMock<ActivityLazyLoaded> eventSourceMock;

    @Mock
    private Workbench workbench;

    private SyncPromises promises = new SyncPromises();

    private AppFormerComponentsRegistry registry = spy(new AppFormerComponentsRegistry());

    private AppFormerJsActivityLoader appFormerJsActivityLoader;

    private AppFormerJsBridge appFormerJsBridge;

    @Before
    public void before() {
        appFormerJsActivityLoader = spy(new AppFormerJsActivityLoader(
                promises,
                activityManager,
                activityBeansCache,
                placeManager,
                lazyLoadingScreen,
                eventSourceMock,
                null,
                registry));

        appFormerJsBridge = spy(new AppFormerJsBridge(workbench, appFormerJsActivityLoader, promises));
    }

    @Test
    public void testGoTo() {
        doNothing().when(placeManager).goTo((PlaceRequest) any());
        final SyncBeanManager beanManager = ensureSetupBeanManager();
        ensureBeanManagerHas(beanManager, PlaceManager.class, placeManager);

        appFormerJsBridge.goTo("SomePlace");

        verify(placeManager).goTo((PlaceRequest) any());
    }

    @Test
    public void testGoToPath() {
        doNothing().when(placeManager).goTo((Path) any());
        final SyncBeanManager beanManager = ensureSetupBeanManager();
        ensureBeanManagerHas(beanManager, PlaceManager.class, placeManager);

        appFormerJsBridge.goToPath("SomeUri");

        verify(placeManager).goTo((Path) any());
    }

    @Test
    public void testFireEvent() {
        PowerMockito.mockStatic(CDI.class);
        PowerMockito.doNothing().when(CDI.class);
        CDI.fireEvent(any());

        PowerMockito.mockStatic(Marshalling.class);
        PowerMockito.when(Marshalling.fromJSON((String) any())).thenReturn("MarshalledEvent");

        appFormerJsBridge.fireEvent("{\"foo\": \"bar\"}");

        PowerMockito.verifyStatic(Marshalling.class);
        Marshalling.fromJSON("{\"foo\": \"bar\"}");
        PowerMockito.verifyStatic(CDI.class);
        CDI.fireEvent("MarshalledEvent");
    }

    @Test
    public void testTranslate() {
        doReturn("translated!").when(translationService).format("key");
        final SyncBeanManager beanManager = ensureSetupBeanManager();
        ensureBeanManagerHas(beanManager, TranslationService.class, translationService);

        final String translated = appFormerJsBridge.translate("key", new String[]{});

        assertEquals("translated!", translated);
        verify(translationService).format("key");
    }

    @Test
    public void testInitWithErrorWhileLoadingScripts() {
        ensureBridgeWillBeExposed();
        ensureScriptWontLoadForModule("ModuleWithError");

        appFormerJsBridge.init("ModuleWithError");

        verify(workbench).addStartupBlocker(AppFormerJsBridge.class);
        verify(appFormerJsActivityLoader, never()).init(anyString());
        verify(workbench).removeStartupBlocker(AppFormerJsBridge.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScreenLifecycle() {
        final ArgumentCaptor<SingletonBeanDefinition> captor = ArgumentCaptor.forClass(SingletonBeanDefinition.class);

        //Init
        ensureComponentIsInAppFormerComponentsRegistry("foo-screen", SCREEN);
        ensureDomGlobalCanCreateDivs();
        ensureScriptsWillLoadForModule("ScreenTestModule");
        ensureBridgeWillBeExposed();
        final SyncBeanManager beanManager = ensureSetupBeanManager();

        appFormerJsBridge.init("ScreenTestModule");

        verify(workbench).addStartupBlocker(AppFormerJsBridge.class);
        verify(appFormerJsActivityLoader).init("ScreenTestModule");
        verify(appFormerJsActivityLoader).registerScreen(any());
        verify(activityBeansCache).addNewScreenActivity(any());
        verify(beanManager).registerBean(captor.capture());
        verify(beanManager).registerBeanTypeAlias(any(), eq(WorkbenchScreenActivity.class));
        verify(beanManager).registerBeanTypeAlias(any(), eq(JsWorkbenchLazyActivity.class));
        verify(beanManager).registerBeanTypeAlias(any(), eq(Activity.class));
        verify(workbench).removeStartupBlocker(AppFormerJsBridge.class);

        //Register Screen
        final JsWorkbenchScreenActivity activity = (JsWorkbenchScreenActivity) spy(captor.getValue().getInstance());
        final JavaScriptObject screenJsObject = ensureActivityRepresentsRegisteredComponent(activity, "foo-screen");
        ensureBeanManagerHas(beanManager, AppFormerJsActivityLoader.class, appFormerJsActivityLoader);

        appFormerJsBridge.registerScreen(screenJsObject);

        verify(appFormerJsActivityLoader).onComponentLoaded(screenJsObject);
        verify(activity).updateRealContent(screenJsObject);
        assertTrue(activity.isScreenLoaded());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPerspectiveLifecycle() {
        final ArgumentCaptor<SingletonBeanDefinition> captor = ArgumentCaptor.forClass(SingletonBeanDefinition.class);

        //Init
        ensureComponentIsInAppFormerComponentsRegistry("foo-perspective", PERSPECTIVE);
        ensureDomGlobalCanCreateDivs();
        ensureScriptsWillLoadForModule("PerspectiveTestModule");
        ensureBridgeWillBeExposed();
        final SyncBeanManager beanManager = ensureSetupBeanManager();
        ensureBeanManagerHas(beanManager, ActivityBeansCache.class, activityBeansCache);
        ensureBeanManagerHas(beanManager, PlaceManager.class, placeManager);
        ensureBeanManagerHas(beanManager, ActivityManager.class, activityManager);
        ensureBeanManagerHasBeansForName(beanManager, "foo-perspective");

        appFormerJsBridge.init("PerspectiveTestModule");

        verify(workbench).addStartupBlocker(AppFormerJsBridge.class);
        verify(appFormerJsActivityLoader).init("PerspectiveTestModule");
        verify(appFormerJsActivityLoader).registerPerspective(any());
        verify(beanManager).registerBean(captor.capture());
        verify(beanManager).registerBeanTypeAlias(any(), eq(PerspectiveActivity.class));
        verify(beanManager).registerBeanTypeAlias(any(), eq(JsWorkbenchLazyActivity.class));
        verify(beanManager).registerBeanTypeAlias(any(), eq(Activity.class));
        verify(activityBeansCache).addNewPerspectiveActivity(any());
        verify(workbench).removeStartupBlocker(AppFormerJsBridge.class);

        //Register Perspective
        final JsWorkbenchLazyPerspectiveActivity activity = (JsWorkbenchLazyPerspectiveActivity) spy(captor.getValue().getInstance());
        final JavaScriptObject screenJsObject = ensureActivityRepresentsRegisteredComponent(activity, "foo-perspective");
        ensureBeanManagerHas(beanManager, AppFormerJsActivityLoader.class, appFormerJsActivityLoader);
        ensurePerspectiveIsNotTemplated(activity);

        appFormerJsBridge.registerPerspective(screenJsObject);

        verify(appFormerJsActivityLoader).onComponentLoaded(screenJsObject);
        verify(activity).updateRealContent(screenJsObject);
        assertTrue(activity.isPerspectiveLoaded());
    }

    @Test
    public void registerEditor() {

        //Init
        ensureComponentIsInAppFormerComponentsRegistry("foo-editor", EDITOR);
        ensureDomGlobalCanCreateDivs();
        ensureScriptsWillLoadForModule("EditorTestModule");
        ensureBridgeWillBeExposed();

        appFormerJsBridge.init("EditorTestModule");

        verify(workbench).addStartupBlocker(AppFormerJsBridge.class);
        verify(appFormerJsActivityLoader).init("EditorTestModule");
        verify(appFormerJsActivityLoader).registerEditor(any());
        assertTrue(appFormerJsActivityLoader.editors.containsKey("foo-editor"));
        verify(workbench).removeStartupBlocker(AppFormerJsBridge.class);

        //Registering Editor is still not supported, see https://issues.jboss.org/browse/AF-1882
    }

    private void ensurePerspectiveIsNotTemplated(JsWorkbenchLazyPerspectiveActivity activity) {
        doReturn(false).when(activity).isPerspectiveTemplated(any());
    }

    private void ensureBeanManagerHasBeansForName(final SyncBeanManager beanManager, final String name) {
        doReturn(singletonList(mock(SyncBeanDef.class))).when(beanManager).lookupBeans(name);
    }

    private void ensureBridgeWillBeExposed() {
        doNothing().when(appFormerJsBridge).exposeBridgeAsNativeJs();
    }

    private void ensureScriptsWillLoadForModule(final String module) {
        doReturn(promises.resolve()).when(appFormerJsBridge).loadAppFormerJsAndReactScripts(module);
    }

    private void ensureScriptWontLoadForModule(final String module) {
        doReturn(promises.reject(null)).when(appFormerJsBridge).loadAppFormerJsAndReactScripts(module);
    }

    private JavaScriptObject ensureActivityRepresentsRegisteredComponent(final Activity activity, final String id) {
        final JavaScriptObject screenJsObject = mock(JavaScriptObject.class);
        doReturn(id).when(appFormerJsActivityLoader).extractComponentId(screenJsObject);
        doReturn(activity).when(activityManager).getActivity(any());
        return screenJsObject;
    }

    private void ensureBeanManagerHas(final SyncBeanManager beanManager, final Class<?> clazz, final Object bean) {
        final SyncBeanDef syncBeanDef = mock(SyncBeanDef.class);
        doReturn(bean).when(syncBeanDef).getInstance();
        doReturn(syncBeanDef).when(beanManager).lookupBean(clazz);
    }

    private void ensureDomGlobalCanCreateDivs() {
        doReturn(nativeScreenContainerDiv()).when(mockDomGlobalDocument()).createElement(any());
    }

    private SyncBeanManager ensureSetupBeanManager() {
        PowerMockito.mockStatic(IOC.class);
        SyncBeanManager beanManager = mock(SyncBeanManager.class);
        PowerMockito.when(IOC.getBeanManager()).thenReturn(beanManager);
        doNothing().when(beanManager).registerBean(any());
        doNothing().when(beanManager).registerBeanTypeAlias(any(), any());
        return beanManager;
    }

    private void ensureComponentIsInAppFormerComponentsRegistry(final String id, final AppFormerComponentsRegistry.Entry.Type type) {
        doReturn(new String[]{id}).when(registry).keys();
        final AppFormerComponentsRegistry.Entry entry = spy(new AppFormerComponentsRegistry.Entry(id, mock(JavaScriptObject.class)));
        doReturn(type).when(entry).getType();
        doReturn("").when(entry).getSource();
        doReturn(new HashMap<>()).when(entry).getParams();
        doReturn(entry).when(appFormerJsActivityLoader).newRegistryEntry(eq(id));
    }

    private HTMLDivElement nativeScreenContainerDiv() {
        final HTMLDivElement div = mock(HTMLDivElement.class);
        div.classList = mock(DOMTokenList.class);
        return div;
    }

    private HTMLDocument mockDomGlobalDocument() {
        try {
            final Field document = DomGlobal.class.getDeclaredField("document");
            document.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(document, document.getModifiers() & ~Modifier.FINAL);

            final HTMLDocument mock = mock(HTMLDocument.class);
            document.set(null, mock);
            return mock;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
