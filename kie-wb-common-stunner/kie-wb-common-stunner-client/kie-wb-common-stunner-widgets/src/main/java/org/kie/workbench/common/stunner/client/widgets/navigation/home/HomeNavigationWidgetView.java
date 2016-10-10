/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.navigation.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.navigation.home.item.HomeNavigationItem;

import javax.enterprise.context.Dependent;

@Dependent
public class HomeNavigationWidgetView extends Composite implements HomeNavigationWidget.View {

    interface ViewBinder extends UiBinder<Widget, HomeNavigationWidgetView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private HomeNavigationWidget presenter;

    @UiField
    FlowPanel rootPanel;

    @UiField
    Icon addIcon;

    @UiField
    PanelGroup mainPanel;

    @Override
    public void init( final HomeNavigationWidget presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        addIcon.addClickHandler( event -> presenter.onButtonClick() );
    }

    @Override
    public HomeNavigationWidget.View setIcon( final IconType iconType ) {
        addIcon.setType( iconType );
        return this;
    }

    @Override
    public HomeNavigationWidget.View setIconTitle( final String text ) {
        addIcon.setTitle( text );
        return this;
    }

    @Override
    public HomeNavigationWidget.View add( final HomeNavigationItem.View view ) {
        mainPanel.add( view );
        return this;
    }

    @Override
    public HomeNavigationWidget.View clear() {
        mainPanel.clear();
        return this;
    }

}
