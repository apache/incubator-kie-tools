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

public class LienzoPanelResizeEvent
        extends AbstractLienzoPanelEvent<LienzoPanelResizeEventHandler>
{
    public static final Type<LienzoPanelResizeEventHandler> TYPE = new Type<>();

    private final double m_width;

    private final double m_height;

    public LienzoPanelResizeEvent(final double m_width,
                                  final double m_height)
    {
        this.m_width = m_width;
        this.m_height = m_height;
    }

    @Override
    public Type<LienzoPanelResizeEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final LienzoPanelResizeEventHandler handler)
    {
        handler.onResize(this);
    }

    public double getWidth()
    {
        return m_width;
    }

    public double getHeight()
    {
        return m_height;
    }
}
