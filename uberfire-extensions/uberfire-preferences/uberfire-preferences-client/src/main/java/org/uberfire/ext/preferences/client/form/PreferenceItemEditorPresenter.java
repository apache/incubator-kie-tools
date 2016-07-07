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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;

@Dependent
public class PreferenceItemEditorPresenter {

    public interface View extends UberView<PreferenceItemEditorPresenter> {

        void setLabel( String label );

        void setValue( String value );

        void undoChanges();

        String getNewPreferenceValue();
    }

    private final View view;

    private String preferenceKey;

    private String persistedPreferenceValue;

    @Inject
    public PreferenceItemEditorPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public void undoChanges() {
        view.undoChanges();
    }

    public boolean shouldBePersisted() {
        return persistedPreferenceValue != null && !persistedPreferenceValue.equals( getNewPreferenceValue() );
    }

    public String getNewPreferenceValue() {
        return view.getNewPreferenceValue();
    }

    public void setPreferenceKey( final String preferenceKey ) {
        this.preferenceKey = preferenceKey;
        view.setLabel( preferenceKey );
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public String getPersistedPreferenceValue() {
        return persistedPreferenceValue;
    }

    public void setPersistedPreferenceValue( final String persistedPreferenceValue ) {
        this.persistedPreferenceValue = persistedPreferenceValue;
        view.setValue( persistedPreferenceValue );
    }

    public View getView() {
        return view;
    }
}
