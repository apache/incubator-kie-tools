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

package com.ait.lienzo.client.core.mediator;

import java.util.Iterator;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.NFastArrayListIterator;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelHandlerManager;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import elemental2.dom.UIEvent;

/**
 * Mediators maintains a list (or stack) of {@link IMediator} instances.
 * <p>
 * These mediators can be used to intercept the events in the
 * {@link Viewport} of a {@link LienzoPanel}.
 * Mediators are typically used for zooming or rubberbanding operations.
 * <p>
 * The mediators are processed in the order of the (internal) list.
 * To insert a new mediator into the start of the list (at position 0),
 * use the {@link #push(IMediator) push} method.
 * To remove the first one, use the {@link #pop() pop} method.
 * The {@link #add(int, IMediator) addBoundingBox} and {@link #remove(IMediator) remove} methods can be used for more finer grained control.
 * <p>
 * Each IMediator must implement two methods:
 * <ul>
 * <li>{@link IMediator#handleEvent(GwtEvent) handleEvent(GwtEvent)} - acts on the event if needed, and returns true if it did
 * <li>{@link IMediator#cancel() cancel()} - terminates the current operation and resets the internal state of the mediator for future use
 * </ul>
 * <p>
 * See the built-in mediators:
 * <ul>
 * <li>{@link MouseBoxZoomMediator}
 * <li>{@link MouseWheelZoomMediator}
 * <li>{@link MouseSwipeZoomMediator}
 * </ul>
 *
 * @see LienzoPanelHandlerManager
 * @see IMediator
 * @see Viewport#pushMediator(IMediator)
 * @see Viewport#getMediators()
 * @since 1.1
 */
public final class Mediators implements Iterable<IMediator> {

    private final Viewport m_viewport;

    private int m_size = 0;

    private boolean m_enabled = true;

    private final NFastArrayList<IMediator> m_mediators = new NFastArrayList<>();

    public Mediators(final Viewport viewport) {
        m_viewport = viewport;
    }

    public void push(final IMediator mediator) {
        if (null != mediator) {
            if (mediator instanceof AbstractMediator) {
                ((AbstractMediator) mediator).setViewport(m_viewport);
            }
            m_mediators.push(mediator);

            m_size = m_mediators.size();
        }
    }

    public IMediator pop() {
        if (m_size == 0) {
            return null;
        }
        final IMediator last = m_mediators.shift();

        m_size = m_mediators.size();

        return last;
    }

    public void add(final int index, final IMediator mediator) {
        if (null != mediator) {
            if (mediator instanceof AbstractMediator) {
                ((AbstractMediator) mediator).setViewport(m_viewport);
            }
            m_mediators.splice(index, 0, mediator);

            m_size = m_mediators.size();
        }
    }

    public boolean remove(final IMediator mediator) {
        final boolean removed = m_mediators.contains(mediator);

        m_mediators.remove(mediator);

        m_size = m_mediators.size();

        return removed;
    }

    public <H extends EventHandler> boolean handleEvent(Type<H> type, final UIEvent event, int x, int y) {
        if ((m_size > 0) && (m_enabled)) {
            for (int i = 0; i < m_size; i++) {
                final IMediator mediator = m_mediators.get(i);

                if ((null != mediator) && (mediator.isEnabled()) && (mediator.handleEvent(type, event, x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEnabled() {
        return m_enabled;
    }

    public void setEnabled(final boolean enabled) {
        m_enabled = enabled;
    }

    @Override
    public Iterator<IMediator> iterator() {
        return new NFastArrayListIterator<>(m_mediators);
    }
}
