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
import com.ait.lienzo.client.core.animation.AnimationScheduler;
import com.ait.lienzo.client.core.animation.AnimationScheduler.AnimationCallback;

public abstract class AbstractAnimation implements IAnimation, IAnimationHandle
{
    private Node<?>                  m_node;

    private final double             m_duration;

    private final IAnimationCallback m_callback;

    private double                   m_begtime = 0;

    private boolean                  m_running = false;

    private AnimationCallback        m_animate = null;

    protected AbstractAnimation(final double duration, final IAnimationCallback callback)
    {
        m_duration = duration;

        m_callback = callback;
    }

    protected double getBegTime()
    {
        return m_begtime;
    }

    private final AnimationCallback getAnimationCallback()
    {
        if (null == m_animate)
        {
            m_animate = new AnimationCallback()
            {
                @Override
                public void execute(double time)
                {
                    doFrame();

                    if (isRunning() && m_animate != null)
                    {
                        AnimationScheduler.get().requestAnimationFrame(m_animate);
                    }
                    else
                    {
                        doClose();
                    }
                }
            };
        }
        return m_animate;
    }

    @Override
    public IAnimationHandle run()
    {
        if (isRunning())
        {
            return this;
        }
        m_running = true;

        m_begtime = System.currentTimeMillis();

        doStart();

        AnimationScheduler.get().requestAnimationFrame(getAnimationCallback());

        return this;
    }

    @Override
    public IAnimationHandle stop()
    {
        m_running = false;

        m_animate = null;

        return this;
    }

    @Override
    public boolean isRunning()
    {
        return m_running;
    }

    @Override
    public IAnimation setNode(final Node<?> node)
    {
        m_node = node;

        return this;
    }

    @Override
    public Node<?> getNode()
    {
        return m_node;
    }

    @Override
    public double getPercent()
    {
        double duration = getDuration();

        if (duration != INDEFINITE_ANIMATION)
        {
            return (((double) System.currentTimeMillis()) - m_begtime) / m_duration;
        }
        else
        {
            return 1.0;
        }
    }

    @Override
    public double getDuration()
    {
        return m_duration;
    }

    @Override
    public IAnimation doStart()
    {
        final Node<?> node = getNode();

        if (null != node)
        {
            node.doAnimating();
        }
        if (null != m_callback)
        {
            m_callback.onStart(this, this);
        }
        return this;
    }

    @Override
    public IAnimation doFrame()
    {
        if (null != m_callback)
        {
            m_callback.onFrame(this, this);
        }
        return this;
    }

    @Override
    public IAnimation doClose()
    {
        final Node<?> node = getNode();

        if (null != node)
        {
            node.unAnimating();
        }
        if (null != m_callback)
        {
            m_callback.onClose(this, this);
        }
        return this;
    }
}
