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

package com.ait.lienzo.client.core.animation;

import java.util.Iterator;

import com.ait.lienzo.client.core.types.BoundedListIterator;
import com.ait.lienzo.client.core.types.IBoundedList;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class AnimationProperties implements IBoundedList<AnimationProperty>
{
    private final NFastArrayList<AnimationProperty> m_properties = new NFastArrayList<>();

    public static final AnimationProperties toPropertyList(final AnimationProperty property, final AnimationProperty... properties)
    {
        return new AnimationProperties(property, properties);
    }

    public AnimationProperties()
    {
    }

    public AnimationProperties(final AnimationProperty property, final AnimationProperty... properties)
    {
        push(property);

        if (null != properties)
        {
            final int size = properties.length;

            for (int i = 0; i < size; i++)
            {
                push(properties[i]);
            }
        }
    }

    @Override
    public final int size()
    {
        return m_properties.size();
    }

    @Override
    public final AnimationProperty get(final int i)
    {
        if ((i < 0) || (i >= m_properties.size()))
        {
            return null;
        }
        return m_properties.get(i);
    }

    public final AnimationProperties push(final AnimationProperty property)
    {
        if (null != property)
        {
            m_properties.add(property);
        }
        return this;
    }

    @Override
    public final Iterator<AnimationProperty> iterator()
    {
        return new BoundedListIterator<>(this);
    }

    @Override
    public final boolean isEmpty()
    {
        return m_properties.isEmpty();
    }
}
