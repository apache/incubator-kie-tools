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
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopedValue;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class PreferencesEditorFormPresenter {

    public interface View extends UberElement<PreferencesEditorFormPresenter> {

        String getNoChangesMessage();

        String getPreferencesSavedSuccessfullyMessage( String preferenceKey );

        String getErrorsWhenSavingMessage( String preferenceKey,
                                           String details );
    }

    private final View view;

    private final Event<NotificationEvent> notification;

    private final ManagedInstance<PreferencesEditorItemPresenter> editorItemProducer;

    private PreferenceStore preferenceStore;

    private List<PreferencesEditorItemPresenter> preferencesEditors;

    @Inject
    public PreferencesEditorFormPresenter( final View view,
                                           final Event<NotificationEvent> notification,
                                           final ManagedInstance<PreferencesEditorItemPresenter> editorItemProducer ) {
        this.view = view;
        this.notification = notification;
        this.editorItemProducer = editorItemProducer;
    }

    public void init( final PreferenceStore preferenceStore,
                      final List<String> managedKeys,
                      final String componentTitle,
                      final ViewMode viewMode ) {
        this.preferenceStore = preferenceStore;

        final PreferencesEditorFormPresenter presenter = this;

        preferenceStore.searchScoped( managedKeys, preferences -> {
            preferencesEditors = new ArrayList<>();

            for ( final Map.Entry<String, PreferenceScopedValue<Object>> preference : preferences.entrySet() ) {
                final PreferencesEditorItemPresenter preferenceItem = editorItemProducer.get();

                preferenceItem.setScopeResolver( preferenceStore.getDefaultScopeResolver() );
                preferenceItem.setViewMode( viewMode );
                preferenceItem.setPreferenceKey( preference.getKey() );
                preferenceItem.setPersistedPreferenceValue( preference.getValue().getValue() );
                preferenceItem.setPersistedPreferenceScope( preference.getValue().getScope() );
                preferenceItem.setComponentTitle( componentTitle );

                preferencesEditors.add( preferenceItem );
            }

            view.init( presenter );
        } );
    }

    public void save() {
        boolean noChangesWereMade = true;

        for ( final PreferencesEditorItemPresenter preferenceItem : getPreferencesEditors() ) {
            if ( preferenceItem.shouldBePersisted() ) {
                final String key = preferenceItem.getPreferenceKey();
                final Object newValue = preferenceItem.getNewPreferenceValue();
                final PreferenceScope newScope = preferenceItem.getNewPreferenceScope();

                preferenceStore.put( newScope, key, newValue, onSuccess -> {
                    notification.fire( new NotificationEvent( view.getPreferencesSavedSuccessfullyMessage( preferenceItem.getPreferenceKey() ),
                                                              NotificationEvent.NotificationType.SUCCESS ) );
                    preferenceItem.setPersistedPreferenceValue( newValue );
                    preferenceItem.setPersistedPreferenceScope( newScope );
                }, ( message, throwable ) -> {
                    final String errorMessage = view.getErrorsWhenSavingMessage( preferenceItem.getPreferenceKey(),
                                                                                 throwable.getMessage() );
                    final NotificationEvent notificationEvent = new NotificationEvent( errorMessage,
                                                                                       NotificationEvent.NotificationType.ERROR );
                    notification.fire( notificationEvent );
                    return false;
                } );

                noChangesWereMade = false;
            }
        }

        if ( noChangesWereMade ) {
            notification.fire( new NotificationEvent( view.getNoChangesMessage(),
                                                      NotificationEvent.NotificationType.INFO ) );
        }
    }

    public void undoChanges() {
        for ( final PreferencesEditorItemPresenter preferenceEditor : preferencesEditors ) {
            preferenceEditor.undoChanges();
        }
    }

    public List<PreferencesEditorItemPresenter> getPreferencesEditors() {
        return preferencesEditors;
    }

    public View getView() {
        return view;
    }
}
