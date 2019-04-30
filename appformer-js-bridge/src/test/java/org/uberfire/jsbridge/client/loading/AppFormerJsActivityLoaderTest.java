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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.inject.Instance;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.promise.Promise;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.jsbridge.JsWorkbenchLazyActivity;
import org.uberfire.jsbridge.client.screen.JsWorkbenchScreenActivity;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.Type.EDITOR;
import static org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry.Type.SCREEN;

@RunWith(MockitoJUnitRunner.class)
public class AppFormerJsActivityLoaderTest {

    private AppFormerJsActivityLoader appFormerJsActivityLoader;

    @Mock
    private AppFormerComponentsRegistry appFormerComponentsRegistry;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private EventSourceMock<ActivityLazyLoaded> activityLazyLoadedEvent;

    private SyncPromises promises = new SyncPromises();

    @Before
    @SuppressWarnings("unchecked")
    public void before() {
        appFormerJsActivityLoader = spy(new AppFormerJsActivityLoader(
                promises,
                activityManager,
                mock(ActivityBeansCache.class),
                mock(PlaceManager.class),
                mock(LazyLoadingScreen.class),
                activityLazyLoadedEvent,
                mock(Instance.class),
                appFormerComponentsRegistry));
    }

    @Test
    public void registerComponentPerspective() {
        doNothing().when(appFormerJsActivityLoader).registerPerspective(any());
        AppFormerComponentsRegistry.Entry entry = getEntry("a-perspective", AppFormerComponentsRegistry.Entry.Type.PERSPECTIVE, "bar.js");
        appFormerJsActivityLoader.registerComponent(entry);
        verify(appFormerJsActivityLoader).registerPerspective(entry);
    }

    @Test
    public void registerComponentScreen() {
        doNothing().when(appFormerJsActivityLoader).registerScreen(any());
        AppFormerComponentsRegistry.Entry entry = getEntry("a-screen", SCREEN, "bar.js");
        appFormerJsActivityLoader.registerComponent(entry);
        verify(appFormerJsActivityLoader).registerScreen(entry);
    }

    @Test
    public void registerComponentEditor() {
        doNothing().when(appFormerJsActivityLoader).registerEditor(any());
        AppFormerComponentsRegistry.Entry entry = getEntry("an-editor", EDITOR, "bar.js");
        appFormerJsActivityLoader.registerComponent(entry);
        verify(appFormerJsActivityLoader).registerEditor(entry);
    }

    private AppFormerComponentsRegistry.Entry getEntry(final String componentId,
                                                       final AppFormerComponentsRegistry.Entry.Type type,
                                                       final String scriptFileName) {

        final AppFormerComponentsRegistry.Entry entry = spy(new AppFormerComponentsRegistry.Entry(componentId, mock(JavaScriptObject.class)));
        doReturn(type).when(entry).getType();
        doReturn(scriptFileName).when(entry).getSource();
        return entry;
    }

    @Test
    public void init() {
        doReturn(new String[]{"foo", "bar"}).when(appFormerComponentsRegistry).keys();
        doReturn(mock(JavaScriptObject.class)).when(appFormerComponentsRegistry).get(anyString());
        doNothing().when(appFormerJsActivityLoader).registerComponent(any());

        appFormerJsActivityLoader.init("my-module");

        verify(appFormerJsActivityLoader, times(2)).registerComponent(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void onComponentLoaded_unregistered() {
        final JavaScriptObject jsObject = mock(JavaScriptObject.class);
        doReturn("my-component-id").when(appFormerJsActivityLoader).extractComponentId(jsObject);

        appFormerJsActivityLoader.onComponentLoaded(jsObject);
    }

    @Test
    public void onComponentLoaded_registeredEditor() {
        final AppFormerComponentsRegistry.Entry entry = getEntry("my-component-id", EDITOR, "foo.js");

        doReturn("my-component-id").when(appFormerJsActivityLoader).extractComponentId(any());
        doNothing().when(appFormerJsActivityLoader).registerEditor(any(), eq("my-component-id"));

        appFormerJsActivityLoader.registerEditor(entry);
        appFormerJsActivityLoader.onComponentLoaded(entry.getSelf());

        verify(appFormerJsActivityLoader).registerEditor(entry.getSelf(), entry.getComponentId());
    }

    @Test
    public void onComponentLoaded_registeredComponent() {
        final AppFormerComponentsRegistry.Entry entry = getEntry("my-component-id", SCREEN, "foo.js");

        doReturn("my-component-id").when(appFormerJsActivityLoader).extractComponentId(any());
        doNothing().when(appFormerJsActivityLoader).registerScreen(entry);
        doReturn(mock(Activity.class)).when(appFormerJsActivityLoader).updateRealContent(any(), eq("my-component-id"));

        appFormerJsActivityLoader.registerComponent(entry);
        appFormerJsActivityLoader.onComponentLoaded(entry.getSelf());

        verify(appFormerJsActivityLoader).updateRealContent(entry.getSelf(), entry.getComponentId());
        verify(activityLazyLoadedEvent).fire(any());
    }

    @Test(expected = RuntimeException.class)
    public void loadScriptFor_noScript() {
        doReturn(Optional.empty()).when(appFormerJsActivityLoader.getScriptFileName("my-component-id"));
        appFormerJsActivityLoader.loadScriptFor("my-component-id");
    }

    @Test
    public void loadScriptFor_unloaded() {
        init();
        doReturn(Optional.of("a-script-path.js")).when(appFormerJsActivityLoader).getScriptFileName("my-component-id");
        doReturn(promises.<Void>resolve()).when(appFormerJsActivityLoader).loadScript("my-module/a-script-path.js");

        appFormerJsActivityLoader.loadScriptFor("my-component-id").catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void loadScriptFor_loaded() {
        loadScriptFor_unloaded();
        verify(appFormerJsActivityLoader).loadScript(any()); //Invoked once on first load

        appFormerJsActivityLoader.loadScriptFor("my-component-id").catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(appFormerJsActivityLoader).loadScript(any()); //Still only invoked once
    }

    @Test
    public void updateRealContent() {
        final JavaScriptObject jsObject = mock(JavaScriptObject.class);
        final JsWorkbenchScreenActivity activity = mock(JsWorkbenchScreenActivity.class);
        doReturn(activity).when(activityManager).getActivity(any());

        final Activity foo = appFormerJsActivityLoader.updateRealContent(jsObject, "foo");

        assertEquals(foo, activity);
        verify((JsWorkbenchLazyActivity) foo).updateRealContent(jsObject);
    }

    @Test
    public void getScriptFileName_unexistent() {
        assertEquals(Optional.empty(), appFormerJsActivityLoader.getScriptFileName("my-component"));
    }

    @Test
    public void getScriptFileName_editor() {
        appFormerJsActivityLoader.registerEditor(getEntry("my-component", EDITOR, "foo.js"));

        final Optional<String> scriptFileName = appFormerJsActivityLoader.getScriptFileName("my-component");

        assertTrue(scriptFileName.isPresent());
        assertEquals("foo.js", scriptFileName.get());
    }

    @Test
    public void getScriptFileName_component() {
        doNothing().when(appFormerJsActivityLoader).registerScreen(any());
        appFormerJsActivityLoader.registerComponent(getEntry("my-component", SCREEN, "foo.js"));

        final Optional<String> scriptFileName = appFormerJsActivityLoader.getScriptFileName("my-component");

        assertTrue(scriptFileName.isPresent());
        assertEquals("foo.js", scriptFileName.get());
    }

    @Test
    public void triggerLoadOfMatchingEditors_nullPath() {
        assertFalse(appFormerJsActivityLoader.triggerLoadOfMatchingEditors(null, null));
    }

    @Test
    public void triggerLoadOfMatchingEditors_nullPathURI() {
        assertFalse(appFormerJsActivityLoader.triggerLoadOfMatchingEditors(new PathFactory.PathImpl("foo.txt", null), null));
    }

    @Test
    public void triggerLoadOfMatchingEditors_noMatchingEditors() {
        doReturn(new ArrayList<>()).when(appFormerJsActivityLoader).loadMatchingEditors("default://foo.txt");
        assertFalse(appFormerJsActivityLoader.triggerLoadOfMatchingEditors(new PathFactory.PathImpl("foo.txt", "default://foo.txt"), null));
    }

    @Test
    public void triggerLoadOfMatchingEditors_moreThanZeroMatchingEditors() {
        final List<Promise<Void>> loadingEditors = singletonList(promises.resolve());
        doReturn(loadingEditors).when(appFormerJsActivityLoader).loadMatchingEditors("default://foo.txt");
        doNothing().when(appFormerJsActivityLoader).finishLoadingMatchingEditors(any(), any());

        assertTrue(appFormerJsActivityLoader.triggerLoadOfMatchingEditors(new PathFactory.PathImpl("foo.txt", "default://foo.txt"), null));

        verify(appFormerJsActivityLoader).finishLoadingMatchingEditors(loadingEditors, null);
    }

    @Test
    public void finishLoadingMatchingEditors_success() {
        final List<Promise<Void>> ps = asList(promises.resolve(), promises.resolve());
        final AtomicBoolean pass = new AtomicBoolean(false);
        appFormerJsActivityLoader.finishLoadingMatchingEditors(ps, () -> pass.set(true));
        assertTrue(pass.get());
    }

    @Test
    public void finishLoadingMatchingEditors_failure() {
        final List<Promise<Void>> ps = asList(promises.resolve(), promises.reject(null));
        appFormerJsActivityLoader.finishLoadingMatchingEditors(ps, Assert::fail);
    }

    @Test
    public void loadMatchingEditors_matches() {
        AppFormerComponentsRegistry.Entry entry1 = getEntry("my-editor", EDITOR, "foo.js");
        doReturn(true).when(entry1).matches("my-asset.txt");
        appFormerJsActivityLoader.registerEditor(entry1);

        AppFormerComponentsRegistry.Entry entry2 = getEntry("my-other-editor", EDITOR, "foo.js");
        doReturn(true).when(entry2).matches("my-asset.txt");
        appFormerJsActivityLoader.registerEditor(entry2);

        final Promise<Object> p1 = promises.resolve();
        doReturn(p1).when(appFormerJsActivityLoader).loadScriptFor("my-editor");

        final Promise<Object> p2 = promises.resolve();
        doReturn(p2).when(appFormerJsActivityLoader).loadScriptFor("my-other-editor");

        final List<Promise<Void>> promises = appFormerJsActivityLoader.loadMatchingEditors("my-asset.txt");

        assertEquals(2, promises.size());
        assertSame(p1, promises.get(0));
        assertSame(p2, promises.get(1));
    }

    @Test
    public void loadMatchingEditors_doesntMatch() {
        AppFormerComponentsRegistry.Entry entry1 = getEntry("my-editor", EDITOR, "foo.js");
        doReturn(false).when(entry1).matches("my-asset.txt");
        appFormerJsActivityLoader.registerEditor(entry1);

        AppFormerComponentsRegistry.Entry entry2 = getEntry("my-other-editor", EDITOR, "foo.js");
        doReturn(true).when(entry2).matches("my-asset.txt");
        appFormerJsActivityLoader.registerEditor(entry2);

        final Promise<Object> p1 = promises.resolve();
        doReturn(p1).when(appFormerJsActivityLoader).loadScriptFor("my-editor");

        final Promise<Object> p2 = promises.resolve();
        doReturn(p2).when(appFormerJsActivityLoader).loadScriptFor("my-other-editor");

        final List<Promise<Void>> promises = appFormerJsActivityLoader.loadMatchingEditors("my-asset.txt");

        assertEquals(1, promises.size());
        assertSame(p2, promises.get(0));
    }
}