/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.shared.GwtEvent;

public class ExpressionTypeChangeEvent extends GwtEvent<ExpressionTypeChangeHandler> {

    private static final GwtEvent.Type<ExpressionTypeChangeHandler> TYPE = new GwtEvent.Type<>();
    private String oldType;
    private String newType;

    public ExpressionTypeChangeEvent( String oldType,
                                      String newType ) {
        super();
        this.oldType = oldType;
        this.newType = newType;
    }

    public String getOldType() {
        return oldType;
    }

    public String getNewType() {
        return newType;
    }

    @Override
    protected void dispatch( ExpressionTypeChangeHandler handler ) {
        handler.onExpressionTypeChanged( this );
    }

    @Override
    public GwtEvent.Type<ExpressionTypeChangeHandler> getAssociatedType() {
        return getType();
    }

    public static final Type<ExpressionTypeChangeHandler> getType() {
        return TYPE;
    }
}
