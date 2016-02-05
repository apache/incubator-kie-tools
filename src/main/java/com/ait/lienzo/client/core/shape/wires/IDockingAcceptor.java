package com.ait.lienzo.client.core.shape.wires;

public interface IDockingAcceptor {

    IDockingAcceptor DEFAULT = new DefaultDockingAcceptor();

    boolean dockingAllowed(WiresContainer parent, WiresShape child, WiresShape target);

    boolean acceptDocking(WiresContainer parent, WiresShape child, WiresShape target);

    class DefaultDockingAcceptor implements IDockingAcceptor {
        @Override
        public boolean dockingAllowed(WiresContainer parent, WiresShape child, WiresShape target) {
            return true;
        }

        @Override
        public boolean acceptDocking(WiresContainer parent, WiresShape child, WiresShape target) {
            return true;
        }
    }

}
