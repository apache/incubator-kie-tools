/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.containment;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractContainmentBasedControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ContainmentAcceptorControlImpl extends AbstractContainmentBasedControl<AbstractCanvasHandler>
        implements ContainmentAcceptorControl<AbstractCanvasHandler> {

    private CanvasCommandFactory canvasCommandFactory;

    @Inject
    public ContainmentAcceptorControlImpl( final CanvasCommandFactory canvasCommandFactory,
                                           final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        super( canvasCommandManager );
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void doEnable( final WiresCanvas.View view ) {
        view.setContainmentAcceptor( CONTAINMENT_ACCEPTOR );
    }

    @Override
    protected void doDisable( final WiresCanvas.View view ) {
        view.setContainmentAcceptor( IContainmentAcceptor.NONE );
    }

    @Override
    protected boolean isEdgeAccepted( final Edge edge ) {
        return edge.getContent() instanceof Child;
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand( final Node parent, final Node child ) {
        return canvasCommandFactory.ADD_CHILD_EDGE( parent, child );
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand( final Node parent, final Node child ) {
        return canvasCommandFactory.DELETE_CHILD_EDGE( parent, child );
    }

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed( final WiresContainer wiresContainer,
                                           final WiresShape wiresShape ) {
            if ( !isAccept( wiresContainer, wiresShape ) ) {
                return false;
            }
            final Node childNode = WiresUtils.getNode( getCanvasHandler(), wiresShape );
            final Node parentNode = WiresUtils.getNode( getCanvasHandler(), wiresContainer );
            return allow( parentNode, childNode );
        }

        @Override
        public boolean acceptContainment( final WiresContainer wiresContainer,
                                          final WiresShape wiresShape ) {
            if ( !isAccept( wiresContainer, wiresShape ) ) {
                return false;
            }
            final Node childNode = WiresUtils.getNode( getCanvasHandler(), wiresShape );
            final Node parentNode = WiresUtils.getNode( getCanvasHandler(), wiresContainer );
            return accept( parentNode, childNode );
        }

    };

}
