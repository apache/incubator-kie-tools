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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PreferencesEditorFormView implements IsElement,
                                                  PreferencesEditorFormPresenter.View {

    private PreferencesEditorFormPresenter presenter;

    @Inject
    @DataField("preferences")
    Div preferencesFieldSet;

    @Inject
    @DataField("preferences-save-button")
    Button saveButton;

    @Inject
    @DataField("preferences-undo-changes-button")
    Button undoChangesButton;

    @Inject
    public PreferencesEditorFormView() {
        super();
    }

    @Override
    public void init( final PreferencesEditorFormPresenter presenter ) {
        this.presenter = presenter;

        for ( PreferencesEditorItemPresenter preference : presenter.getPreferencesEditors() ) {
            preferencesFieldSet.appendChild( preference.getView().getElement() );
        }
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("preferences-save-button")
    public void onSave( final Event event ) {
        presenter.save();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("preferences-undo-changes-button")
    public void onUndoChanges( final Event event ) {
        presenter.undoChanges();
    }

    @Override
    public String getNoChangesMessage() {
        return "No changes were made.";
    }

    @Override
    public String getPreferencesSavedSuccessfullyMessage( final String preferenceKey ) {
        return "Preference " + preferenceKey + " saved successfully.";
    }

    @Override
    public String getErrorsWhenSavingMessage( final String preferenceKey,
                                              final String details ) {
        return "An error occurred when saving your changes on preference " + preferenceKey + ". Details: " + details;
    }
}
