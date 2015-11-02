/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.event;

import com.ait.tooling.nativetools.client.collection.NFastStringSet;

public abstract class AbstractAccumulatingAttributesChangedBatcher implements IAttributesChangedBatcher
{
    private AttributesChangedManager m_manager = null;

    private NFastStringSet           m_changed = new NFastStringSet();

    private long                     m_begtime = 0L;

    private long                     m_endtime = 0L;

    protected AbstractAccumulatingAttributesChangedBatcher()
    {
    }

    @Override
    public final void bufferAttributeWithManager(final String name, final AttributesChangedManager manager)
    {
        m_manager = manager;

        m_changed.add(name);

        final long time = System.currentTimeMillis();

        if (0L == m_begtime)
        {
            m_begtime = time;
        }
        if (time > m_endtime)
        {
            m_endtime = time;
        }
        tick();
    }

    @Override
    public final void cancelAttributesChangedBatcher()
    {
        m_begtime = 0L;

        m_endtime = 0L;

        m_manager = null;
    }

    protected final void tock()
    {
        if ((null != m_manager) && (false == m_changed.isEmpty()))
        {
            tick();
        }
    }

    protected final void dispatch()
    {
        if ((null != m_manager) && (false == m_changed.isEmpty()))
        {
            final NFastStringSet changed = new NFastStringSet(m_changed);

            m_changed = new NFastStringSet();

            final long begtime = m_begtime;

            final long endtime = m_endtime;

            final AttributesChangedManager manager = m_manager;

            m_begtime = 0L;

            m_endtime = 0L;

            m_manager = null;

            manager.fireChanged(changed, begtime, endtime);
        }
    }

    protected abstract void tick();
}
