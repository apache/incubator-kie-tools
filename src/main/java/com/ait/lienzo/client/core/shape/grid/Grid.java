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

import com.ait.lienzo.client.core.shape.CompositeProxy;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.shared.core.types.ProxyType;
import com.google.gwt.json.client.JSONObject;

public class Grid extends CompositeProxy<Grid, Group>
{
    private Group                      m_proxy;

    private NFastArrayList<GridColumn> m_gcols;

    public Grid()
    {
        super(ProxyType.GRID);
    }

    protected Grid(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ProxyType.GRID, node, ctx);
    }

    public List<GridColumn> getColumns()
    {
        if (null == m_gcols)
        {
            return null;
        }
        if (m_gcols.size() < 1)
        {
            return null;
        }
        return Collections.unmodifiableList(m_gcols.toList());
    }

    public Grid addColumn(final GridColumn c)
    {
        if (null == m_gcols)
        {
            m_gcols = new NFastArrayList<GridColumn>();
        }
        if (null != c)
        {
            if (false == m_gcols.contains(c))
            {
                m_gcols.add(c);
            }
        }
        return this;
    }

    public Grid addColumns(final GridColumn c, final GridColumn... cols)
    {
        addColumn(c);

        for (GridColumn g : cols)
        {
            addColumn(g);
        }
        return this;
    }

    public Grid removeColumn(final GridColumn c)
    {
        if (null != m_gcols)
        {
            if (null != c)
            {
                if (m_gcols.contains(c))
                {
                    m_gcols.remove(c);
                }
            }
        }
        return this;
    }

    public Grid setColumns(final GridColumn c, final GridColumn... cols)
    {
        removeColumns();

        return addColumns(c, cols);
    }

    public Grid setColumns(final Collection<GridColumn> cols)
    {
        removeColumns();

        for (GridColumn c : cols)
        {
            addColumn(c);
        }
        return this;
    }

    public Grid setColumns(final Iterable<GridColumn> cols)
    {
        removeColumns();

        for (GridColumn c : cols)
        {
            addColumn(c);
        }
        return this;
    }

    public Grid removeColumns()
    {
        if (null != m_gcols)
        {
            m_gcols.clear();
            
            m_gcols = null;
        }
        return this;
    }

    @Override
    protected final Group getProxy()
    {
        if (null == m_proxy)
        {
            m_proxy = new Group();
        }
        return m_proxy;
    }

    public static class GridFactory extends CompositeProxyFactory<Grid, Group>
    {
        public GridFactory()
        {
            super(ProxyType.GRID);
        }

        @Override
        public Grid create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Grid(node, ctx);
        }
    }
}
