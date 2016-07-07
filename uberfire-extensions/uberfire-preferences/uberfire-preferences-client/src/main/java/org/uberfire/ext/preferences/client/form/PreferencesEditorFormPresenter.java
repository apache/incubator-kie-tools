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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class PreferencesEditorFormPresenter {

    public interface View extends UberElement<PreferencesEditorFormPresenter> {

        String getNoChangesMessage();

        String getPreferencesSavedSuccessfullyMessage();

        String getUnexpectedErrorDuringSavingMessage( String details );
    }

    private final SyncBeanManager beanManager;

    private final View view;

    private final Caller<PreferenceStore> preferenceStoreCaller;

    private final Event<NotificationEvent> notification;

    private List<PreferenceItemEditorPresenter> preferencesEditors;

    @Inject
    public PreferencesEditorFormPresenter( final SyncBeanManager beanManager,
                                           final View view,
                                           final Caller<PreferenceStore> preferenceStoreCaller,
                                           final Event<NotificationEvent> notification ) {
        this.beanManager = beanManager;
        this.view = view;
        this.preferenceStoreCaller = preferenceStoreCaller;
        this.notification = notification;
    }

    public void setManagedKeys( final List<String> managedKeys ) {
        final PreferencesEditorFormPresenter presenter = this;

        preferenceStoreCaller.call( new RemoteCallback<Map<String, Object>>() {
            @Override
            public void callback( final Map<String, Object> preferences ) {
                preferencesEditors = new ArrayList<>();

                for ( final Map.Entry<String, Object> preference : preferences.entrySet() ) {
                    final PreferenceItemEditorPresenter preferenceEditor = beanManager.lookupBean( PreferenceItemEditorPresenter.class ).newInstance();
                    preferenceEditor.setPreferenceKey( preference.getKey() );
                    preferenceEditor.setPersistedPreferenceValue( preference.getValue().toString() );

                    preferencesEditors.add( preferenceEditor );
                }

                view.init( presenter );
            }
        } ).search( managedKeys );
    }

    public void save() {
        Map<String, String> preferencesToPersist = getPreferencesToPersist();

        if ( !preferencesToPersist.isEmpty() ) {
            preferenceStoreCaller.call( voidReturn -> {
                notification.fire( new NotificationEvent( view.getPreferencesSavedSuccessfullyMessage(), NotificationEvent.NotificationType.SUCCESS ) );
            }, ( o, throwable ) -> {
                notification.fire( new NotificationEvent( view.getUnexpectedErrorDuringSavingMessage( throwable.getMessage() ), NotificationEvent.NotificationType.ERROR ) );
                return false;
            } ).put( preferencesToPersist );
        } else {
            notification.fire( new NotificationEvent( view.getNoChangesMessage(), NotificationEvent.NotificationType.INFO ) );
        }
    }

    public void undoChanges() {
        for ( final PreferenceItemEditorPresenter preferenceEditor : preferencesEditors ) {
            preferenceEditor.undoChanges();
        }
    }

    protected Map<String, String> getPreferencesToPersist() {
        Map<String, String> preferencesToPersist = new HashMap<>();

        for ( final PreferenceItemEditorPresenter preference : preferencesEditors ) {
            if ( preference.shouldBePersisted() ) {
                final String key = preference.getPreferenceKey();
                final String newValue = preference.getNewPreferenceValue();
                preferencesToPersist.put( key, newValue );
                preference.setPersistedPreferenceValue( newValue );
            }
        }
        return preferencesToPersist;
    }

    public List<PreferenceItemEditorPresenter> getPreferencesEditors() {
        return preferencesEditors;
    }

    public View getView() {
        return view;
    }

    @PreDestroy
    public void destroy() {
        if ( preferencesEditors != null ) {
            for ( PreferenceItemEditorPresenter item : preferencesEditors ) {
                beanManager.destroyBean( item );
            }
        }
    }
}
