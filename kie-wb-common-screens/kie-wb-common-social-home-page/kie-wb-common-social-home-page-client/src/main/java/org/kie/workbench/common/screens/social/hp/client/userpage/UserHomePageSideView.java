/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Well;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class UserHomePageSideView extends Composite implements UserHomePageSidePresenter.View {

    private final FlowPanel userInfoPanel = GWT.create( FlowPanel.class );

    private UserHomePageSidePresenter presenter = null;

    @AfterInitialization
    public void setup() {
        initWidget( userInfoPanel );
    }

    @Override
    public void init( final UserHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupUserInfo( String userName,
                               SideUserInfoPresenter sideUserInfoPresenter ) {
        Well userWell = GWT.create( Well.class );
        userWell.add( sideUserInfoPresenter.getView() );
        userInfoPanel.add( userWell );

    }

    @Override
    public void setupSearchPeopleMenu( final List<String> users,
                                       final ParameterizedCommand<String> onSelect,
                                       final String suggestText ) {

        SearchWidget searchWidget = GWT.create( SearchWidget.class );
        searchWidget.init( users,onSelect,suggestText );
        userInfoPanel.add( searchWidget );
    }

    @Override
    public void setupHomeLink( Anchor anchor ) {
        Paragraph p = GWT.create( Paragraph.class );
        p.add( anchor );
        userInfoPanel.add( p );
    }

    @Override
    public void clear() {
        userInfoPanel.clear();
    }

}
