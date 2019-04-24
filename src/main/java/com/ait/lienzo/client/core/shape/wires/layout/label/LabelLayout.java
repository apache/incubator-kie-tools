/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.layout.label;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Direction;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.HorizontalAlignment;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.ReferencePosition;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.VerticalAlignment;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints.Type;

public class LabelLayout
{
    private DirectionLayout m_directionLayout;
    private SizeConstraints m_sizeConstraints;

    protected LabelLayout(final DirectionLayout directionLayout, final SizeConstraints sizeConstraints)
    {
        m_directionLayout = directionLayout;
        m_sizeConstraints = sizeConstraints;
    }

    public SizeConstraints getSizeConstraints()
    {
        return m_sizeConstraints;
    }

    public DirectionLayout getDirectionLayout()
    {
        return m_directionLayout;
    }

    @Override public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof LabelLayout))
        {
            return false;
        }
        final LabelLayout that = (LabelLayout) o;
        return Objects.equals(getDirectionLayout(), that.getDirectionLayout()) &&
               Objects.equals(getSizeConstraints(), that.getSizeConstraints());
    }

    @Override public int hashCode()
    {
        return Objects.hash(getDirectionLayout(), getSizeConstraints());
    }

    public static class Builder
    {
        private       HorizontalAlignment    m_horizontalAlignment = HorizontalAlignment.CENTER;
        private       VerticalAlignment      m_verticalAlignment   = VerticalAlignment.MIDDLE;
        private       ReferencePosition      m_referencePosition   = ReferencePosition.INSIDE;
        private       Orientation            m_orientation         = Orientation.HORIZONTAL;
        private final Map<Direction, Double> m_margins             = new HashMap<>();
        private       SizeConstraints        m_sizeConstraints     = new SizeConstraints(100, 100, Type.PERCENTAGE);

        public Builder horizontalAlignment(final HorizontalAlignment horizontalAlignment)
        {
            m_horizontalAlignment = horizontalAlignment;
            return this;
        }

        public Builder verticalAlignment(final VerticalAlignment verticalAlignment)
        {
            m_verticalAlignment = verticalAlignment;
            return this;
        }

        public Builder orientation(final Orientation orientation)
        {
            m_orientation = orientation;
            return this;
        }

        public Builder referencePosition(final ReferencePosition referencePosition)
        {
            m_referencePosition = referencePosition;
            return this;
        }

        public Builder margin(final Direction direction, final Double value)
        {
            m_margins.put(direction, value);
            return this;
        }

        public Builder margins(final Map<Direction, Double> margins)
        {
            m_margins.putAll(margins);
            return this;
        }

        public Builder sizeConstraints(final SizeConstraints sizeConstraints)
        {
            m_sizeConstraints = sizeConstraints;
            return this;
        }

        public LabelLayout build()
        {
            final DirectionLayout directionLayout = new DirectionLayout.Builder()
                    .horizontalAlignment(m_horizontalAlignment).verticalAlignment(m_verticalAlignment)
                    .orientation(m_orientation).referencePosition(m_referencePosition).margins(m_margins).build();
            final Double marginX = m_margins.get(HorizontalAlignment.LEFT) != null ?
                    m_margins.get(HorizontalAlignment.LEFT) :
                    m_margins.get(HorizontalAlignment.RIGHT);
            final Double marginY = m_margins.get(VerticalAlignment.TOP) != null ?
                    m_margins.get(VerticalAlignment.TOP) :
                    m_margins.get(VerticalAlignment.BOTTOM);

            final SizeConstraints sizeConstraints = (m_sizeConstraints == null) ?
                    new SizeConstraints() :
                    new SizeConstraints(m_sizeConstraints.getWidth(), m_sizeConstraints.getHeight(),
                            m_sizeConstraints.getType(), marginX != null ? marginX : 0, marginY != null ? marginY : 0);
            return new LabelLayout(directionLayout, sizeConstraints);
        }
    }
}