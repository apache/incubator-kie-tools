package org.uberfire.ext.plugin.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RuntimePluginBaseEditorTest {

    private PluginServices pluginServices;
    private CallerMock<PluginServices> callerMock;
    RemoteCallback<PluginContent> successCallBack;

    RuntimePluginBaseView baseEditorView = null;

    private RuntimePluginBaseEditor editor;

    @Before
    public void setup() {
        pluginServices = mock( PluginServices.class );
        callerMock = new CallerMock<PluginServices>( pluginServices );
        editor = createRuntimePluginBaseEditor();
        successCallBack = mock( RemoteCallback.class );
        baseEditorView = mock( RuntimePluginBaseView.class );
    }

    @Test
    public void loadContentTest() {

        final PluginContent pluginContent = mock( PluginContent.class );
        when( pluginServices.getPluginContent( Matchers.<Path>any() ) ).thenReturn( pluginContent );

        assertNull( editor.getOriginalHash() );

        editor.loadContent();

        verify( pluginServices ).getPluginContent( Matchers.<Path>any() );
        verify( baseEditorView ).setFramework( anyCollection() );
        verify( baseEditorView ).setupContent( eq(pluginContent), Matchers.<ParameterizedCommand<Media>>any() );
        verify( baseEditorView ).hideBusyIndicator();

        assertNotNull( editor.getOriginalHash() );
    }

    private RuntimePluginBaseEditor createRuntimePluginBaseEditor() {

        return new RuntimePluginBaseEditor( baseEditorView ) {
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
                return mock( ObservablePath.class );
            }

            @Override
            public PluginSimpleContent getContent() {
                return mock( PluginSimpleContent.class );
            }
        };

    }


}