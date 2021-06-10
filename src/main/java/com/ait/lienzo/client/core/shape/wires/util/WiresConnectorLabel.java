package com.ait.lienzo.client.core.shape.wires.util;

import com.ait.lienzo.client.core.shape.IDestroyable;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedHandler;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public class WiresConnectorLabel implements IDestroyable
{
    private final WiresConnector                   connector;

    private final HandlerRegistrationManager       m_registrationManager;

    private final Text                             text;

    private final BiConsumer<WiresConnector, Text> executor;

    WiresConnectorLabel(final String text,
                        final WiresConnector connector,
                        final BiConsumer<WiresConnector, Text> executor)
    {
        this(new Text(text), connector, executor, new HandlerRegistrationManager());
    }

    WiresConnectorLabel(final Text text,
                        final WiresConnector connector,
                        final BiConsumer<WiresConnector, Text> executor,
                        final HandlerRegistrationManager registrationManager)
    {
        this.connector = connector;
        this.executor = executor;
        this.m_registrationManager = registrationManager;
        this.text = text;
        init();
    }

    public WiresConnectorLabel configure(Consumer<Text> consumer)
    {
        consumer.accept(text);
        refresh();
        return this;
    }

    public WiresConnectorLabel show()
    {
        text.setAlpha(1);
        refresh();
        return this;
    }

    public WiresConnectorLabel hide()
    {
        text.setAlpha(0);
        batch();
        return this;
    }

    public Text getText()
    {
        return text;
    }

    public boolean isVisible()
    {
        return text.getAlpha() > 0;
    }

    @Override
    public void destroy()
    {
        m_registrationManager.destroy();
        text.removeFromParent();
    }

    private void init()
    {
        text.setListening(false);
        text.setDraggable(false);
        connector.getGroup().add(text);
        refresh();
        m_registrationManager.register(connector.addWiresConnectorPointsChangedHandler(pointsUpdatedHandler));
    }

    private void refresh()
    {
        executor.accept(connector, text);
        batch();
    }

    private void batch()
    {
        final Layer layer = connector.getGroup().getLayer();
        if(layer != null){
            layer.batch();
        }
    }

    private final WiresConnectorPointsChangedHandler pointsUpdatedHandler = event -> {
        if (isVisible())
        {
            refresh();
        }
    };
}
