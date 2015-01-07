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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;

public final class LayerRedrawManager
{
    private static final LayerRedrawManager s_instance = new LayerRedrawManager();

    private NFastArrayList<Layer>            m_layers   = new NFastArrayList<Layer>();

    private AnimationCallback               m_redraw;

    public static final LayerRedrawManager get()
    {
        return s_instance;
    }

    private LayerRedrawManager()
    {
        m_redraw = new AnimationCallback()
        {
            @Override
            public void execute(double time)
            {
                final NFastArrayList<Layer> list = m_layers;

                m_layers = new NFastArrayList<Layer>();

                final int size = list.size();

                for (int i = 0; i < size; i++)
                {
                    list.get(i).draw();
                }
            }
        };
    }

    public void schedule(Layer layer)
    {
        if ((null != layer) && (false == m_layers.contains(layer)))
        {
            m_layers.add(layer);

            kick();
        }
    }

    private void kick()
    {
        if (m_layers.size() > 0)
        {
            AnimationScheduler.get().requestAnimationFrame(m_redraw);
        }
    }
}
