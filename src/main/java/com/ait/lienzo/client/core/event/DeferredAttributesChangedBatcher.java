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

package com.ait.lienzo.client.core.event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public final class DeferredAttributesChangedBatcher extends AbstractAccumulatingAttributesChangedBatcher
{
    private final ScheduledCommand m_action;

    private boolean                m_refire = true;

    public DeferredAttributesChangedBatcher()
    {
        m_action = new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                dispatch();

                m_refire = true;

                tock();
            }
        };
    }

    @Override
    protected final void tick()
    {
        if (m_refire)
        {
            m_refire = false;

            Scheduler.get().scheduleDeferred(m_action);
        }
    }

    @Override
    public final IAttributesChangedBatcher copy()
    {
        return new DeferredAttributesChangedBatcher();
    }

    @Override
    public final String getName()
    {
        return "DeferredAttributesChangedBatcher()";
    }
}
