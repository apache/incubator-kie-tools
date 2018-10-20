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

import com.ait.lienzo.client.widget.panel.AbstractLienzoPanelEvent;

public class LienzoPanelScrollEvent
        extends AbstractLienzoPanelEvent<LienzoPanelScrollEventHandler>
{
    public static final Type<LienzoPanelScrollEventHandler> TYPE = new Type<>();

    private final double pctX;

    private final double pctY;

    public LienzoPanelScrollEvent(final double pctX,
                                  final double pctY)
    {
        this.pctX = pctX;
        this.pctY = pctY;
    }

    @Override
    public Type<LienzoPanelScrollEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final LienzoPanelScrollEventHandler handler)
    {
        handler.onScroll(this);
    }

    public double getPctX()
    {
        return pctX;
    }

    public double getPctY()
    {
        return pctY;
    }
}
