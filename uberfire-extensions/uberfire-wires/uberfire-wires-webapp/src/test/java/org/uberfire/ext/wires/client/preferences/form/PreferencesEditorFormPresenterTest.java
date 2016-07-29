/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client.preferences.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.impl.DefaultPreferenceScopeResolver;
import org.uberfire.ext.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopedValue;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PreferencesEditorFormPresenterTest {

    private PreferencesEditorFormPresenter.View view;

    private PreferenceStore preferenceStore;

    private Event<NotificationEvent> notification;

    private PreferencesEditorFormPresenter presenter;

    private ManagedInstance<PreferencesEditorItemPresenter> editorItemProvider;

    @Before
    public void setup() {
        view = mock( PreferencesEditorFormPresenter.View.class );
        notification = mock( Event.class );
        editorItemProvider = mock( ManagedInstance.class );

        final SessionInfoMock sessionInfo = new SessionInfoMock();
        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        final PreferenceScopeFactoryImpl scopeFactory = new PreferenceScopeFactoryImpl( scopeTypes );
        final DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopeFactory, null );
        final PreferenceScopeResolutionStrategyInfo info = defaultPreferenceScopeResolutionStrategy.getInfo();

        preferenceStore = spy( new PreferenceStore( null, null, info, new DefaultPreferenceScopeResolver( info.order() ) ) );

        doAnswer( invocationOnMock -> newPreferencesEditorItemPresenterInstance() ).when( editorItemProvider ).get();

        Map<String, PreferenceScopedValue<Object>> map = new HashMap<>();
        map.put( "my.first.preference.key", new PreferenceScopedValue<>( "value1", mock( PreferenceScope.class ) ) );
        map.put( "my.second.preference.key", new PreferenceScopedValue<>( "value2", mock( PreferenceScope.class ) ) );
        map.put( "my.third.preference.key", new PreferenceScopedValue<>( "value3", mock( PreferenceScope.class ) ) );
        doAnswer( invocationOnMock -> {
            ( (RemoteCallback<Map<String, PreferenceScopedValue<Object>>>) invocationOnMock.getArguments()[1] ).callback( map );
            return null;
        } ).when( preferenceStore ).searchScoped( anyList(), any( RemoteCallback.class ) );

        presenter = spy( new PreferencesEditorFormPresenter( view, notification, editorItemProvider ) );

        List<String> managedKeys = new ArrayList<>();
        managedKeys.add( "my.first.preference.key" );
        managedKeys.add( "my.second.preference.key" );
        managedKeys.add( "my.third.preference.key" );

        presenter.init( preferenceStore, managedKeys, "component title", ViewMode.GLOBAL );
    }

    @Test
    public void managedKeysWereSetTest() {
        List<PreferencesEditorItemPresenter> preferencesEditors = presenter.getPreferencesEditors();
        assertEquals( 3, preferencesEditors.size() );
    }

    @Test
    public void saveWithNoChangesTest() {
        doReturn( new ArrayList<>() ).when( presenter ).getPreferencesEditors();

        presenter.save();

        verify( notification ).fire( any( NotificationEvent.class ) );
        verify( view ).getNoChangesMessage();
    }

    @Test
    public void saveSuccessTest() {
        doAnswer( invocationOnMock -> {
            ( (RemoteCallback<Void>) invocationOnMock.getArguments()[3] ).callback( null );
            return null;
        } ).when( preferenceStore ).put( any( PreferenceScope.class ), anyString(), anyString(), any( RemoteCallback.class ), any( ErrorCallback.class ) );

        presenter.save();

        verify( notification, times( 3 ) ).fire( any( NotificationEvent.class ) );
        verify( view, times( 3 ) ).getPreferencesSavedSuccessfullyMessage( anyString() );
    }

    @Test
    public void saveFailTest() {
        doAnswer( invocationOnMock -> {
            ( (ErrorCallback<Message>) invocationOnMock.getArguments()[4] ).error( null, new RuntimeException() );
            return null;
        } ).when( preferenceStore ).put( any( PreferenceScope.class ), anyString(), anyString(), any( RemoteCallback.class ), any( ErrorCallback.class ) );

        presenter.save();

        verify( notification, times( 3 ) ).fire( any( NotificationEvent.class ) );
        verify( view, times( 3 ) ).getErrorsWhenSavingMessage( anyString(), anyString() );
    }

    @Test
    public void undoChangesTest() {
        presenter.undoChanges();

        for ( final PreferencesEditorItemPresenter preferenceEditor : presenter.getPreferencesEditors() ) {
            verify( preferenceEditor ).undoChanges();
        }
    }

    private PreferencesEditorItemPresenter newPreferencesEditorItemPresenterInstance() {
        final PreferencesEditorItemPresenter.View viewMock = mock( PreferencesEditorItemPresenter.View.class );
        final PreferencesEditorItemPresenter presenter = new PreferencesEditorItemPresenter( viewMock );

        return spy( presenter );
    }
}
