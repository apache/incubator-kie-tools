/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.core.client.JavaScriptObject;

@StubClass("com.ait.lienzo.client.core.shape.Shape$OptionalShapeFields")
public class OptionalShapeFields extends JavaScriptObject
{
    private boolean                m_drag;

    private boolean                m_apsh;

    private DragConstraintEnforcer m_denf;

    private IControlHandleFactory  m_hand;

    public static final OptionalShapeFields make()
    {
        return new OptionalShapeFields();
    }

    protected OptionalShapeFields()
    {
    }

    public boolean isDragging()
    {
        return m_drag;
    }

    public void setDragging(final boolean drag)
    {
        m_drag = drag;
    }

    public boolean isAppliedShadow()
    {
        return m_apsh;
    }

    public void setAppliedShadow(final boolean apsh)
    {
        m_apsh = apsh;
    }

    public DragConstraintEnforcer getDragConstraintEnforcer()
    {
        return m_denf;
    }

    public void setDragConstraintEnforcer(final DragConstraintEnforcer denf)
    {
        m_denf = denf;
    }

    public IControlHandleFactory getControlHandleFactory()
    {
        return m_hand;
    }

    public void setControlHandleFactory(final IControlHandleFactory hand)
    {
        m_hand = hand;
    }
}
