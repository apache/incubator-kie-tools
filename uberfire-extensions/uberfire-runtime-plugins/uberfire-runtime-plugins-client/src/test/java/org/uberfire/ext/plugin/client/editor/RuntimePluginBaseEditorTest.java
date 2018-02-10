/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.client.editor;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RuntimePluginBaseEditorTest {

    @Mock
    private RemoteCallback<PluginContent> successCallBack;

    @Mock
    private RuntimePluginBaseView baseEditorView;

    @Mock
    private PluginServices pluginServices;

    @Mock
    private RuntimePluginBaseEditor editor;

    @Mock
    private Plugin pluginMock;

    private CallerMock<PluginServices> callerMock;

    @Before
    public void setup() {
        callerMock = new CallerMock<>(pluginServices);
        editor = spy(createRuntimePluginBaseEditor());
    }

    @Test
    public void loadContentTest() {

        final PluginContent pluginContent = mock(PluginContent.class);
        when(pluginServices.getPluginContent(Matchers.<Path>any())).thenReturn(pluginContent);

        assertNull(editor.getOriginalHash());

        editor.loadContent();

        verify(pluginServices).getPluginContent(Matchers.<Path>any());
        verify(baseEditorView).setFramework(anyCollection());
        verify(baseEditorView).setupContent(eq(pluginContent),
                                            Matchers.<ParameterizedCommand<Media>>any());
        verify(baseEditorView).hideBusyIndicator();

        assertNotNull(editor.getOriginalHash());
    }

    @Test
    public void pluginSavedTest() {
        editor.onPluginSaved(mock(PluginSaved.class));
        verify(editor).registerPlugin(any(Plugin.class));
    }

    @Test
    public void pluginAddedTest() {
        editor.onPluginAdded(mock(PluginAdded.class));
        verify(editor).registerPlugin(any(Plugin.class));
    }

    @Test
    public void pluginDeletedTest() {
        editor.onPluginDeleted(mock(PluginDeleted.class));
        verify(editor).unregisterPlugin(any(String.class),
                                        any(PluginType.class));
    }

    @Test
    public void pluginRenamedTest() {
        editor.onPluginRenamed(mock(PluginRenamed.class));
        verify(editor).unregisterPlugin(any(String.class),
                                        any(PluginType.class));
        verify(editor).registerPlugin(any(Plugin.class));
    }

    @Test
    public void saveCommandTest() {
        editor.getSaveCommand().execute("commitMessage");
        verify(baseEditorView).onSave();
    }

    @Test
    public void testGetContentSupplier() {

        final Supplier<Plugin> contentSupplier = editor.getContentSupplier();

        assertEquals(pluginMock, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertEquals(callerMock, editor.getSaveAndRenameServiceCaller());
    }

    private RuntimePluginBaseEditor createRuntimePluginBaseEditor() {

        return new RuntimePluginBaseEditor(baseEditorView) {
            @Override
            protected PluginType getPluginType() {
                return PluginType.DYNAMIC_MENU;
            }

            @Override
            protected ClientResourceType getResourceType() {
                return null;
            }

            @Override
            RuntimePluginBaseView view() {
                return baseEditorView;
            }

            @Override
            Caller<PluginServices> getPluginServices() {
                return callerMock;
            }

            @Override
            ObservablePath getCurrentPath() {
                return mock(ObservablePath.class);
            }

            @Override
            public Plugin getContent() {
                return pluginMock;
            }

            @Override
            void unregisterPlugin(String name,
                                  PluginType type) {
            }

            @Override
            void registerPlugin(Plugin plugin) {
            }

            @Override
            public RemoteCallback<Path> getSaveSuccessCallback(final int newHash) {
                return path -> {
                };
            }
        };
    }
}
