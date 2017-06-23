/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.types;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class BoundedListIterator<T> implements Iterator<T>
{
    private int                   m_curpos;

    private final int             m_szlist;

    private final IBoundedList<T> m_listof;

    public BoundedListIterator(final IBoundedList<T> listof)
    {
        m_curpos = 0;

        m_listof = listof;

        m_szlist = listof.size();
    }

    @Override
    public final boolean hasNext()
    {
        return (m_curpos != m_szlist);
    }

    @Override
    public final T next()
    {
        final int i = m_curpos;

        if (i >= m_szlist)
        {
            throw new NoSuchElementException();
        }
        m_curpos = i + 1;

        return m_listof.get(i);
    }

    @Override
    public final void remove()
    {
        throw new UnsupportedOperationException();
    }
}
