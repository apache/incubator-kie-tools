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

package com.ait.lienzo.tools.common.api.types;

public class Activatable implements IActivatable
{
    private boolean m_active;

    public Activatable()
    {
        this(false);
    }

    public Activatable(final boolean active)
    {
        m_active = active;
    }

    @Override
    public boolean isActive()
    {
        return m_active;
    }

    @Override
    public boolean setActive(final boolean active)
    {
        if (active != m_active)
        {
            m_active = active;

            return true;
        }
        return false;
    }
}
