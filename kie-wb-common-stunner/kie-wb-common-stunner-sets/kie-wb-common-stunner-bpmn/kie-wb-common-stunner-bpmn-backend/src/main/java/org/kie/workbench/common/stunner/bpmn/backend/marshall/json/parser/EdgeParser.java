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

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ArrayParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.IntegerFieldParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class EdgeParser extends ElementParser<Edge<View, Node>> {

    public EdgeParser(final String name,
                      final Edge<View, Node> element) {
        super(name,
              element);
    }

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
        // TODO: Dockers -> BPMN2 marshaller expects dockers present for sequence flows,
        // so using dummy 0,0 coordinates for now - Use magnets index or whatever can fit here.
        ObjectParser docker1ObjParser = new ObjectParser("")
                .addParser(new IntegerFieldParser("x",
                                                  0))
                .addParser(new IntegerFieldParser("y",
                                                  0));
        ObjectParser docker2ObjParser = new ObjectParser("")
                .addParser(new IntegerFieldParser("x",
                                                  0))
                .addParser(new IntegerFieldParser("y",
                                                  0));
        ArrayParser dockersParser = new ArrayParser("dockers")
                .addParser(docker1ObjParser)
                .addParser(docker2ObjParser);
        super.addParser(dockersParser);
    }
}
