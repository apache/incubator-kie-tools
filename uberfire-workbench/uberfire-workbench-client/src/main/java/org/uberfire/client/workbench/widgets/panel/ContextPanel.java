/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.resources.WorkbenchResources;

public class ContextPanel extends Composite {

    private final FlowPanel container = new FlowPanel();
    private Widget widget;
    private String style;
    boolean isVisible = false;
    private UIPart uiPart;

    public ContextPanel() {
        initWidget( container );
    }

    public void toogleDisplay() {
        if ( widget == null ) {
            return;
        }
        if ( isVisible ) {
            widget.getElement().addClassName( style );
            widget.getElement().removeClassName( WorkbenchResources.INSTANCE.CSS().showContext() );
            isVisible = false;
        } else {
            widget.getElement().removeClassName( style );
            widget.getElement().addClassName( WorkbenchResources.INSTANCE.CSS().showContext() );
            isVisible = true;
        }
    }

    public void setUiPart( final UIPart uiPart ) {
        if ( uiPart != null ) {
            this.uiPart = uiPart;
            this.widget = uiPart.getWidget().asWidget();
            this.widget.getElement().getStyle().setFloat( Style.Float.LEFT );
            this.widget.getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
            this.style = this.widget.getElement().getClassName();
            container.clear();
            container.add( widget );
        } else {
            this.uiPart = null;
            this.widget = null;
            this.style = null;
            container.clear();
        }
    }

    public UIPart getUiPart() {
        return uiPart;
    }
}
