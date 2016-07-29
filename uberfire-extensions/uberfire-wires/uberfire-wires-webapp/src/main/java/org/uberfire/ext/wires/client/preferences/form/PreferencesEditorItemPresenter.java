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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolver;
import org.uberfire.ext.preferences.shared.impl.DefaultScopes;

@Dependent
public class PreferencesEditorItemPresenter {

    public interface View extends UberElement<PreferencesEditorItemPresenter> {

        void setKey( String label );

        void setValue( Object value );

        void setComponentTitle( String componentTitle );

        Object getNewPreferenceValue();

        void setUserScopeSelected( boolean selected );

        void setAllUsersScopeSelected( boolean selected );

        void setComponentScopeSelected( boolean selected );

        void setEntireApplicationScopeSelected( boolean selected );

        boolean isUserScopeSelected();

        boolean isAllUsersScopeSelected();

        boolean isComponentScopeSelected();

        boolean isEntireApplicationScopeSelected();

        void setViewMode( ViewMode viewMode );
    }

    private final View view;

    private PreferenceScopeResolver scopeResolver;

    private String preferenceKey;

    private Object persistedPreferenceValue;

    private PreferenceScope persistedPreferenceScope;

    private ViewMode viewMode;

    @Inject
    public PreferencesEditorItemPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public void undoChanges() {
        view.setValue( persistedPreferenceValue );
    }

    public boolean shouldBePersisted() {
        final Object newPreferenceValue = getNewPreferenceValue();
        final PreferenceScope newPreferenceScope = getNewPreferenceScope();

        boolean shouldBePersisted = persistedPreferenceValue != null && !persistedPreferenceValue.equals( newPreferenceValue );
        shouldBePersisted |= newPreferenceValue != null && !newPreferenceValue.equals( persistedPreferenceValue );
        shouldBePersisted |= !persistedPreferenceScope.equals( newPreferenceScope );

        return shouldBePersisted;
    }

    public PreferenceScope getNewPreferenceScope() {
        if ( viewMode.equals( ViewMode.GLOBAL ) ) {
            return persistedPreferenceScope;
        } else if ( viewMode.equals( ViewMode.USER ) ) {
            if ( view.isUserScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.USER.type() );
            } else if ( view.isAllUsersScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.ALL_USERS.type() );
            }
        } else if ( viewMode.equals( ViewMode.COMPONENT ) ) {
            if ( view.isComponentScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.COMPONENT.type() );
            } else if ( view.isEntireApplicationScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.ENTIRE_APPLICATION.type() );
            }
        } else if ( viewMode.equals( ViewMode.USER_COMPONENT ) ) {
            if ( view.isUserScopeSelected() && view.isComponentScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.USER.type(), DefaultScopes.COMPONENT.type() );
            } else if ( view.isUserScopeSelected() && view.isEntireApplicationScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.USER.type(), DefaultScopes.ENTIRE_APPLICATION.type() );
            } else if ( view.isAllUsersScopeSelected() && view.isComponentScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.ALL_USERS.type(), DefaultScopes.COMPONENT.type() );
            } else if ( view.isAllUsersScopeSelected() && view.isEntireApplicationScopeSelected() ) {
                return scopeResolver.resolve( DefaultScopes.ALL_USERS.type(), DefaultScopes.ENTIRE_APPLICATION.type() );
            }
        }

        throw new RuntimeException( "Invalid scope selected." );
    }

    public Object getNewPreferenceValue() {
        return view.getNewPreferenceValue();
    }

    public void setPreferenceKey( final String preferenceKey ) {
        this.preferenceKey = preferenceKey;
        view.setKey( preferenceKey );
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public void setPersistedPreferenceValue( final Object persistedPreferenceValue ) {
        this.persistedPreferenceValue = persistedPreferenceValue;
        view.setValue( persistedPreferenceValue );
    }

    public void setPersistedPreferenceScope( final PreferenceScope persistedPreferenceScope ) {
        this.persistedPreferenceScope = persistedPreferenceScope;

        if ( viewMode.equals( ViewMode.GLOBAL ) ) {
            view.setUserScopeSelected( false );
            view.setComponentScopeSelected( false );
            view.setAllUsersScopeSelected( false );
            view.setEntireApplicationScopeSelected( false );
        } else if ( viewMode.equals( ViewMode.USER ) ) {
            if ( scopeResolver.resolve( DefaultScopes.USER.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( true );
                view.setComponentScopeSelected( false );
                view.setAllUsersScopeSelected( false );
                view.setEntireApplicationScopeSelected( true );
            } else if ( scopeResolver.resolve( DefaultScopes.ALL_USERS.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( false );
                view.setComponentScopeSelected( false );
                view.setAllUsersScopeSelected( true );
                view.setEntireApplicationScopeSelected( true );
            }
        } else if ( viewMode.equals( ViewMode.COMPONENT ) ) {
            if ( scopeResolver.resolve( DefaultScopes.COMPONENT.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( false );
                view.setComponentScopeSelected( true );
                view.setAllUsersScopeSelected( true );
                view.setEntireApplicationScopeSelected( false );
            } else if ( scopeResolver.resolve( DefaultScopes.ENTIRE_APPLICATION.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( false );
                view.setComponentScopeSelected( false );
                view.setAllUsersScopeSelected( true );
                view.setEntireApplicationScopeSelected( true );
            }
        } else if ( viewMode.equals( ViewMode.USER_COMPONENT ) ) {
            if ( scopeResolver.resolve( DefaultScopes.USER.type(), DefaultScopes.COMPONENT.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( true );
                view.setComponentScopeSelected( true );
                view.setAllUsersScopeSelected( false );
                view.setEntireApplicationScopeSelected( false );
            } else if ( scopeResolver.resolve( DefaultScopes.USER.type(), DefaultScopes.ENTIRE_APPLICATION.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( true );
                view.setComponentScopeSelected( false );
                view.setAllUsersScopeSelected( false );
                view.setEntireApplicationScopeSelected( true );
            } else if ( scopeResolver.resolve( DefaultScopes.ALL_USERS.type(), DefaultScopes.COMPONENT.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( false );
                view.setComponentScopeSelected( true );
                view.setAllUsersScopeSelected( true );
                view.setEntireApplicationScopeSelected( false );
            } else if ( scopeResolver.resolve( DefaultScopes.ALL_USERS.type(), DefaultScopes.ENTIRE_APPLICATION.type() ).equals( persistedPreferenceScope ) ) {
                view.setUserScopeSelected( false );
                view.setComponentScopeSelected( false );
                view.setAllUsersScopeSelected( true );
                view.setEntireApplicationScopeSelected( true );
            }
        }
    }

    public void setComponentTitle( final String componentTitle ) {
        view.setComponentTitle( componentTitle );
    }

    public void setScopeResolver( final PreferenceScopeResolver scopeResolver ) {
        this.scopeResolver = scopeResolver;
    }

    public void setViewMode( final ViewMode viewMode ) {
        this.viewMode = viewMode;
        view.setViewMode( viewMode );
    }

    public View getView() {
        return view;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }
}
