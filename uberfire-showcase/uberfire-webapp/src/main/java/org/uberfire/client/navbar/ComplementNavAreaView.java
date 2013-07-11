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

package org.uberfire.client.navbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Column;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.AppResource;
import org.uberfire.client.workbench.widgets.menu.PespectiveContextMenusPresenter;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
public class ComplementNavAreaView
        extends Composite
        implements RequiresResize,
                   ComplementNavAreaPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Panel, ComplementNavAreaView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField(provided = true)
    public Image logo;

    @UiField
    public FlowPanel contextMenuArea;

    @Inject
    private PespectiveContextMenusPresenter contextMenu;

    @PostConstruct
    public void init() {
        logo = new Image( AppResource.INSTANCE.images().ufUserLogo() );
        initWidget( uiBinder.createAndBindUi( this ) );
        contextMenuArea.add( contextMenu.getView() );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
//        panel.setPixelSize( width, height );
    }

}