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

package org.uberfire.client.workbench.widgets.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Skeleton for popups
 */
@Dependent
public class PopupView extends DialogBox {

    @PostConstruct
    public void init() {
        setAnimationEnabled( true );
        setGlassEnabled( true );
    }

    public void setContent( final IsWidget widget ) {
        setWidget( widget );
    }

    public void setTitle( final IsWidget titleWidget ) {
        setHTML( titleWidget.asWidget().getElement().getInnerHTML() );
    }

}