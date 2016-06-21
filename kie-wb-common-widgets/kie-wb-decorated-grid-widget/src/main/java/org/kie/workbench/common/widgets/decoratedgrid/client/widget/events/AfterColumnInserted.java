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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class AfterColumnInserted
        extends GwtEvent<AfterColumnInserted.Handler> {

    private final int index;

    public AfterColumnInserted( final int index ) {
        this.index = index;
    }

    public static interface Handler
            extends
            EventHandler {

        void onAfterColumnInserted( AfterColumnInserted event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    public int getIndex() {
        return index;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( AfterColumnInserted.Handler handler ) {
        handler.onAfterColumnInserted( this );
    }

}
