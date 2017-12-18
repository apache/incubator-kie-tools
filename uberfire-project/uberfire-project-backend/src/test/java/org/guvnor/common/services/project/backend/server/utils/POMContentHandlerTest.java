/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server.utils;

import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Test;

import static org.junit.Assert.*;

public class POMContentHandlerTest {

    private static final String GAV_GROUP_ID_XML = "<groupId>org.guvnor</groupId>";
    private static final String GAV_ARTIFACT_ID_XML = "<artifactId>test</artifactId>";
    private static final String GAV_VERSION_XML = "<version>0.0.1</version>";
    private static final String URL_XML = "<url>url</url>";
    private static final String EXISTING_PLUGIN_XML = "<plugin>"
            + "<groupId>org.kie</groupId>"
            + "<artifactId>kie-maven-plugin</artifactId>"
            + "<version>another-version</version>"
            + "<extensions>true</extensions>"
            + "</plugin>";

    @Test
    public void testPOMContentHandlerNewProject() throws IOException {
        final POMContentHandler handler = new POMContentHandler();
        final GAV gav = new GAV();
        gav.setGroupId("org.guvnor");
        gav.setArtifactId("test");
        gav.setVersion("0.0.1");
        final POM pom = new POM("name",
                                "description",
                                "url",
                                gav);
        final String xml = handler.toString(pom);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML,
                                       xml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML,
                                       xml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML,
                                       xml);
        assertContainsIgnoreWhitespace(URL_XML,
                                       xml);
    }

    @Test
    public void testPOMContentHandlerExistingProject() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler();
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<modelVersion>4.0.0</modelVersion>"
                + "<groupId>org.guvnor</groupId>"
                + "<artifactId>test</artifactId>"
                + "<version>0.0.1</version>"
                + "<name>name</name>"
                + "<url>url</url>"
                + "<description>description</description>"
                + "</project>";

        final POM pom = handler.toModel(xml);
        assertEquals("org.guvnor",
                     pom.getGav().getGroupId());
        assertEquals("test",
                     pom.getGav().getArtifactId());
        assertEquals("0.0.1",
                     pom.getGav().getVersion());
        assertEquals("name",
                     pom.getName());
        assertEquals("description",
                     pom.getDescription());
        assertEquals("url",
                     pom.getUrl());

        final String enrichedXml = handler.toString(pom,
                                                    xml);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(URL_XML,
                                       enrichedXml);
    }

    @Test
    public void testPOMContentHandlerExistingJarProject() throws IOException, XmlPullParserException {
        /*
           Keep the original type
         */

        final POMContentHandler handler = new POMContentHandler();
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<modelVersion>4.0.0</modelVersion>"
                + "<groupId>org.guvnor</groupId>"
                + "<artifactId>test</artifactId>"
                + "<version>0.0.1</version>"
                + "<packaging>something</packaging>"
                + "<name>name</name>"
                + "<url>url</url>"
                + "<description>description</description>"
                + "</project>";

        final String enrichedXml = handler.toString(handler.toModel(xml),
                                                    xml);

        assertContainsIgnoreWhitespace("<packaging>something</packaging>",
                                       enrichedXml);
    }

    @Test
    public void testPOMContentHandlerExistingKieProject() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler();
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<modelVersion>4.0.0</modelVersion>"
                + "<groupId>org.guvnor</groupId>"
                + "<artifactId>test</artifactId>"
                + "<version>0.0.1</version>"
                + "<name>name</name>"
                + "<description>description</description>"
                + "<url>url</url>"
                + "<build>"
                + "<plugins>"
                + "<plugin>"
                + "<groupId>org.kie</groupId>"
                + "<artifactId>kie-maven-plugin</artifactId>"
                + "<version>another-version</version>"
                + "<extensions>true</extensions>"
                + "</plugin>"
                + "</plugins>"
                + "</build>"
                + "</project>";

        final POM pom = handler.toModel(xml);
        assertEquals("org.guvnor",
                     pom.getGav().getGroupId());
        assertEquals("test",
                     pom.getGav().getArtifactId());
        assertEquals("0.0.1",
                     pom.getGav().getVersion());
        assertEquals("name",
                     pom.getName());
        assertEquals("description",
                     pom.getDescription());
        assertEquals("url",
                     pom.getUrl());

        final String enrichedXml = handler.toString(pom,
                                                    xml);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(EXISTING_PLUGIN_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(URL_XML,
                                       enrichedXml);
    }

    @Test
    public void testParent() throws Exception {
        final POMContentHandler handler = new POMContentHandler();
        final String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                        "  <modelVersion>4.0.0</modelVersion>" +
                        "  <parent>" +
                        "    <groupId>org.tadaa</groupId>" +
                        "    <artifactId>tadaa</artifactId>" +
                        "    <version>1.2.3</version>" +
                        "  </parent>" +
                        "  <artifactId>myproject</artifactId>" +
                        "  <packaging>kjar</packaging>" +
                        "  <name>myproject</name>" +
                        "  <build>" +
                        "    <plugins>" +
                        "      <plugin>" +
                        "        <groupId>org.kie</groupId>" +
                        "        <artifactId>kie-maven-plugin</artifactId>" +
                        "        <version>another-version</version>" +
                        "        <extensions>true</extensions>" +
                        "      </plugin>" +
                        "    </plugins>" +
                        "  </build>" +
                        "</project>";

        final POM pom = handler.toModel(xml);

        assertNotNull(pom.getParent());
        assertEquals("org.tadaa",
                     pom.getParent().getGroupId());
        assertEquals("tadaa",
                     pom.getParent().getArtifactId());
        assertEquals("1.2.3",
                     pom.getParent().getVersion());
    }

    private void assertContainsIgnoreWhitespace(final String expected,
                                                final String xml) {
        final String cleanExpected = expected.replaceAll("\\s+",
                                                         "");
        final String cleanActual = xml.replaceAll("\\s+",
                                                  "");

        assertTrue(cleanActual.contains(cleanExpected));
    }
}
