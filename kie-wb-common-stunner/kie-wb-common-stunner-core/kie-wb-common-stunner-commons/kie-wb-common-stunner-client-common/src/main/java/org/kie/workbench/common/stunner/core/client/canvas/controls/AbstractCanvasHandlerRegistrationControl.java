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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCanvasHandlerRegistrationControl extends AbstractCanvasHandlerControl
        implements CanvasRegistationControl<AbstractCanvasHandler, Element> {

    private final Map<String, ViewHandler<?>> handlers = new HashMap<>();

    public void update( final Element element ) {
        // Do nothing by default.
    }

    protected void registerHandler( String uuid, ViewHandler<?> handler ) {
        handlers.put( uuid, handler );
    }

    @Override
    protected void doDisable() {
        // De-register all drag handlers.
        for ( Map.Entry<String, ViewHandler<?>> entry : handlers.entrySet() ) {
            final String uuid = entry.getKey();
            final Shape shape = canvasHandler.getCanvas().getShape( uuid );
            final ViewHandler<?> handler = entry.getValue();
            doDeregisterHandler( shape, handler );
        }

    }

    @Override
    public void deregister( final Element element ) {
        handlers.remove( element.getUUID() );

    }

    protected void doDeregisterHandler( final Shape shape,
                                        final ViewHandler<?> handler ) {
        if ( null != shape && null != handler ) {
            final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shape.getShapeView();
            hasEventHandlers.removeHandler( handler );
        }

    }

    public void deregisterAll() {
        handlers.clear();

    }

    protected boolean checkEventContext( final AbstractCanvasHandlerEvent canvasHandlerEvent ) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return canvasHandler != null && canvasHandler.equals( _canvasHandler );
    }

}
