package com.ait.lienzo.client.core.event;

import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.tools.client.event.INodeEvent;

public interface EventReceiver {

    <H extends EventHandler, S> void fireEvent(final INodeEvent<H, S> event);
}
