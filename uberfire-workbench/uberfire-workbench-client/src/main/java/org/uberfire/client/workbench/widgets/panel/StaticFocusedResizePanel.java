/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;

public class StaticFocusedResizePanel
extends ResizeComposite
implements HasSelectionHandlers<PartDefinition>,
HasFocusHandlers {

    private final RequiresResizeFocusPanel container = new RequiresResizeFocusPanel();

    private PartDefinition partDefinition;

    public StaticFocusedResizePanel() {
        initWidget( container );
        container.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( partDefinition != null ) {
                    SelectionEvent.fire( StaticFocusedResizePanel.this, partDefinition );
                }
            }
        } );
    }

    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        container.setWidget( part );
    }

    public void clear() {
        partDefinition = null;
        container.clear();
    }

    public void setFocus( boolean hasFocus ) {
        if ( hasFocus ) {
            //style
        } else {
            //style
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addFocusHandler( FocusHandler handler ) {
        return container.addFocusHandler( handler );
    }

    public WorkbenchPartPresenter.View getPartView() {
        return (WorkbenchPartPresenter.View) container.getWidget();
    }
}
