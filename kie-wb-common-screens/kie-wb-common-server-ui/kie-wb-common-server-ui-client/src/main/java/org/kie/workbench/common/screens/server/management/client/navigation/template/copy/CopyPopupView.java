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

package org.kie.workbench.common.screens.server.management.client.navigation.template.copy;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
@Templated
public class CopyPopupView extends Composite
        implements CopyPopupPresenter.View {

    private CopyPopupPresenter presenter;

    private final BaseModal modal;
    private final TranslationService translationService;

    @DataField("template-name-group")
    Element templateNameGroup = DOM.createDiv();

    @Inject
    @DataField("template-name-label")
    FormLabel templateNameLabel;

    @Inject
    @DataField("template-name")
    TextBox templateName;

    @Inject
    @DataField("template-name-help")
    Span templateNameHelp;

    @Inject
    public CopyPopupView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
        this.modal = new BaseModal();
    }

    @Override
    public void init( final CopyPopupPresenter presenter ) {
        this.presenter = presenter;
        templateName.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                if ( !templateName.getText().trim().isEmpty() ) {
                    StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.NONE );
                    templateNameHelp.setVisible( false );
                }
            }
        } );
        this.modal.setTitle( getCopyServerTemplatePopupTitle() );
        this.modal.setBody( this );
        this.templateNameLabel.setText( getTemplateNameLabelText() );
        this.modal.add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.save();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

    }

    @Override
    public void clear() {
        templateName.setText( "" );
        StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.NONE );
        templateNameHelp.setVisible( false );
    }

    @Override
    public String getNewTemplateName() {
        return templateName.getText();
    }

    @Override
    public void errorOnTemplateNameFromGroup() {
        StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.ERROR );
        templateNameHelp.setText( getTemplateNameEmptyMessage() );
        templateNameHelp.setVisible( true );
    }

    @Override
    public void display() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void errorOnTemplateNameFromGroup( final String message ) {
        StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.ERROR );
        templateNameHelp.setText( message );
        templateNameHelp.setVisible( true );
    }

    private String getTemplateNameLabelText() {
        return translationService.format( Constants.CopyPopupView_TemplateNameLabelText );
    }

    private String getCopyServerTemplatePopupTitle() {
        return translationService.format( Constants.CopyPopupView_CopyServerTemplatePopupTitle );
    }

    private String getTemplateNameEmptyMessage() {
        return translationService.format( Constants.CopyPopupView_TemplateNameEmptyMessage );
    }
}
