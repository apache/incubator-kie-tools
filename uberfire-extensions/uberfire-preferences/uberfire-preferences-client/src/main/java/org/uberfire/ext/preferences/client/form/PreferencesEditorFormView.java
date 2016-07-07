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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Form;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.preferences.client.resources.i18n.Constants;

@Dependent
@Templated
public class PreferencesEditorFormView implements PreferencesEditorFormPresenter.View {

    private final TranslationService translationService;

    private PreferencesEditorFormPresenter presenter;

    @Inject
    @DataField("preferences-form")
    Form form;

    @Inject
    @DataField("preferences-fieldset")
    FieldSet preferencesFieldSet;

    @Inject
    @DataField("preferences-save-button")
    Button saveButton;

    @Inject
    @DataField("preferences-undo-changes-button")
    Button undoChangesButton;

    @Inject
    public PreferencesEditorFormView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final PreferencesEditorFormPresenter presenter ) {
        this.presenter = presenter;

        for ( PreferenceItemEditorPresenter preference : presenter.getPreferencesEditors() ) {
            preferencesFieldSet.add( preference.getView().asWidget() );
        }
    }

    @EventHandler("preferences-save-button")
    public void onSave( final ClickEvent event ) {
        presenter.save();
    }

    @EventHandler("preferences-undo-changes-button")
    public void onUndoChanges( final ClickEvent event ) {
        presenter.undoChanges();
    }

    @Override
    public String getNoChangesMessage() {
        return translationService.format( Constants.PreferencesEditorFormView_NoChangesMessage );
    }

    @Override
    public String getPreferencesSavedSuccessfullyMessage() {
        return translationService.format( Constants.PreferencesEditorFormView_SavedSuccessfullyMessage );
    }

    @Override
    public String getUnexpectedErrorDuringSavingMessage( final String details ) {
        return translationService.format( Constants.PreferencesEditorFormView_ErrorDuringSavingMessage, details );
    }

    @Override
    public HTMLElement getElement() {
        return form;
    }
}
