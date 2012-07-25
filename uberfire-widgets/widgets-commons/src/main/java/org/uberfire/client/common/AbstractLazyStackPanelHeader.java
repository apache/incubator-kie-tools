/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.common;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AbstractLazyStackPanelHeader extends SimplePanel
    implements
    HasCloseHandlers<AbstractLazyStackPanelHeader>,
    HasOpenHandlers<AbstractLazyStackPanelHeader> {

    protected boolean expanded = false;

    public HandlerRegistration addOpenHandler(OpenHandler<AbstractLazyStackPanelHeader> handler) {
        return addHandler( handler,
                           OpenEvent.getType() );
    }

    public HandlerRegistration addCloseHandler(CloseHandler<AbstractLazyStackPanelHeader> handler) {
        return addHandler( handler,
                           CloseEvent.getType() );
    }
    
    public abstract void expand();
    
    public abstract void collapse();

}
