/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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
import com.google.gwt.core.client.JavaScriptObject;

public final class SpriteMap
{
    private final SpriteMapJSO m_jso;

    public SpriteMap(String name, BoundingBox frame, BoundingBox... frames)
    {
        this(SpriteMapJSO.make());

        define(name, frame, frames);
    }

    public SpriteMap(String name, Collection<BoundingBox> list)
    {
        this(SpriteMapJSO.make());

        define(name, list);
    }

    public SpriteMap(SpriteMapJSO jso)
    {
        m_jso = jso;
    }

    public final SpriteMapJSO getJSO()
    {
        return m_jso;
    }

    public final SpriteMap define(String name, BoundingBox frame, BoundingBox... frames)
    {
        BoundingBoxArrayJSO ajso = BoundingBoxArrayJSO.make();

        ajso.add(frame.getJSO());

        for (int i = 0; i < frames.length; i++)
        {
            ajso.add(frames[i].getJSO());
        }
        return this;
    }

    public final SpriteMap define(String name, Collection<BoundingBox> list)
    {
        BoundingBoxArrayJSO ajso = BoundingBoxArrayJSO.make();

        for (BoundingBox frame : list)
        {
            ajso.add(frame.getJSO());
        }
        return this;
    }

    public final BoundingBox[] getFrames(String name)
    {
        BoundingBoxArrayJSO ajso = m_jso.get(name);

        if (ajso != null)
        {
            final int size = ajso.size();

            if (size > 0)
            {
                BoundingBox[] frames = new BoundingBox[size];

                for (int i = 0; i < size; i++)
                {
                    frames[i] = new BoundingBox(ajso.get(i));
                }
                return frames;
            }
        }
        return null;
    }

    public static final class SpriteMapJSO extends JavaScriptObject
    {
        static final SpriteMapJSO make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected SpriteMapJSO()
        {
        }

        final native BoundingBoxArrayJSO get(String name)
        /*-{
             return this[name];
         }-*/;

        final native void put(String name, BoundingBoxArrayJSO valu)
        /*-{
             this[name] = valu;
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
