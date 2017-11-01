/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.globals.client.editor;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
@Templated
public class AddGlobalPopupViewImpl implements AddGlobalPopupView {

    @DataField("view")
    Div view;

    @DataField("aliasFormGroup")
    Element aliasFormGroup = DOM.createSpan();

    @DataField("aliasInput")
    TextInput aliasInput;

    @DataField("aliasInputHelp")
    Element aliasInputHelp = DOM.createSpan();

    @DataField("classNameFormGroup")
    Element classNameFormGroup = DOM.createSpan();

    @DataField("classNameSelect")
    Select classNameSelect;

    @DataField("classNameSelectHelp")
    Element classNameSelectHelp = DOM.createSpan();

    @DataField("addButton")
    Button addButton;

    @DataField("cancelButton")
    Button cancelButton;

    private Presenter presenter;

    private BaseModal modal;

    private TranslationService translationService;

    @Inject
    public AddGlobalPopupViewImpl( final Div view,
                                   final TextInput aliasInput,
                                   final Select classNameSelect,
                                   final Button yesButton,
                                   final Button cancelButton,
                                   final TranslationService translationService ) {
        this.view = view;
        this.aliasInput = aliasInput;
        this.classNameSelect = classNameSelect;
        this.addButton = yesButton;
        this.cancelButton = cancelButton;
        this.translationService = translationService;

        aliasInput.setAttribute( "placeholder",
                                 translationService.getTranslation( GlobalsEditorConstants.AddGlobalPopupAliasInputPlaceholder ) );
    }

    @Override
    public void init( final Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    @Override
    public void setClassNames( List<String> classNames ) {
        for ( String className : classNames ) {
            Option option = new Option();
            option.setText( className );
            option.setValue( className );
            classNameSelect.add( option );
        }
        classNameSelect.refresh();
    }

    @Override
    public String getInsertedAlias() {
        return aliasInput.getValue();
    }

    @Override
    public String getSelectedClassName() {
        Option selectedItem = classNameSelect.getSelectedItem();
        return selectedItem != null ? selectedItem.getValue() : null;
    }

    @Override
    public void hideAliasValidationError() {
        aliasInputHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        StyleHelper.addUniqueEnumStyleName( aliasFormGroup,
                                            ValidationState.class,
                                            ValidationState.NONE );
    }

    @Override
    public void showAliasValidationError() {
        aliasInputHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        StyleHelper.addUniqueEnumStyleName( aliasFormGroup,
                                            ValidationState.class,
                                            ValidationState.ERROR );
    }

    @Override
    public void hideClassNameValidationError() {
        classNameSelectHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        StyleHelper.addUniqueEnumStyleName( classNameFormGroup,
                                            ValidationState.class,
                                            ValidationState.NONE );
    }

    @Override
    public void showClassNameValidationError() {
        classNameSelectHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        StyleHelper.addUniqueEnumStyleName( classNameFormGroup,
                                            ValidationState.class,
                                            ValidationState.ERROR );
    }

    @Override
    public void clear() {
        hideAliasValidationError();
        hideClassNameValidationError();
        aliasInput.setValue( "" );
        classNameSelect.clear();
    }

    @Override
    public void show() {
        modal = new BaseModal();
        modal.setTitle( translationService.getTranslation( GlobalsEditorConstants.AddGlobalPopupTitle ) );
        modal.setBody( ElementWrapperWidget.getWidget( view ) );
        modal.show();
    }

    @Override
    public void hide() {
        if ( modal != null ) {
            modal.hide();
        }
    }

    @EventHandler("aliasInput")
    public void aliasInputChanged( final ChangeEvent changeEvent ) {
        presenter.onAliasInputChanged();
    }

    @EventHandler("classNameSelect")
    public void classNameSelectChanged( final ChangeEvent changeEvent ) {
        presenter.onClassNameSelectChanged();
    }

    @EventHandler("addButton")
    public void addButtonClicked( final ClickEvent clickEvent ) {
        presenter.onAddButtonClicked();
    }

    @EventHandler("cancelButton")
    public void cancelButtonClicked( final ClickEvent clickEvent ) {
        presenter.onCancelButtonClicked();
    }
}
