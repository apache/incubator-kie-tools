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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;

import javax.enterprise.context.Dependent;

@Dependent
public class ToolbarViewImpl extends Composite implements ToolbarView {

    interface ViewBinder extends UiBinder<Widget, ToolbarViewImpl> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    ButtonGroup mainGroup;

    AbstractToolbar presenter;

    @Override
    public void init( final AbstractToolbar presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public ToolbarView addItem( final IsWidget w ) {
        mainGroup.add( w );
        return this;
    }

    @Override
    public ToolbarView show() {
        this.setVisible( true );
        return this;
    }

    @Override
    public ToolbarView hide() {
        this.setVisible( false );
        return this;

    }

    @Override
    public ToolbarView clear() {
        mainGroup.clear();
        return this;
    }

    @Override
    public void destroy() {
        this.removeFromParent();
    }

}
