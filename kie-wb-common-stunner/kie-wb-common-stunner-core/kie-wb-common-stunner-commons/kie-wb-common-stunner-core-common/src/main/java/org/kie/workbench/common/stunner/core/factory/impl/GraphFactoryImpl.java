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

package org.kie.workbench.common.stunner.core.factory.impl;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSetImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GraphFactoryImpl extends AbstractElementFactory<String, DefinitionSet, Graph<DefinitionSet, Node>>
        implements GraphFactory {

    private final DefinitionManager definitionManager;

    protected GraphFactoryImpl() {
        this.definitionManager = null;
    }

    @Inject
    public GraphFactoryImpl( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return GraphFactory.class;
    }

    @Override
    @SuppressWarnings( "unchecked   " )
    public Graph<DefinitionSet, Node> build( final String uuid,
                                             final String definitionSetId ) {
        final GraphImpl graph = new GraphImpl<>( uuid, new GraphNodeStoreImpl() );
        final DefinitionSet content = new DefinitionSetImpl( definitionSetId );
        graph.setContent( content );
        content.setBounds( new BoundsImpl(
                new BoundImpl( 0d, 0d ),
                new BoundImpl( DEFAULT_WIDTH, DEFAULT_HEIGHT )
        ) );
        return graph;
    }


}
