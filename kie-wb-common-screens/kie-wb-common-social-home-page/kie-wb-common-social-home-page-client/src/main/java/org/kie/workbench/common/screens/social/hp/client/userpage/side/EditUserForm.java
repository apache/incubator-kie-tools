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

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class EditUserForm
        extends PopupPanel {

    private SocialUser socialUser;

    @UiField
    TextBox email;

    @UiField
    TextBox realName;

    ParameterizedCommand<SocialUser> updateCommand;

    interface Binder
            extends
            UiBinder<Widget, EditUserForm> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );

    }

    @UiHandler("update")
    void update( final ClickEvent event ) {
        socialUser.setEmail( email.getText() );
        socialUser.setRealName( realName.getText() );
        hide();
        updateCommand.execute( socialUser );
    }

    public void show( SocialUser socialUser,
                      ParameterizedCommand<SocialUser> updateCommand ) {
        this.socialUser = socialUser;
        this.updateCommand = updateCommand;
        this.email.setText( "" );
        this.realName.setText( "" );
        show();
    }

}
