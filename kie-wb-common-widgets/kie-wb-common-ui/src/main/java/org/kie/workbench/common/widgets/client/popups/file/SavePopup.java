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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.FormStylePopup;

import static org.kie.commons.validation.PortablePreconditions.*;

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
        setGlassEnabled( true );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                      checkInCommentTextBox );

        final HorizontalPanel hp = new HorizontalPanel();
        final Button create = new Button( CommonConstants.INSTANCE.Save() );
        create.addClickHandler( new ClickHandler() {
            public void onClick( final ClickEvent arg0 ) {
                hide();
                command.execute( checkInCommentTextBox.getText() );
            }
        } );
        hp.add( create );

        final Button cancel = new Button( CommonConstants.INSTANCE.Cancel() );
        cancel.addClickHandler( new ClickHandler() {
            public void onClick( final ClickEvent arg0 ) {
                hide();
            }
        } );
        hp.add( new HTML( "&nbsp" ) );
        hp.add( cancel );
        addAttribute( "",
                      hp );
    }

    public void show() {
        setAfterShow( new Command() {
            public void execute() {
                checkInCommentTextBox.setFocus( true );
            }
        } );
        super.show();
        checkInCommentTextBox.setFocus( true );
    }

}
