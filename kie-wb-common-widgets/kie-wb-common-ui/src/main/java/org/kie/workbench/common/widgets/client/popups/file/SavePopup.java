/*
 * Copyright 2005 JBoss Inc
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

package org.kie.workbench.common.widgets.client.popups.file;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.uberfire.client.common.popups.FormStylePopup;
import org.kie.uberfire.client.common.popups.footers.GenericModalFooter;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A popup and confirmation dialog for committing an asset.
 */
public class SavePopup extends FormStylePopup {

    final private TextBox checkInCommentTextBox = new TextBox();

    public SavePopup( final CommandWithCommitMessage command ) {
        super( CommonImages.INSTANCE.edit(),
               CommonConstants.INSTANCE.SavePopupTitle() );

        checkNotNull( "command",
                      command );

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                      checkInCommentTextBox );

        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.Save(),
                          new Command() {
                              @Override
                              public void execute() {
                                  hide();
                                  command.execute( checkInCommentTextBox.getText() );
                              }
                          },
                          IconType.SAVE,
                          ButtonType.PRIMARY );
        footer.addButton( CommonConstants.INSTANCE.Cancel(),
                          new Command() {
                              @Override
                              public void execute() {
                                  hide();
                              }
                          },
                          ButtonType.DEFAULT );
        add( footer );
    }

}
