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

import java.util.Collection;

import com.ait.lienzo.tools.common.api.java.util.StringOps;

import elemental2.core.Global;
import elemental2.core.JsMap;
import elemental2.core.JsArray;

// @FIXME this needs to be tested well (mdp)
public final class SpriteBehaviorMap
{
    private final JsMap<String, JsArray<BoundingBox>> m_jso;

    public SpriteBehaviorMap(final String behavior, final BoundingBox... frames)
    {
        this(new JsMap<String, JsArray<BoundingBox>>());

        addBehavior(behavior, frames);
    }


    public SpriteBehaviorMap(final JsMap<String, JsArray<BoundingBox>>  jso)
    {
        m_jso = jso;
    }

    public final JsMap<String, JsArray<BoundingBox>>  getJSO()
    {
        return m_jso;
    }

    public final SpriteBehaviorMap addBehavior(String behavior, final BoundingBox... frames)
    {
        behavior = StringOps.requireTrimOrNull(behavior, "behavior is null or empty");

        if (null != m_jso.get(behavior))
        {
            throw new IllegalStateException("behavior " + behavior + " is already defined");
        }
        if (frames.length < 2)
        {
            throw new IllegalStateException("must be at least 2 frames for behavior " + behavior);
        }
        final JsArray<BoundingBox> ajso = new JsArray<>();

        for (int i = 0; i < frames.length; i++)
        {
            ajso.push(frames[i]);
        }
        m_jso.set(behavior, ajso);

        return this;
    }

    public final SpriteBehaviorMap addBehavior(String behavior, final Collection<BoundingBox> frames)
    {
        behavior = StringOps.requireTrimOrNull(behavior, "behavior is null or empty");

        if (null != m_jso.get(behavior))
        {
            throw new IllegalStateException("behavior " + behavior + " is already defined");
        }
        if (frames.size() < 2)
        {
            throw new IllegalStateException("must be at least 2 frames for behavior " + behavior);
        }
        final JsArray<BoundingBox> ajso = new JsArray<>();

        for (BoundingBox frame : frames)
        {
            ajso.push(frame);
        }
        m_jso.set(behavior, ajso);

        return this;
    }

    public final BoundingBox[] getFramesForBehavior(final String behavior)
    {
        JsArray<BoundingBox> ajso = m_jso.get(StringOps.requireTrimOrNull(behavior, "behavior is null or empty"));

        if (ajso != null)
        {
            final int size = ajso.getLength();

            if (size > 1)
            {
                final BoundingBox[] frames = new BoundingBox[size];

                for (int i = 0; i < size; i++)
                {
                    frames[i] = BoundingBox.fromBoundingBox(ajso.getAt(i));
                }
                return frames;
            }
        }
        return null;
    }

    public final String toJSONString()
    {
        return Global.JSON.stringify(m_jso);
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (!(other instanceof SpriteBehaviorMap)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((SpriteBehaviorMap) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

}
