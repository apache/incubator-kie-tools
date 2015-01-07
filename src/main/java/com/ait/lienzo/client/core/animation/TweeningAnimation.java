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

package com.ait.lienzo.client.core.animation;

import java.util.ArrayList;

import com.ait.lienzo.client.core.shape.Node;

public class TweeningAnimation extends TimedAnimation
{
    private final AnimationTweener       m_tweener;

    private final AnimationProperties    m_properties;

    private ArrayList<AnimationProperty> m_workingset;

    public TweeningAnimation(Node<?> node, AnimationTweener tweener, AnimationProperties properties, double duration, IAnimationCallback callback)
    {
        super(duration, callback);

        setNode(node);

        m_tweener = tweener;

        m_properties = properties;
    }

    @Override
    public IAnimation doStart()
    {
        if (null != m_properties)
        {
            m_workingset = new ArrayList<AnimationProperty>();

            for (AnimationProperty property : m_properties.getProperties())
            {
                if (property.init(getNode()))
                {
                    m_workingset.add(property);
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

        return super.doClose();
    }

    private void apply(double percent)
    {
        if (null != m_tweener)
        {
            percent = m_tweener.tween(percent);
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
                    boolean good = m_workingset.get(i).apply(node, percent);

                    draw = (draw || good);
                }
                if (draw)
                {
                    LayerRedrawManager.get().schedule(node.getLayer());
                }
            }
        }
    }
}
