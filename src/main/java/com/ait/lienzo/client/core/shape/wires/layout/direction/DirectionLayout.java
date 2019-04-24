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
package com.ait.lienzo.client.core.shape.wires.layout.direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DirectionLayout
{
    public interface Direction
    {
    }

    public enum HorizontalAlignment implements Direction
    {
        RIGHT, CENTER, LEFT
    }

    public enum VerticalAlignment implements Direction
    {
        TOP, MIDDLE, BOTTOM
    }

    public enum ReferencePosition
    {
        INSIDE, OUTSIDE
    }

    public enum Orientation
    {
        HORIZONTAL, VERTICAL
    }

    private HorizontalAlignment    m_horizontalAlignment;

    private VerticalAlignment      m_verticalAlignment;

    private ReferencePosition      m_referencePosition;

    private Orientation            m_orientation;

    private Map<Direction, Double> m_margins;

    public DirectionLayout(final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment,
                           final Orientation orientation, final ReferencePosition referencePosition,
                           final Map<Direction, Double> margins)
    {
        m_horizontalAlignment = horizontalAlignment;
        m_verticalAlignment = verticalAlignment;
        m_referencePosition = referencePosition;
        m_orientation = orientation;
        m_margins = margins;
    }

    public Map<Direction, Double> getMargins()
    {
        return m_margins;
    }

    public Double getMargin(Direction direction)
    {
        final Double value = m_margins.get(direction);
        return value != null ? value : 0d;
    }

    public HorizontalAlignment getHorizontalAlignment()
    {
        return m_horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment()
    {
        return m_verticalAlignment;
    }

    public ReferencePosition getReferencePosition()
    {
        return m_referencePosition;
    }

    public Orientation getOrientation()
    {
        return m_orientation;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DirectionLayout))
        {
            return false;
        }
        final DirectionLayout that = (DirectionLayout) o;
        return getHorizontalAlignment() == that.getHorizontalAlignment() &&
               getVerticalAlignment() == that.getVerticalAlignment() &&
               getReferencePosition() == that.getReferencePosition() &&
               getOrientation() == that.getOrientation() &&
               Objects.equals(getMargins(), that.getMargins());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getHorizontalAlignment(), getVerticalAlignment(), getReferencePosition(), getOrientation(), getMargins());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DirectionLayout{");
        sb.append("m_horizontalAlignment=").append(m_horizontalAlignment);
        sb.append(", m_verticalAlignment=").append(m_verticalAlignment);
        sb.append(", m_referencePosition=").append(m_referencePosition);
        sb.append(", m_orientation=").append(m_orientation);
        sb.append(", m_margins=").append(m_margins);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder
    {
        private HorizontalAlignment    m_horizontalAlignment = HorizontalAlignment.CENTER;

        private VerticalAlignment      m_verticalAlignment   = VerticalAlignment.MIDDLE;

        private ReferencePosition      m_referencePosition   = ReferencePosition.INSIDE;

        private Orientation            m_orientation         = Orientation.HORIZONTAL;

        private Map<Direction, Double> m_margins             = new HashMap<>();

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

        public Builder margin(Direction direction, Double value)
        {
            m_margins.put(direction, value);
            return this;
        }

        public Builder margins(Map<Direction, Double> margins)
        {
            m_margins = margins;
            return this;
        }

        public DirectionLayout build()
        {
            return new DirectionLayout(m_horizontalAlignment, m_verticalAlignment, m_orientation, m_referencePosition,
                                       m_margins);
        }
    }
}
