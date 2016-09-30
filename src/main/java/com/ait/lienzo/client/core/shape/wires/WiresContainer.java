/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.tooling.common.api.flow.Flows;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.Objects;

import static com.ait.lienzo.client.core.AttributeOp.any;

public class WiresContainer
{
    private static final Flows.BooleanOp                 XYWH_OP                  = any( Attribute.X, Attribute.Y );

    private NFastArrayList<WiresShape>   m_childShapes;

    private IContainer<?, IPrimitive<?>> m_container;

    private WiresContainer               m_parent;

    private IContainmentAcceptor         m_containmentAcceptor = IContainmentAcceptor.ALL;;

    private IDockingAcceptor             m_dockingAcceptor     = IDockingAcceptor.ALL;

    private WiresContainer               dockedTo;

    private final HandlerManager         m_manager;

    private boolean                      m_dragging;

    private final IAttributesChangedBatcher  attributesChangedBatcher;

    private final HandlerRegistrationManager m_registrationManager;

    public WiresContainer( final IContainer<?, IPrimitive<?>> container )
    {
        this( container, null, new HandlerRegistrationManager(), new AnimationFrameAttributesChangedBatcher() );
    }

    WiresContainer( final IContainer<?, IPrimitive<?>> container,
                    final HandlerManager m_manager,
                    final HandlerRegistrationManager m_registrationManager,
                    final IAttributesChangedBatcher  attributesChangedBatcher )
    {
        this.m_container = container;
        this.m_manager = null != m_manager ? m_manager : new HandlerManager(this);
        this.m_dragging = false;
        this.m_childShapes = new NFastArrayList<WiresShape>();
        this.m_registrationManager = m_registrationManager;
        this.attributesChangedBatcher = attributesChangedBatcher;
        init();
    }

    private void init()
    {
        if ( null != m_container ) {

            m_registrationManager.register(
                    m_container.addNodeDragStartHandler( new NodeDragStartHandler() {
                        @Override
                        public void onNodeDragStart( final NodeDragStartEvent event ) {
                            WiresContainer.this.m_dragging = true;
                            m_manager.fireEvent(
                                    new WiresDragStartEvent(
                                            WiresContainer.this,
                                            event ) );
                        }
                    } )
            );

            m_registrationManager.register(
                    m_container.addNodeDragMoveHandler( new NodeDragMoveHandler() {
                        @Override
                        public void onNodeDragMove( final NodeDragMoveEvent event ) {
                            WiresContainer.this.m_dragging = true;
                            m_manager.fireEvent(
                                    new WiresDragMoveEvent(
                                            WiresContainer.this,
                                            event ) );
                        }
                    } )
            );

            m_registrationManager.register(
                    m_container.addNodeDragEndHandler( new NodeDragEndHandler() {
                        @Override
                        public void onNodeDragEnd( final NodeDragEndEvent event ) {
                            WiresContainer.this.m_dragging = false;
                            m_manager.fireEvent(
                                    new WiresDragEndEvent(
                                            WiresContainer.this,
                                            event ) );
                        }
                    } )
            );

            m_container.setAttributesChangedBatcher( attributesChangedBatcher );

            final AttributesChangedHandler handler = new AttributesChangedHandler() {
                @Override
                public void onAttributesChanged( AttributesChangedEvent event ) {
                    if ( !WiresContainer.this.m_dragging && event.evaluate( XYWH_OP ) ) {
                        m_manager.fireEvent(
                                new WiresMoveEvent( WiresContainer.this,
                                        ( int ) getGroup().getX(),
                                        ( int ) getGroup().getY() ) );
                    }

                }
            };

            // Attribute change handlers.
            m_registrationManager.register(
                    m_container.addAttributesChangedHandler( Attribute.X, handler )
            );

            m_registrationManager.register(
                    m_container.addAttributesChangedHandler( Attribute.Y, handler )
            );

        }

    }

    public IContainer<?, IPrimitive<?>> getContainer()
    {
        return m_container;
    }

    public Group getGroup() {
        return getContainer().asGroup();
    }

    public void setContainer(IContainer<?, IPrimitive<?>> container)
    {
        m_container = container;
    }

    public WiresContainer getParent()
    {
        return m_parent;
    }

    public void setParent(WiresContainer parent)
    {
        m_parent = parent;
    }

    public NFastArrayList<WiresShape> getChildShapes()
    {
        return m_childShapes;
    }

    public IContainmentAcceptor getContainmentAcceptor()
    {
        return m_containmentAcceptor;
    }

    public void setContainmentAcceptor(IContainmentAcceptor containmentAcceptor)
    {
        m_containmentAcceptor = containmentAcceptor;
    }

    public IDockingAcceptor getDockingAcceptor()
    {
        return m_dockingAcceptor;
    }

    public void setDockingAcceptor(IDockingAcceptor dockingAcceptor)
    {
        m_dockingAcceptor = dockingAcceptor;
    }

    public void add(WiresShape shape)
    {
        if (shape.getParent() == this)
        {
            return;
        }
        if (shape.getParent() != null)
        {
            shape.removeFromParent();
        }

        m_childShapes.add(shape);

        m_container.add(shape.getGroup());

        shape.setParent(this);

        if (shape.getMagnets() != null)
        {
            shape.getMagnets().shapeMoved();
        }
    }

    public void remove(WiresShape shape)
    {
        if (m_childShapes != null)
        {
            m_childShapes.remove(shape);

            m_container.remove(shape.getGroup());

            shape.setParent(null);
        }
    }

    public void setDockedTo(WiresContainer dockedTo)
    {
        this.dockedTo = dockedTo;
    }

    public WiresContainer getDockedTo()
    {
        return dockedTo;
    }

    public WiresContainer setDraggable( final boolean draggable )
    {

        getGroup().setDraggable(draggable);

        return this;

    }

    public final HandlerRegistration addWiresMoveHandler( final WiresMoveHandler handler )
    {
        Objects.requireNonNull( handler );

        return m_manager.addHandler( WiresMoveEvent.TYPE, handler );

    }

    public final HandlerRegistration addWiresDragStartHandler( final WiresDragStartHandler dragHandler )
    {
        Objects.requireNonNull( dragHandler );

        return m_manager.addHandler( WiresDragStartEvent.TYPE, dragHandler );

    }

    public final HandlerRegistration addWiresDragMoveHandler( final WiresDragMoveHandler dragHandler )
    {
        Objects.requireNonNull( dragHandler );

        return m_manager.addHandler( WiresDragMoveEvent.TYPE, dragHandler );

    }

    public final HandlerRegistration addWiresDragEndHandler( final WiresDragEndHandler dragHandler )
    {
        Objects.requireNonNull( dragHandler );

        return m_manager.addHandler( WiresDragEndEvent.TYPE, dragHandler );

    }

    protected void preDestroy() {

    }

    public void destroy() {
        preDestroy();
        removeHandlers();
        m_container.removeFromParent();
        m_parent = null;
    }

    private void removeHandlers()
    {
        m_registrationManager.removeHandler();
        attributesChangedBatcher.cancelAttributesChangedBatcher();
    }


    protected HandlerManager getHandlerManager() {
        return m_manager;
    }

}
