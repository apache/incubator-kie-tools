/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.screens.social.hp.client.userpage.side;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class EditUserForm
        extends BaseModal {

    private SocialUser socialUser;

    @UiField
    TextBox emailTextBox;

    @UiField
    TextBox realNameTextBox;

    ParameterizedCommand<SocialUser> updateCommand;

    interface Binder
            extends
            UiBinder<Widget, EditUserForm> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void init() {
        setBody( uiBinder.createAndBindUi( EditUserForm.this ) );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                save();
            }
        }, new Command() {
            @Override
            public void execute() {
                cancel();
            }
        } ) );
        setTitle( "Edit User" );
    }

    void save() {
        socialUser.setEmail( emailTextBox.getText() );
        socialUser.setRealName( realNameTextBox.getText() );
        hide();
        updateCommand.execute( socialUser );
    }

    void cancel() {
        hide();
    }

    public void show( final SocialUser socialUser,
                      final ParameterizedCommand<SocialUser> updateCommand ) {
        this.socialUser = socialUser;
        this.updateCommand = updateCommand;
        this.emailTextBox.setText( socialUser.getEmail() );
        this.realNameTextBox.setText( socialUser.getRealName() );
        show();
    }

}
