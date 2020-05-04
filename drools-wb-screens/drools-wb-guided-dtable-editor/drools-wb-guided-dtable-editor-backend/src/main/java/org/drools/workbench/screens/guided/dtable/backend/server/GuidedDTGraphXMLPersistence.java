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

package org.drools.workbench.screens.guided.dtable.backend.server;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.kie.soup.xstream.XStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuidedDTGraphXMLPersistence {

    private static final Logger logger = LoggerFactory.getLogger( GuidedDTGraphXMLPersistence.class );

    private static final GuidedDTGraphXMLPersistence INSTANCE = new GuidedDTGraphXMLPersistence();

    private XStream xt;

    private GuidedDTGraphXMLPersistence() {
        xt = XStreamUtils.createTrustingXStream(new DomDriver());
        xt.alias( "graph",
                  GuidedDecisionTableEditorGraphModel.class );
        xt.alias( "entry",
                  GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry.class );
    }

    public static GuidedDTGraphXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final GuidedDecisionTableEditorGraphModel content ) {
        if ( content == null ) {
            return xt.toXML( new GuidedDecisionTableEditorGraphModel() );
        }

        try {
            return xt.toXML( content );

        } catch ( Exception e ) {
            logger.error( "Unable to marshal model. Returning a XML for empty GuidedDecisionTableEditorGraphModel.",
                          e );
        }
        return xt.toXML( new GuidedDecisionTableEditorGraphModel() );
    }

    public GuidedDecisionTableEditorGraphModel unmarshal( final String xml ) {
        if ( xml == null || xml.trim().equals( "" ) ) {
            return new GuidedDecisionTableEditorGraphModel();
        }

        try {
            final Object o = xt.fromXML( xml );
            return (GuidedDecisionTableEditorGraphModel) o;

        } catch ( Exception e ) {
            logger.error( "Unable to unmarshal content. Returning an empty GuidedDecisionTableEditorGraphModel.",
                          e );
        }
        return new GuidedDecisionTableEditorGraphModel();
    }

}
