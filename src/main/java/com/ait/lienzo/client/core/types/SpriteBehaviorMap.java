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

import com.ait.lienzo.client.core.types.BoundingBox.BoundingBoxJSO;
import com.ait.tooling.common.api.java.util.StringOps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public final class SpriteBehaviorMap
{
    private final SpriteBehaviorMapJSO m_jso;

    public SpriteBehaviorMap(final String behavior, final BoundingBox... frames)
    {
        this(SpriteBehaviorMapJSO.make());

        addBehavior(behavior, frames);
    }

    public SpriteBehaviorMap(final String behavior, final Collection<BoundingBox> list)
    {
        this(SpriteBehaviorMapJSO.make());

        addBehavior(behavior, list);
    }

    public SpriteBehaviorMap(final SpriteBehaviorMapJSO jso)
    {
        m_jso = jso;
    }

    public final SpriteBehaviorMapJSO getJSO()
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
        final BoundingBoxArrayJSO ajso = BoundingBoxArrayJSO.make();

        for (int i = 0; i < frames.length; i++)
        {
            ajso.add(frames[i].getJSO());
        }
        m_jso.put(behavior, ajso);

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
        final BoundingBoxArrayJSO ajso = BoundingBoxArrayJSO.make();

        for (BoundingBox frame : frames)
        {
            ajso.add(frame.getJSO());
        }
        m_jso.put(behavior, ajso);

        return this;
    }

    public final BoundingBox[] getFramesForBehavior(final String behavior)
    {
        BoundingBoxArrayJSO ajso = m_jso.get(StringOps.requireTrimOrNull(behavior, "behavior is null or empty"));

        if (ajso != null)
        {
            final int size = ajso.size();

            if (size > 1)
            {
                final BoundingBox[] frames = new BoundingBox[size];

                for (int i = 0; i < size; i++)
                {
                    frames[i] = new BoundingBox(ajso.get(i));
                }
                return frames;
            }
        }
        return null;
    }

    public final String toJSONString()
    {
        return new JSONObject(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (false == (other instanceof SpriteBehaviorMap)))
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

    public static final class SpriteBehaviorMapJSO extends JavaScriptObject
    {
        static final SpriteBehaviorMapJSO make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected SpriteBehaviorMapJSO()
        {
        }

        final native BoundingBoxArrayJSO get(String behavior)
        /*-{
			return this[behavior];
        }-*/;

        final native void put(String behavior, BoundingBoxArrayJSO valu)
        /*-{
			this[behavior] = valu;
        }-*/;
    }

    private static final class BoundingBoxArrayJSO extends JavaScriptObject
    {
        static final BoundingBoxArrayJSO make()
        {
            return JavaScriptObject.createArray().cast();
        }

        protected BoundingBoxArrayJSO()
        {
        }

        final native BoundingBoxJSO get(int indx)
        /*-{
			return this[indx];
        }-*/;

        final native void add(BoundingBoxJSO valu)
        /*-{
			this[this.length] = valu;
        }-*/;

        final native int size()
        /*-{
			return this.length;
        }-*/;
    }
}
