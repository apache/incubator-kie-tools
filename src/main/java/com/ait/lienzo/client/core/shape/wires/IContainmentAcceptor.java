package com.ait.lienzo.client.core.shape.wires;

public interface IContainmentAcceptor
{
    static IContainmentAcceptor DEFAULT = new DefaultContainmentAcceptor();

    boolean containmentAllowed(WiresContainer parent, WiresShape child);

    boolean acceptContainment(WiresContainer parent, WiresShape child);

    public static class DefaultContainmentAcceptor implements IContainmentAcceptor
    {
        private DefaultContainmentAcceptor()
        {

        }

        @Override public boolean containmentAllowed(WiresContainer parent, WiresShape child)
        {
            return true;
        }

        @Override public boolean acceptContainment(WiresContainer parent, WiresShape child)
        {
            return true;
        }

    }
}
