/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser;

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ArrayParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.IntegerFieldParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class EdgeParser extends ElementParser<Edge<View, Node>> {

    public EdgeParser(final String name,
                      final Edge<View, Node> element) {
        super(name,
              element);
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
        ViewConnector viewConnector = (ViewConnector) element.getContent();
        ObjectParser docker1ObjParser = createDockerObjectParser(viewConnector.getSourceMagnet());
        ObjectParser docker2ObjParser = createDockerObjectParser(viewConnector.getTargetMagnet());
        ArrayParser dockersParser = new ArrayParser("dockers")
                .addParser(docker1ObjParser)
                .addParser(docker2ObjParser);
        super.addParser(dockersParser);
    }

    private ObjectParser createDockerObjectParser(Optional<Magnet> magnet) {
        if (magnet.isPresent() && ((Magnet) magnet.get()).getLocation() != null) {
            Point2D location = ((Magnet) magnet.get()).getLocation();
            return createDockerObjectParser(Double.valueOf(location.getX()).intValue(),
                                                        Double.valueOf(location.getY()).intValue());
        } else {
            return createDockerObjectParser(-1, -1);
        }
    }

    private ObjectParser createDockerObjectParser(final int x, final int y) {
        return new ObjectParser("")
                .addParser(new IntegerFieldParser("x",
                                                  x))
                .addParser(new IntegerFieldParser("y",
                                                  y));
    }
}
