/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionChangeHandler;

public class FactTypeKnownValueChangeEvent
        extends GwtEvent<FactTypeKnownValueChangeHandler> {

    private static final GwtEvent.Type<FactTypeKnownValueChangeHandler> TYPE = new GwtEvent.Type<FactTypeKnownValueChangeHandler>();

    @Override
    protected void dispatch( FactTypeKnownValueChangeHandler handler ) {
        handler.onValueChanged( this );
    }

    @Override
    public GwtEvent.Type<FactTypeKnownValueChangeHandler> getAssociatedType() {
        return getType();
    }

    public static final Type<FactTypeKnownValueChangeHandler> getType() {
        return TYPE;
    }
}
