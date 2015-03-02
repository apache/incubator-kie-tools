/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.grid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.types.NFastArrayList;

public class GridColumn
{
    private final String                   m_title;

    private final IGridColumnFieldAccessor m_field;

    private double                         m_width;

    private NFastArrayList<GridColumn>     m_scols;

    public GridColumn(final String title, final IGridColumnFieldAccessor field)
    {
        m_title = title;

        m_field = field;
    }

    public GridColumn(final String title)
    {
        this(title, null);
    }

    public String getTitle()
    {
        return m_title;
    }

    public IGridColumnFieldAccessor getField()
    {
        return m_field;
    }

    public double getWidth()
    {
        return m_width;
    }

    public GridColumn setWidth(final double width)
    {
        m_width = width;

        return this;
    }

    public List<GridColumn> getSubColumns()
    {
        if (null == m_scols)
        {
            return null;
        }
        if (m_scols.size() < 1)
        {
            return null;
        }
        return Collections.unmodifiableList(m_scols.toList());
    }

    public GridColumn addSubColumn(final GridColumn c)
    {
        if (null == m_scols)
        {
            m_scols = new NFastArrayList<GridColumn>();
        }
        if (null != c)
        {
            if (false == m_scols.contains(c))
            {
                m_scols.add(c);
            }
        }
        return this;
    }

    public GridColumn addSubColumns(final GridColumn c, final GridColumn... cols)
    {
        addSubColumn(c);

        for (GridColumn g : cols)
        {
            addSubColumn(g);
        }
        return this;
    }

    public GridColumn removeSubColumn(final GridColumn c)
    {
        if (null != m_scols)
        {
            if (null != c)
            {
                if (m_scols.contains(c))
                {
                    m_scols.remove(c);
                }
            }
        }
        return this;
    }

    public GridColumn setSubColumns(final GridColumn c, final GridColumn... cols)
    {
        removeSubColumns();

        return addSubColumns(c, cols);
    }

    public GridColumn setSubColumns(final Collection<GridColumn> cols)
    {
        removeSubColumns();

        for (GridColumn c : cols)
        {
            addSubColumn(c);
        }
        return this;
    }

    public GridColumn setSubColumns(final Iterable<GridColumn> cols)
    {
        removeSubColumns();

        for (GridColumn c : cols)
        {
            addSubColumn(c);
        }
        return this;
    }

    public GridColumn removeSubColumns()
    {
        if (null != m_scols)
        {
            m_scols.clear();

            m_scols = null;
        }
        return this;
    }
}
