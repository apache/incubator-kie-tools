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

package org.uberfire.client.editors.admin1;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class MyAdminAreaView extends Composite
    implements
    RequiresResize,
    MyAdminAreaPresenter.View {

    interface MyAdminAreaViewBinder
            extends
            UiBinder<Widget, MyAdminAreaView> {
    }

    private static MyAdminAreaViewBinder uiBinder = GWT.create( MyAdminAreaViewBinder.class );

    @UiField
    public Label                         nameLabel;

    @UiField
    public HTMLPanel                     panel;

    @SuppressWarnings("unused")
    private MyAdminAreaPresenter         presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init(final MyAdminAreaPresenter presenter) {
        this.presenter = presenter;
    }

    public void setName(final String name) {
        nameLabel.setText( name );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize( width,
                            height );
    }
}