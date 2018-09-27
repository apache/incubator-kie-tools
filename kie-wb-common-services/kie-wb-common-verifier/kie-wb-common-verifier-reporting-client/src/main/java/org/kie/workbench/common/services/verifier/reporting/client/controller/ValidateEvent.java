/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.verifier.reporting.client.controller;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

/**
 * An event to signal that the UI has changed the underlying model data
 */
public class ValidateEvent extends GwtEvent<ValidateEvent.Handler> {

    public interface Handler
            extends
            EventHandler {

        void onValidate(ValidateEvent event);

    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final List<Coordinate> updates;

    public ValidateEvent() {
        updates = Collections.emptyList();
    }

    public ValidateEvent( List<Coordinate> updates ) {
        this.updates = updates;
    }

    public List<Coordinate> getUpdates() {
        return this.updates;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( ValidateEvent.Handler handler ) {
        handler.onValidate( this );
    }

}
