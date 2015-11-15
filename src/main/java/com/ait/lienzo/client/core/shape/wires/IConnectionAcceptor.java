package com.ait.lienzo.client.core.shape.wires;

public interface IConnectionAcceptor
{
    static IConnectionAcceptor DEFAULT = new DefaultConnectionAcceptor();


    boolean acceptHead(WiresConnection head, WiresMagnet magnet);
    boolean acceptTail(WiresConnection tail, WiresMagnet magnet);

    boolean headConnectionAllowed(WiresConnection head, WiresShape shape);
    boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape);

    public static class DefaultConnectionAcceptor implements IConnectionAcceptor
    {
        private DefaultConnectionAcceptor()
        {

        }

        @Override public boolean tailConnectionAllowed(WiresConnection connection, WiresShape shape)
        {
            return true;
        }

        @Override public boolean headConnectionAllowed(WiresConnection connection, WiresShape shape)
        {
            return true;
        }

        @Override public boolean acceptHead(WiresConnection connection, WiresMagnet magnet)
        {
            return true;
        }

        @Override public boolean acceptTail(WiresConnection connection, WiresMagnet magnet)
        {
            return true;
        }

    }
}
