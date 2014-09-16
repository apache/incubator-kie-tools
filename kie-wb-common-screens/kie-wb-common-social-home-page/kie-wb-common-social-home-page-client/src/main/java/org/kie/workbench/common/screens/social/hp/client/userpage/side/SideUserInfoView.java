/*
 * Copyright 2012 JBoss Inc
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

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.model.SocialUser;

@Dependent
public class SideUserInfoView extends Composite
        implements SideUserInfoPresenter.View {

    interface SideUserInfoBinder
            extends
            UiBinder<Widget, SideUserInfoView> {

    }

    @UiField
    Paragraph userName;

    @UiField
    Paragraph email;

    @UiField
    FlowPanel action;

    @UiField
    FlowPanel userPanel;

    private static SideUserInfoBinder uiBinder = GWT.create( SideUserInfoBinder.class );

    public SideUserInfoView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setUserPanel( Image userPanel ) {
        this.userPanel.add( userPanel );
    }

    @Override
    public void setUserInfo( SocialUser socialUser ) {
        userName.setText( "User name: " + socialUser.getUserName() );
        email.setText( "E-mail: " + socialUser.getEmail() );
    }

    @Override
    public void setupLink( Button followUnfollow ) {
        action.add( followUnfollow );
    }

    @Override
    public void clear() {
        userPanel.clear();
        action.clear();
        userName.clear();
        email.clear();
    }
}