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

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class TweeningAnimation extends TimedAnimation
{
    private final AnimationTweener            m_tweener;

    private final AnimationProperties         m_properties;

    private NFastArrayList<AnimationProperty> m_workingset;

    private boolean                           m_refreshing = false;

    public TweeningAnimation(final Node<?> node, final AnimationTweener tweener, final AnimationProperties properties, final double duration, final IAnimationCallback callback)
    {
        super(duration, callback);

        setNode(node);

        m_tweener = tweener;

        m_properties = properties;
    }

    @Override
    public IAnimation doStart()
    {
        if ((null != m_properties) && (m_properties.size() > 0))
        {
            m_workingset = new NFastArrayList<>();

            final Node<?> node = getNode();

            final int size = m_properties.size();

            for (int i = 0; i < size; i++)
            {
                AnimationProperty property = m_properties.get(i);

                if (null != property)
                {
                    if (property.isStateful())
                    {
                        property = property.copy();
                    }
                    if (null != property)
                    {
                        if (property.init(node))
                        {
                            m_workingset.add(property);

                            m_refreshing = m_refreshing || property.isRefreshing();
                        }
                    }
                }
            }
        }
        apply(0.0);

        return super.doStart();
    }

    @Override
    public IAnimation doFrame()
    {
        apply(getPercent());

        return super.doFrame();
    }

    @Override
    public IAnimation doClose()
    {
        apply(1.0);

        m_workingset = null;

        m_refreshing = false;

        return super.doClose();
    }

    private void apply(double percent)
    {
        if (null != m_tweener)
        {
            percent = m_tweener.apply(percent);
        }
        if (null != m_workingset)
        {
            final int size = m_workingset.size();

            if (size > 0)
            {
                boolean draw = false;

                final Node<?> node = getNode();

                for (int i = 0; i < size; i++)
                {
                    final boolean good = m_workingset.get(i).apply(node, percent);

                    draw = (draw || good);// Don't combine booleans above to avoid short-circuits. - DSJ
                }
                if (draw)
                {
                    if (m_refreshing)
                    {
                        node.refresh();
                    }
                    node.batch();
                }
            }
        }
    }
}
