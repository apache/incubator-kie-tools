/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class BaseScreenView extends Composite implements BaseScreen {

    interface BaseScreenViewBinder
            extends
            UiBinder<FlowPanel, BaseScreenView> {

    }

    private static BaseScreenViewBinder uiBinder = GWT.create( BaseScreenViewBinder.class );

    @UiField
    FlowPanel mainPanel;

    @UiConstructor
    public BaseScreenView() {
        init();
    }

    private void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init(IsWidget explorerWidget) {
        mainPanel.clear();
        mainPanel.add(explorerWidget);
    }
}