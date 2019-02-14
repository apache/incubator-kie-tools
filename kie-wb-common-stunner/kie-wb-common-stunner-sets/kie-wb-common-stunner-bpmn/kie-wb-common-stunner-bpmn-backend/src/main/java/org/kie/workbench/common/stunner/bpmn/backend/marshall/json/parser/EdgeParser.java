/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser;

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ArrayParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.IntegerFieldParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class EdgeParser extends ElementParser<Edge<View, Node>> {

    public EdgeParser(final String name,
                      final Edge<View, Node> element) {
        super(name,
              element);
    }

    @Override
    protected void parseExtendedProperties(final ObjectParser propertiesParser) {
        super.parseExtendedProperties(propertiesParser);
        ViewConnector viewConnector = (ViewConnector) element.getContent();
        viewConnector.getTargetConnection()
                .ifPresent(connection -> appendConnAuto((Connection) connection,
                                                        Bpmn2OryxManager.TARGET,
                                                        propertiesParser));
        viewConnector.getSourceConnection()
                .ifPresent(connection -> appendConnAuto((Connection) connection,
                                                        Bpmn2OryxManager.SOURCE,
                                                        propertiesParser));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(final Context context) {
        super.initialize(context);
        String outNodeId = element.getTargetNode() != null ? element.getTargetNode().getUUID() : null;
        // Outgoing.
        if (null != outNodeId) {
            ArrayParser outgoingParser = new ArrayParser("outgoing");
            outgoingParser.addParser(new ObjectParser("").addParser(new StringFieldParser("resourceId",
                                                                                          outNodeId)));
            super.addParser(outgoingParser);
        }
        // Use dockers
        ArrayParser dockersParser = new ArrayParser("dockers");
        ViewConnector viewConnector = (ViewConnector) element.getContent();

        //insert source
        dockersParser.addParser(createDockerObjectParser(viewConnector.getSourceConnection()));

        //inserting ControlPoints
        final ControlPoint[] controlPoints = viewConnector.getControlPoints();
        if (null != controlPoints) {
            for (ControlPoint controlPoint : controlPoints) {
                dockersParser.addParser(createDockerObjectParser(controlPoint.getLocation()));
            }
        }

        //insert target
        dockersParser.addParser(createDockerObjectParser(viewConnector.getTargetConnection()));
        super.addParser(dockersParser);
    }

    private ObjectParser createDockerObjectParser(Optional<Connection> magnet) {
        return (magnet.isPresent() ? createDockerObjectParser(magnet.get().getLocation()) :
                createDockerObjectParser(-1, -1));
    }

    private ObjectParser createDockerObjectParser(Point2D location) {
        return (Objects.nonNull(location) ? createDockerObjectParser(Double.valueOf(location.getX()).intValue(),
                                                                     Double.valueOf(location.getY()).intValue())
                : createDockerObjectParser(-1, -1));
    }

    private ObjectParser createDockerObjectParser(final int x, final int y) {
        return new ObjectParser("")
                .addParser(new IntegerFieldParser("x", x))
                .addParser(new IntegerFieldParser("y", y));
    }

    private void appendConnAuto(final Connection connection,
                                final String type,
                                final ObjectParser propertiesParser) {
        DiscreteConnection magnetConnection = (DiscreteConnection) connection;
        if (magnetConnection.isAuto()) {
            propertiesParser.addParser(new StringFieldParser(Bpmn2OryxManager.MAGNET_AUTO_CONNECTION +
                                                                     type,
                                                             Boolean.toString(true)));
        }
    }
}
