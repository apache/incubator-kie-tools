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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.MainPresenter;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.header.HeaderPresenter;

@Dependent
public class UserHomePageMainView extends Composite implements UserHomePageMainPresenter.View {

    interface Binder extends UiBinder<Widget, UserHomePageMainView> {
    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private UserHomePageMainPresenter presenter = null;

    @UiField
    Column header;

    @UiField
    Column main;

    @PostConstruct
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final UserHomePageMainPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setHeader( final HeaderPresenter headerPresenter ) {
        this.header.clear();
        this.header.add( headerPresenter.getView() );
    }

    @Override
    public void setMain( final MainPresenter mainPresenter ) {
        this.main.clear();
        this.main.add( mainPresenter.getView() );
    }

}
