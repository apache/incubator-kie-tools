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

package org.kie.workbench.common.stunner.forms.client.fields.exclusiveGateway;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.client.session.CanvasSessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.forms.client.fields.util.ContextUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Dependent
public class DefaultRouteFormProvider implements SelectorDataProvider {

    @Inject
    CanvasSessionManager canvasSessionManager;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData( FormRenderingContext context ) {
        List<Edge> outEdges = getGatewayOutEdges( context );
        Map<String, String> values = new TreeMap<>();
        if ( outEdges != null ) {
            for ( Edge edge : outEdges ) {
                SequenceFlow sequenceFlow = ( SequenceFlow ) ( ( ViewConnector ) edge.getContent() ).getDefinition();
                String name = sequenceFlow.getGeneral().getName().getValue();
                String id = edge.getUUID();
                String routeIdentifier = ( name != null && name.length() > 0 ) ? ( name + " : " + id ) : id;
                values.put( routeIdentifier, routeIdentifier );
            }
        }
        return new SelectorData( values, null );
    }

    protected List<Edge> getGatewayOutEdges( FormRenderingContext context ) {
        Object model = ContextUtils.getModel( context );
        if ( model instanceof ExclusiveDatabasedGateway ) {
            ExclusiveDatabasedGateway gateway = ( ExclusiveDatabasedGateway ) model;
            Node gatewayNode = null;
            Diagram diagram = canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram();
            Iterator<Element> it = diagram.getGraph().nodes().iterator();
            while ( it.hasNext() ) {
                Element element = it.next();
                if ( element.getContent() instanceof View ) {
                    Object oDefinition = ( ( View ) element.getContent() ).getDefinition();
                    if ( oDefinition instanceof ExclusiveDatabasedGateway ) {
                        ExclusiveDatabasedGateway elementGateway = ( ExclusiveDatabasedGateway ) oDefinition;
                        if ( elementGateway.getId() == gateway.getId() ) {
                            gatewayNode = ( Node ) element;
                            return gatewayNode.getOutEdges();
                        }
                    }
                }
            }
        }
        return null;
    }
}