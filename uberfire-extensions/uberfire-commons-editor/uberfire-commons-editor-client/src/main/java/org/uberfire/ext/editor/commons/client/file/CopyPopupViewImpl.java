/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.file;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.FormStyleItem;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
public class CopyPopupViewImpl extends FormStylePopup implements CopyPopupView {

    private final TextBox nameTextBox = new TextBox();
    private final TextBox checkInCommentTextBox = new TextBox();

    private final HelpBlock nameHelpInline = new HelpBlock();
    private final HelpBlock checkInCommentHelpInline = new HelpBlock();

    FormStyleItem nameFormStyleItem;
    FormStyleItem checkInCommentFormStyleItem;

    private Presenter presenter;

    public CopyPopupViewImpl() {
        super( CommonConstants.INSTANCE.CopyPopupTitle() );

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        nameTextBox.setTitle( CommonConstants.INSTANCE.NewName() );
        nameFormStyleItem = addAttribute( CommonConstants.INSTANCE.NewNameColon(),
                                          nameTextBox );
        nameFormStyleItem.getGroup().add( nameHelpInline );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentFormStyleItem = addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                                                    checkInCommentTextBox );
        checkInCommentFormStyleItem.getGroup().add( checkInCommentHelpInline );

        hide();

        GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.CopyPopupCreateACopy(),
                          new Command() {
                              @Override
                              public void execute() {
                                  presenter.onCopy();
                              }
                          },
                          IconType.SAVE,
                          ButtonType.PRIMARY );
        footer.addButton( CommonConstants.INSTANCE.Cancel(),
                          new Command() {
                              @Override
                              public void execute() {
                                  presenter.onCancel();
                              }
                          },
                          ButtonType.DEFAULT );
        add( footer );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getNewName() {
        return nameTextBox.getText();
    }

    @Override
    public String getCheckInComment() {
        return checkInCommentTextBox.getText();
    }

    @Override
    public void handleInvalidFileName() {
        handleFileNameValidationError( CommonConstants.INSTANCE.InvalidFileName0( getNewName() ) );
    }

    @Override
    public void handleDuplicatedFileName() {
        handleFileNameValidationError( CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( getNewName() ) );
    }

    private void handleFileNameValidationError( String message ) {
        nameFormStyleItem.getFormGroup().setValidationState( ValidationState.ERROR );
        nameHelpInline.setText( message );
    }
}
