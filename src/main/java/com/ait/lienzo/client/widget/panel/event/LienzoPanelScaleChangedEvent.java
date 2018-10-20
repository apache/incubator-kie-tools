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

package com.ait.lienzo.client.widget.panel.event;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.AbstractLienzoPanelEvent;

public class LienzoPanelScaleChangedEvent
        extends AbstractLienzoPanelEvent<LienzoPanelScaleChangedEventHandler>
{
    public static final Type<LienzoPanelScaleChangedEventHandler> TYPE = new Type<>();

    private final Point2D factor;

    public LienzoPanelScaleChangedEvent(final Point2D factor)
    {
        this.factor = factor;
    }

    @Override
    public Type<LienzoPanelScaleChangedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final LienzoPanelScaleChangedEventHandler handler)
    {
        handler.onScale(this);
    }

    public Point2D getFactor()
    {
        return factor;
    }
}
