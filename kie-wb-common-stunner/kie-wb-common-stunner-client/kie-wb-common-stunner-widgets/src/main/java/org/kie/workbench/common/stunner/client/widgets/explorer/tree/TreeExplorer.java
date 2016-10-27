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

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

// TODO: Use incremental updates, do not visit whole graph on each model update.
@Dependent
public class TreeExplorer implements IsWidget {

    private static Logger LOGGER = Logger.getLogger( TreeExplorer.class.getName() );

    public interface View extends UberView<TreeExplorer> {

        View addItem( String uuid, IsWidget itemView, boolean state );

        View addItem( String uuid, IsWidget itemView, boolean state, int... parentIdx );

        View removeItem( int index );

        View removeItem( int index, int... parentIdx );

        View clear();
    }

    DefinitionManager definitionManager;
    ChildrenTraverseProcessor childrenTraverseProcessor;
    Instance<TreeExplorerItem> treeExplorerItemInstances;
    Event<CanvasElementSelectedEvent> elementSelectedEventEvent;
    View view;

    private CanvasHandler canvasHandler;

    @Inject
    public TreeExplorer( final DefinitionManager definitionManager,
                         final ChildrenTraverseProcessor childrenTraverseProcessor,
                         final Instance<TreeExplorerItem> treeExplorerItemInstances,
                         final Event<CanvasElementSelectedEvent> elementSelectedEventEvent,
                         final View view ) {
        this.definitionManager = definitionManager;
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.treeExplorerItemInstances = treeExplorerItemInstances;
        this.elementSelectedEventEvent = elementSelectedEventEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @SuppressWarnings( "unchecked" )
    public void show( final CanvasHandler canvasHandler ) {
        this.canvasHandler = canvasHandler;
        if ( null != canvasHandler && null != canvasHandler.getDiagram() ) {
            doShow( canvasHandler.getDiagram().getGraph() );
        }
    }

    private void doShow( final Graph<org.kie.workbench.common.stunner.core.graph.content.view.View, Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> graph ) {
        traverseChildrenEdges( graph, true );
    }

    private void traverseChildrenEdges( final Graph<org.kie.workbench.common.stunner.core.graph.content.view.View, Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> graph,
                                        final boolean expand ) {
        assert graph != null;
        clear();
        childrenTraverseProcessor.traverse( graph, new AbstractChildrenTraverseCallback<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>, Edge<Child, Node>>() {

            Node parent = null;
            int level = 0;
            final List<Integer> levelIdx = new LinkedList<Integer>();

            @Override
            public void startEdgeTraversal( final Edge<Child, Node> edge ) {
                super.startEdgeTraversal( edge );
                final Node newParent = edge.getSourceNode();
                assert newParent != null;
                if ( null == parent || ( !parent.equals( newParent ) ) ) {
                    level++;
                }
                this.parent = edge.getSourceNode();
            }

            @Override
            public void endEdgeTraversal( final Edge<Child, Node> edge ) {
                super.endEdgeTraversal( edge );
                final Node newParent = edge.getSourceNode();
                assert newParent != null;
                if ( !parent.equals( newParent ) ) {
                    level--;
                    this.parent = newParent;
                }
            }

            @Override
            public void startGraphTraversal( final Graph<DefinitionSet, Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> graph ) {
                super.startGraphTraversal( graph );
                levelIdx.clear();
                levelIdx.add( -1 );
            }

            @Override
            public boolean startNodeTraversal( final Iterator<Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>> parents,
                                               final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node ) {
                super.startNodeTraversal( parents, node );
                onStartNodeTraversal( node );
                return true;
            }

            @Override
            public void startNodeTraversal( final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node ) {
                super.startNodeTraversal( node );
                onStartNodeTraversal( node );
            }

            private void onStartNodeTraversal( final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node ) {
                super.startNodeTraversal( node );
                inc( levelIdx, level );
                if ( null == parent ) {
                    final TreeExplorerItem item = treeExplorerItemInstances.get();
                    view.addItem( node.getUUID(), item.asWidget(), expand );
                    item.show( node );

                } else {
                    int[] parentsIdx = getParentsIdx( levelIdx, level );
                    final TreeExplorerItem item = treeExplorerItemInstances.get();
                    view.addItem( node.getUUID(), item.asWidget(), expand, parentsIdx );
                    item.show( node );

                }

            }

        } );

    }

    private void inc( final List<Integer> levels, final int level ) {
        if ( levels.size() < ( level + 1 ) ) {
            levels.add( 0 );
        } else {
            final int idx = levels.get( level );
            levels.set( level, idx + 1 );
        }
    }

    private int[] getParentsIdx( final List<Integer> idxList, final int maxLevel ) {
        if ( !idxList.isEmpty() ) {
            final int targetPos = ( idxList.size() - ( idxList.size() - maxLevel ) ) + 1;
            final int[] resultArray = new int[ targetPos ];
            for ( int x = 0; x < targetPos; x++ ) {
                resultArray[ x ] = idxList.get( x );
            }
            return resultArray;
        }
        return new int[]{};
    }

    public void clear() {
        view.clear();
    }

    void onSelect( final String uuid ) {
        selectShape( canvasHandler.getCanvas(), uuid );
    }

    private void selectShape( final Canvas canvas, final String uuid ) {
        elementSelectedEventEvent.fire( new CanvasElementSelectedEvent( canvasHandler, uuid ) );

    }

    void onCanvasClearEvent( @Observes CanvasClearEvent canvasClearEvent ) {
        if ( canvasHandler != null && canvasHandler.getCanvas().equals( canvasClearEvent.getCanvas() ) ) {
            clear();
        }
    }

    void onCanvasElementAddedEvent( @Observes CanvasElementAddedEvent canvasElementAddedEvent ) {
        if ( checkEventContext( canvasElementAddedEvent ) ) {
            showEventGraph( canvasElementAddedEvent );
        }
    }

    void onCanvasElementRemovedEvent( @Observes CanvasElementRemovedEvent elementRemovedEvent ) {
        if ( checkEventContext( elementRemovedEvent ) ) {
            showEventGraph( elementRemovedEvent );
        }
    }

    void onCanvasElementsClearEvent( @Observes CanvasElementsClearEvent canvasClearEvent ) {
        if ( checkEventContext( canvasClearEvent ) ) {
            showEventGraph( canvasClearEvent );
        }
    }

    void onCanvasElementUpdatedEvent( @Observes CanvasElementUpdatedEvent canvasElementUpdatedEvent ) {
        if ( checkEventContext( canvasElementUpdatedEvent ) ) {
            showEventGraph( canvasElementUpdatedEvent );
        }
    }

    private boolean checkEventContext( final AbstractCanvasHandlerEvent canvasHandlerEvent ) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return canvasHandler != null && canvasHandler.equals( _canvasHandler );
    }

    private void showEventGraph( final AbstractCanvasHandlerEvent canvasHandlerEvent ) {
        doShow( canvasHandlerEvent.getCanvasHandler().getDiagram().getGraph() );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public CanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

}
