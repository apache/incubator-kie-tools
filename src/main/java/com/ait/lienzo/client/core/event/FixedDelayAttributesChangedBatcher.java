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
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public final class FixedDelayAttributesChangedBatcher extends AbstractAccumulatingAttributesChangedBatcher
{
    private final int              m_timems;

    private final RepeatingCommand m_action;

    private boolean                m_refire = true;

    public FixedDelayAttributesChangedBatcher(int timems)
    {
        if (timems < 0)
        {
            timems = 0;
        }
        m_timems = timems;

        m_action = new RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                dispatch();

                m_refire = true;

                tock();

                return false;
            }
        };
    }

    @Override
    protected final void tick()
    {
        if (m_refire)
        {
            m_refire = false;

            Scheduler.get().scheduleFixedDelay(m_action, m_timems);
        }
    }

    @Override
    public final IAttributesChangedBatcher copy()
    {
        return new FixedDelayAttributesChangedBatcher(m_timems);
    }

    @Override
    public final String getName()
    {
        return "FixedDelayAttributesChangedBatcher(" + m_timems + ")";
    }
}
