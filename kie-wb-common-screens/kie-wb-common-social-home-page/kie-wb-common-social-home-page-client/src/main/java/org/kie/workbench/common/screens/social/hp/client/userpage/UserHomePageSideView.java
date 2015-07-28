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

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class UserHomePageSideView extends Composite implements UserHomePageSidePresenter.View {

    interface Binder extends UiBinder<Widget, UserHomePageSideView> {}

    private static Binder uiBinder = GWT.create( Binder.class );

    private UserHomePageSidePresenter presenter = null;

    @UiField
    Column search;

    @UiField
    Column user;

    SearchWidget searchWidget;

    @AfterInitialization
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final UserHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupUserInfo( String userName,
                               SideUserInfoPresenter sideUserInfoPresenter ) {
        user.clear();
        user.add( sideUserInfoPresenter.getView() );
    }

    @Override
    public void setupSearchPeopleMenu( final Set<String> users,
                                       final ParameterizedCommand<String> onSelect,
                                       final String suggestText ) {

        search.clear();
        searchWidget = GWT.create( SearchWidget.class );
        searchWidget.init( users, onSelect, suggestText );
        search.add( searchWidget );
    }

    @Override
    public void setupHomeLink( Anchor anchor ) {
        Paragraph p = GWT.create( Paragraph.class );
        p.add( anchor );
        user.add( p );
    }

    @Override
    public void clear() {
        user.clear();
        searchWidget.clear();
    }

}
