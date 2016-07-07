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

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PreferenceItemEditorView extends Composite
        implements PreferenceItemEditorPresenter.View {

    private final TranslationService translationService;

    private PreferenceItemEditorPresenter presenter;

    @Inject
    @DataField("preference-label")
    FormLabel preferenceLabel;

    @Inject
    @DataField("preference-value")
    TextBox preferenceValue;

    @Inject
    @DataField("preference-value-help")
    Span preferenceValueHelp;

    @Inject
    public PreferenceItemEditorView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final PreferenceItemEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setLabel( final String label ) {
        preferenceLabel.setText( label );
    }

    @Override
    public void setValue( final String value ) {
        preferenceValue.setValue( value );
    }

    @Override
    public void undoChanges() {
        setLabel( presenter.getPreferenceKey() );
        setValue( presenter.getPersistedPreferenceValue() );
    }

    @Override
    public String getNewPreferenceValue() {
        return preferenceValue.getValue();
    }

}
