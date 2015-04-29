/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.editor.commons.client.file;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Window;
import org.uberfire.ext.editor.commons.client.resources.CommonImages;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

public class CopyPopupViewImpl extends FormStylePopup implements CopyPopupView {

    private final TextBox nameTextBox = new TextBox();
    private final TextBox checkInCommentTextBox = new TextBox();
    private Presenter presenter;

    public CopyPopupViewImpl() {
        super( CommonImages.INSTANCE.edit(),
               CommonConstants.INSTANCE.CopyPopupTitle() );
        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        nameTextBox.setTitle( CommonConstants.INSTANCE.NewName() );
        nameTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.NewNameColon(),
                      nameTextBox );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                      checkInCommentTextBox );
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
    public void handleInvalidFileName( String baseFileName ) {
        Window.alert( CommonConstants.INSTANCE.InvalidFileName0( baseFileName ) );
    }
}
