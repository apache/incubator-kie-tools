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

package org.kie.workbench.common.screens.social.hp.client.userpage.main.header;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.kie.uberfire.social.activities.client.widgets.userbox.UserBoxView;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderView extends Composite
        implements HeaderPresenter.View {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, HeaderView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    @UiField
    FlowPanel friendsList;

    public HeaderView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void addConnection( SocialUser follower,
                               UserBoxView.RelationType relationType,
                               Image connection,
                               ParameterizedCommand<String> clickCommand,
                               final ParameterizedCommand<String> followUnfollowCommand ) {
        UserBoxView followerView = GWT.create( UserBoxView.class );
        followerView.init( follower, relationType, clickCommand, followUnfollowCommand );
        friendsList.add( followerView.asWidget() );
    }

    @Override
    public void clear() {
        friendsList.clear();
    }

    @Override
    public void noConnection() {
        friendsList.add( new Paragraph( "There are no social connections...yet!" ) );
    }

}