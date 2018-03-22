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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonUnmarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.BPMN2JsonParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.ParsingContext;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class Bpmn2Marshaller extends Bpmn2JsonUnmarshaller {

    private final DefinitionManager definitionManager;
    private final OryxManager oryxManager;

    public Bpmn2Marshaller(final DefinitionManager definitionManager,
                           final OryxManager oryxManager) {
        this.definitionManager = definitionManager;
        this.oryxManager = oryxManager;
    }

    public String marshall(final Diagram<Graph, Metadata> diagram,
                           final String preProcessingData) throws IOException {
        JBPMBpmn2ResourceImpl res = marshallToBpmn2Resource(diagram, preProcessingData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        res.save(outputStream,
                 new HashMap<>());
        return StringEscapeUtils.unescapeHtml4(outputStream.toString("UTF-8"));
    }

    public JBPMBpmn2ResourceImpl marshallToBpmn2Resource(final Diagram<Graph, Metadata> diagram,
                                                         final String preProcessingData) throws IOException {
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();
        BPMN2JsonParser parser = createParser(diagram);
        return (JBPMBpmn2ResourceImpl) super.unmarshall(parser, preProcessingData);
    }

    private BPMN2JsonParser createParser(final Diagram<Graph, Metadata> diagram) {
        return new BPMN2JsonParser(diagram,
                                   new ParsingContext(definitionManager,
                                                      oryxManager));
    }
}
