/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.client.console.widget.MessageTableWidget;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
@Templated
public class ValidationPopupViewImpl implements ValidationPopupView {

    @DataField("view")
    Div view;

    @DataField("validationTable")
    MessageTableWidget<ValidationMessage> validationTable;

    @DataField("yesButton")
    Button yesButton;

    @DataField("cancelButton")
    Button cancelButton;

    private Presenter presenter;

    private BaseModal modal;

    private ListDataProvider<ValidationMessage> validationTableDataProvider;

    private TranslationService translationService;

    @Inject
    public ValidationPopupViewImpl( final Div view,
                                    final Button yesButton,
                                    final Button cancelButton,
                                    final TranslationService translationService ) {
        this.view = view;
        this.yesButton = yesButton;
        this.cancelButton = cancelButton;
        this.translationService = translationService;

        this.validationTable = new MessageTableWidget<>( MessageTableWidget.Mode.PAGED );
        validationTable.setDataProvider( new ListDataProvider<>() );

        validationTable.addLevelColumn( 75,
                                        row -> {
                                            final Level level = ( (ValidationMessage) row ).getLevel();
                                            return level != null ? level : Level.ERROR;
                                        } );

        validationTable.addTextColumn( 90,
                                       row -> ( (ValidationMessage) row ).getText() );

        validationTableDataProvider = new ListDataProvider<>();
        validationTableDataProvider.addDataDisplay( validationTable );
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
    public void setYesButtonText( final String text ) {
        yesButton.setTextContent( text );
    }

    @Override
    public void setCancelButtonText( final String text ) {
        cancelButton.setTextContent( text );
    }

    @Override
    public void showYesButton( final boolean show ) {
        yesButton.getStyle().setProperty( "display", show ? "inline" : "none" );
    }

    @Override
    public void showCancelButton( final boolean show ) {
        cancelButton.getStyle().setProperty( "display", show ? "inline" : "none" );
    }

    @Override
    public void setValidationMessages( List<ValidationMessage> messages ) {
        ListDataProvider<ValidationMessage> listDataProvider = (ListDataProvider<ValidationMessage>) this.validationTable.getDataProvider();
        listDataProvider.getList().clear();
        listDataProvider.getList().addAll( messages );
        validationTable.setVisibleRangeAndClearData( new Range( 0, 5 ), true );
    }

    @Override
    public void show() {
        modal = new BaseModal();
        modal.setTitle( translationService.getTranslation( KieWorkbenchWidgetsConstants.ValidationPopupViewImpl_ValidationErrors ) );
        modal.setBody( ElementWrapperWidget.getWidget( view ) );
        modal.show();
    }

    @Override
    public void hide() {
        if ( modal != null ) {
            modal.hide();
        }
    }

    @EventHandler("yesButton")
    public void yesButtonClicked( final ClickEvent clickEvent ) {
        presenter.onYesButtonClicked();
    }

    @EventHandler("cancelButton")
    public void cancelButtonClicked( final ClickEvent clickEvent ) {
        presenter.onCancelButtonClicked();
    }
}
