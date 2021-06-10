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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

import com.ait.lienzo.client.core.animation.AnimationScheduler.AnimationCallback;

import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

public final class LayerRedrawManager
{
    private static final LayerRedrawManager INSTANCE = new LayerRedrawManager();

    private final AnimationCallback         m_redraw;

    private NFastArrayList<Layer>           m_layers = new NFastArrayList<>();

    public static final LayerRedrawManager get()
    {
        return INSTANCE;
    }

    private LayerRedrawManager()
    {
        m_redraw = time -> {
            final int size = m_layers.size();

            if (size > 0)
            {
                final NFastArrayList<Layer> list = m_layers;

                m_layers = new NFastArrayList<Layer>();

                for (int i = 0; i < size; i++)
                {
                    list.get(i).unBatchScheduled().draw();
                }
            }
        };
    }

    public final Layer schedule(final Layer layer)
    {
        if ((null != layer) && (!layer.isBatchScheduled()))
        {
            if (!m_layers.contains(layer))
            {
                m_layers.add(layer.doBatchScheduled());
                kick(layer.getElement());
            }
        }
        return layer;
    }

    private void kick(HTMLElement layerElement)
    {
        if (!m_layers.isEmpty())
        {
            // @TODO check this works (mdp)
            AnimationScheduler.get().requestAnimationFrame(m_redraw, Js.uncheckedCast(layerElement));
        }
    }
}
