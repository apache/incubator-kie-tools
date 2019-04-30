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

import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.jsbridge.client.perspective.JsWorkbenchPerspectiveActivity;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePerspective;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsWorkbenchLazyPerspectiveActivityTest {

    private JsWorkbenchLazyPerspectiveActivity jsWorkbenchLazyPerspectiveActivity;

    private PlaceManager placeManager;
    private AppFormerComponentsRegistry.Entry entry;
    private ActivityManager activityManager;
    private Consumer<String> lazyLoadingParentScript;

    @Before
    public void before() {
        placeManager = mock(PlaceManager.class);
        entry = mock(AppFormerComponentsRegistry.Entry.class);
        activityManager = mock(ActivityManager.class);
        lazyLoadingParentScript = s -> {
        };

        jsWorkbenchLazyPerspectiveActivity = spy(new JsWorkbenchLazyPerspectiveActivity(
                entry,
                placeManager,
                activityManager,
                lazyLoadingParentScript));
    }

    @Test
    public void getNotLoaded() {
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(mock(JsWorkbenchPerspectiveActivity.class));
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(false);
        assertEquals(jsWorkbenchLazyPerspectiveActivity, jsWorkbenchLazyPerspectiveActivity.get());
    }

    @Test
    public void getLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(true);
        assertEquals(backedPerspective, jsWorkbenchLazyPerspectiveActivity.get());
    }

    @Test
    public void onStartupLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(true);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        jsWorkbenchLazyPerspectiveActivity.onStartup(placeRequest);

        verify(backedPerspective).onStartup(placeRequest);
    }

    @Test
    public void onStartupNotLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(false);

        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        jsWorkbenchLazyPerspectiveActivity.onStartup(placeRequest);

        verify(backedPerspective, never()).onStartup(placeRequest);
    }

    @Test
    public void onOpenLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(true);

        jsWorkbenchLazyPerspectiveActivity.onOpen();

        verify(backedPerspective).onOpen();
        verify(placeManager).executeOnOpenCallbacks(any());
        verify(jsWorkbenchLazyPerspectiveActivity, never()).onLoaded();
    }

    @Test
    public void onOpenNotLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(false);

        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();

        verify(backedPerspective, never()).onOpen();
        verify(placeManager, times(2)).executeOnOpenCallbacks(any());
        verify(jsWorkbenchLazyPerspectiveActivity).onLoaded();
    }

    @Test
    public void onCloseLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(true);

        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onClose();

        verify(backedPerspective).onClose();
        verify(placeManager).executeOnCloseCallbacks(any());
    }

    @Test
    public void onCloseNotLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(false);

        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();
        jsWorkbenchLazyPerspectiveActivity.onClose();

        verify(backedPerspective, never()).onClose();
        verify(placeManager, times(2)).executeOnCloseCallbacks(any());
    }

    @Test
    public void onShutdownLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(true);

        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();
        jsWorkbenchLazyPerspectiveActivity.onShutdown();

        verify(backedPerspective).onShutdown();
    }

    @Test
    public void onShutdownNotLoaded() {
        JsWorkbenchPerspectiveActivity backedPerspective = mock(JsWorkbenchPerspectiveActivity.class);
        when(jsWorkbenchLazyPerspectiveActivity.getBackedPerspective()).thenReturn(backedPerspective);
        when(jsWorkbenchLazyPerspectiveActivity.isPerspectiveLoaded()).thenReturn(false);

        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();
        jsWorkbenchLazyPerspectiveActivity.onClose();
        jsWorkbenchLazyPerspectiveActivity.onShutdown();

        verify(backedPerspective, never()).onShutdown();
    }

    @Test
    public void updateRealContent() {
        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();

        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        doReturn(perspectiveActivity).when(jsWorkbenchLazyPerspectiveActivity).getBackedPerspective(any());
        doReturn(true).when(activityManager).isStarted(any());

        jsWorkbenchLazyPerspectiveActivity.updateRealContent(mock(JavaScriptObject.class));

        assertEquals(perspectiveActivity, jsWorkbenchLazyPerspectiveActivity.backedPerspective);
        verify(perspectiveActivity).onStartup(any());
        verify(perspectiveActivity).onOpen();
        verify(placeManager).goTo((PlaceRequest) any());
    }

    @Test
    public void updateRealContent_notStarted() {
        jsWorkbenchLazyPerspectiveActivity.onStartup(mock(PlaceRequest.class));
        jsWorkbenchLazyPerspectiveActivity.onOpen();

        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        doReturn(perspectiveActivity).when(jsWorkbenchLazyPerspectiveActivity).getBackedPerspective(any());
        doReturn(false).when(activityManager).isStarted(any());

        jsWorkbenchLazyPerspectiveActivity.updateRealContent(mock(JavaScriptObject.class));

        assertEquals(perspectiveActivity, jsWorkbenchLazyPerspectiveActivity.backedPerspective);
        verify(perspectiveActivity, never()).onStartup(any());
        verify(perspectiveActivity).onOpen();
        verify(placeManager).goTo((PlaceRequest) any());
    }

    @Test
    public void updateRealContent_notOpen() {
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        doReturn(perspectiveActivity).when(jsWorkbenchLazyPerspectiveActivity).getBackedPerspective(any());
        doReturn(false).when(activityManager).isStarted(any());

        jsWorkbenchLazyPerspectiveActivity.updateRealContent(mock(JavaScriptObject.class));

        assertEquals(perspectiveActivity, jsWorkbenchLazyPerspectiveActivity.backedPerspective);
        verify(perspectiveActivity, never()).onStartup(any());
        verify(perspectiveActivity, never()).onOpen();
        verify(placeManager, never()).goTo((PlaceRequest) any());
    }
}
