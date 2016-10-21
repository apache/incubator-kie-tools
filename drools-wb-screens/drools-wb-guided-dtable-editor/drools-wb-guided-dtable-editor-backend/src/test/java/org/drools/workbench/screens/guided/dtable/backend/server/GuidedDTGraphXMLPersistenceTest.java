/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.backend.server;

import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.junit.Assert.*;

public class GuidedDTGraphXMLPersistenceTest {

    private static final String emptyModelXml = "<graph><entries/></graph>";

    @Test
    public void checkInstanceIdentity() {
        final GuidedDTGraphXMLPersistence s1 = GuidedDTGraphXMLPersistence.getInstance();
        final GuidedDTGraphXMLPersistence s2 = GuidedDTGraphXMLPersistence.getInstance();
        assertEquals( s1,
                      s2 );
    }

    @Test
    public void checkUnmarshallingNull() {
        final GuidedDecisionTableEditorGraphModel model = GuidedDTGraphXMLPersistence.getInstance().unmarshal( null );
        assertEmptyModel( model );
    }

    @Test
    public void checkUnmarshallingEmptyString() {
        final GuidedDecisionTableEditorGraphModel model = GuidedDTGraphXMLPersistence.getInstance().unmarshal( "" );
        assertEmptyModel( model );
    }

    @Test
    public void checkUnmarshallingCorruptString() {
        final GuidedDecisionTableEditorGraphModel model = GuidedDTGraphXMLPersistence.getInstance().unmarshal( "cheese" );
        assertEmptyModel( model );
    }

    @Test
    public void checkUnmarshallingEmptyModel() {
        final GuidedDecisionTableEditorGraphModel model = GuidedDTGraphXMLPersistence.getInstance().unmarshal( emptyModelXml );
        assertEmptyModel( model );
    }

    @Test
    public void checkUnmarshallingModel() {
        final GuidedDecisionTableEditorGraphModel model = GuidedDTGraphXMLPersistence.getInstance().unmarshal( emptyModelXml );
        assertEmptyModel( model );
    }

    @Test
    public void checkMarshallingNull() {
        final String xml = GuidedDTGraphXMLPersistence.getInstance().marshal( null );
        assertNotNull( xml );
        assertEqualsIgnoreWhitespace( emptyModelXml,
                                      xml );
    }

    @Test
    public void checkMarshallingModelWithEntryPosition() {
        final Path path = PathFactory.newPath( "path", "default://path" );
        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel();
        model.getEntries().add( new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry( path,
                                                                                                       path,
                                                                                                       100.0,
                                                                                                       200.0 ) );
        final String xml = GuidedDTGraphXMLPersistence.getInstance().marshal( model );
        assertNotNull( xml );
        assertEqualsIgnoreWhitespace( "<graph>\n" +
                                              "  <entries>\n" +
                                              "    <entry>\n" +
                                              "      <pathHead class=\"org.uberfire.backend.vfs.PathFactory$PathImpl\">\n" +
                                              "        <uri>default://path</uri>\n" +
                                              "        <fileName>path</fileName>\n" +
                                              "        <attributes/>\n" +
                                              "        <hasVersionSupport>false</hasVersionSupport>\n" +
                                              "      </pathHead>\n" +
                                              "      <pathVersion class=\"org.uberfire.backend.vfs.PathFactory$PathImpl\" reference=\"../pathHead\"/>\n" +
                                              "      <x>100.0</x>\n" +
                                              "      <y>200.0</y>\n" +
                                              "    </entry>\n" +
                                              "  </entries>\n" +
                                              "</graph>",
                                      xml );
    }

    @Test
    public void checkMarshallingModelWithoutEntryPosition() {
        final Path path = PathFactory.newPath( "path", "default://path" );
        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel();
        model.getEntries().add( new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry( path,
                                                                                                       path ) );
        final String xml = GuidedDTGraphXMLPersistence.getInstance().marshal( model );
        assertNotNull( xml );
        assertEqualsIgnoreWhitespace( "<graph>\n" +
                                              "  <entries>\n" +
                                              "    <entry>\n" +
                                              "      <pathHead class=\"org.uberfire.backend.vfs.PathFactory$PathImpl\">\n" +
                                              "        <uri>default://path</uri>\n" +
                                              "        <fileName>path</fileName>\n" +
                                              "        <attributes/>\n" +
                                              "        <hasVersionSupport>false</hasVersionSupport>\n" +
                                              "      </pathHead>\n" +
                                              "      <pathVersion class=\"org.uberfire.backend.vfs.PathFactory$PathImpl\" reference=\"../pathHead\"/>\n" +
                                              "    </entry>\n" +
                                              "  </entries>\n" +
                                              "</graph>",
                                      xml );
    }

    private void assertEmptyModel( final GuidedDecisionTableEditorGraphModel model ) {
        assertNotNull( model );
        assertNotNull( model.getEntries() );
        assertEquals( 0,
                      model.getEntries().size() );
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

}
