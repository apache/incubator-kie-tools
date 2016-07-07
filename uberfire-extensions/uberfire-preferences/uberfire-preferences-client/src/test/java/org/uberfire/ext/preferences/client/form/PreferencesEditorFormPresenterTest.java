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

package org.uberfire.ext.preferences.client.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class PreferencesEditorFormPresenterTest {

    private SyncBeanManager beanManager;

    private PreferencesEditorFormPresenter.View view;

    private PreferenceStore preferenceStore;

    private CallerMock<PreferenceStore> preferenceStoreCaller;

    private Event<NotificationEvent> notification;

    private PreferencesEditorFormPresenter presenter;

    @Before
    public void setup() {
        beanManager = mock( SyncBeanManager.class );
        view = mock( PreferencesEditorFormPresenter.View.class );
        preferenceStore = mock( PreferenceStore.class );
        preferenceStoreCaller = new CallerMock<>( preferenceStore );
        notification = mock( Event.class );

        Map<String, Object> map = new HashMap<>();
        map.put( "my.first.preference.key", "value1" );
        map.put( "my.second.preference.key", "value2" );
        map.put( "my.third.preference.key", "value3" );
        doReturn( map ).when( preferenceStore ).search( anyList() );

        doReturn( createSyncBeanDef() ).when( beanManager ).lookupBean( PreferenceItemEditorPresenter.class );

        presenter = spy( new PreferencesEditorFormPresenter( beanManager, view, preferenceStoreCaller, notification ) );

        List<String> managedKeys = new ArrayList<>();
        managedKeys.add( "my.first.preference.key" );
        managedKeys.add( "my.second.preference.key" );
        managedKeys.add( "my.third.preference.key" );

        presenter.setManagedKeys( managedKeys );
    }

    @Test
    public void managedKeysWereSetTest() {
        List<PreferenceItemEditorPresenter> preferencesEditors = presenter.getPreferencesEditors();
        assertEquals( 3, preferencesEditors.size() );
    }

    @Test
    public void saveWithNoChangesTest() {
        doReturn( new HashMap<>() ).when( presenter ).getPreferencesToPersist();

        presenter.save();

        verify( notification ).fire( any( NotificationEvent.class ) );
        verify( view ).getNoChangesMessage();
    }

    @Test
    public void saveSuccessTest() {
        presenter.save();

        verify( notification ).fire( any( NotificationEvent.class ) );
        verify( view ).getPreferencesSavedSuccessfullyMessage();
    }

    @Test
    public void saveFailTest() {
        doThrow( new RuntimeException() ).when( preferenceStore ).put( anyMap() );

        presenter.save();

        verify( notification ).fire( any( NotificationEvent.class ) );
        verify( view ).getUnexpectedErrorDuringSavingMessage( anyString() );
    }

    @Test
    public void undoChangesTest() {
        presenter.undoChanges();

        for ( final PreferenceItemEditorPresenter preferenceEditor : presenter.getPreferencesEditors() ) {
            verify( preferenceEditor ).undoChanges();
        }
    }

    @Test
    public void destroyTest() {
        presenter.destroy();

        for ( final PreferenceItemEditorPresenter preferenceEditor : presenter.getPreferencesEditors() ) {
            verify( beanManager ).destroyBean( preferenceEditor );
        }
    }

    private SyncBeanDef<PreferenceItemEditorPresenter> createSyncBeanDef() {
        return new SyncBeanDef<PreferenceItemEditorPresenter>() {
            @Override
            public boolean isAssignableTo( final Class<?> aClass ) {
                return false;
            }

            @Override
            public Class<PreferenceItemEditorPresenter> getType() {
                return null;
            }

            @Override
            public Class<?> getBeanClass() {
                return null;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return null;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return null;
            }

            @Override
            public boolean matches( final Set<Annotation> set ) {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public boolean isActivated() {
                return false;
            }

            @Override
            public PreferenceItemEditorPresenter getInstance() {
                return null;
            }

            @Override
            public PreferenceItemEditorPresenter newInstance() {
                final PreferenceItemEditorPresenter.View viewMock = mock( PreferenceItemEditorPresenter.View.class );
                final PreferenceItemEditorPresenter presenter = new PreferenceItemEditorPresenter( viewMock );

                return spy( presenter );
            }
        };
    }
}
