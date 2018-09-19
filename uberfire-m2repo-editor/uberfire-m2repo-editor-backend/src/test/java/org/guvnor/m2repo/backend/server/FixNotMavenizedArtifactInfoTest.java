package org.guvnor.m2repo.backend.server;

import java.util.Properties;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.ARTIFACT_ID;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.GROUP_ID;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.VERSION;

public class FixNotMavenizedArtifactInfoTest {

    @Test
    public void testProperties() {
        final FixNotMavenizedArtifactInfo fixNotMavenizedArtifactInfo = new FixNotMavenizedArtifactInfo();
        {
            final Properties result = fixNotMavenizedArtifactInfo.getProperties("/opt/wildfly/standalone/tmp/vfs/temp/temp9c869d5d938ad120/content-b548cc99838ba656/WEB-INF/lib/junit-4.12.jar");

            assertFalse(result.isEmpty());
            assertThat(result.getProperty(GROUP_ID)).isEqualTo("junit");
            assertThat(result.getProperty(ARTIFACT_ID)).isEqualTo("junit");
            assertThat(result.getProperty(VERSION)).isEqualTo("4.12");
        }

        {
            final Properties result = fixNotMavenizedArtifactInfo.getProperties("/opt/wildfly/standalone/tmp/vfs/temp/temp9c869d5d938ad120/content-b548cc99838ba656/WEB-INF/lib/ant-launcher-X.12-SNAPSHOT.jar");

            assertFalse(result.isEmpty());
            assertThat(result.getProperty(GROUP_ID)).isEqualTo("org.apache.ant");
            assertThat(result.getProperty(ARTIFACT_ID)).isEqualTo("ant-launcher");
            assertThat(result.getProperty(VERSION)).isEqualTo("X.12-SNAPSHOT");
        }

        {
            final Properties result = fixNotMavenizedArtifactInfo.getProperties("/opt/wildfly/standalone/tmp/vfs/temp/temp9c869d5d938ad120/content-b548cc99838ba656/WEB-INF/lib/ant-X.12-SNAPSHOT.jar");

            assertFalse(result.isEmpty());
            assertThat(result.getProperty(GROUP_ID)).isEqualTo("org.apache.ant");
            assertThat(result.getProperty(ARTIFACT_ID)).isEqualTo("ant");
            assertThat(result.getProperty(VERSION)).isEqualTo("X.12-SNAPSHOT");
        }
    }

    @Test
    public void testEmptyProperties() {
        final FixNotMavenizedArtifactInfo fixNotMavenizedArtifactInfo = new FixNotMavenizedArtifactInfo();
        final Properties result = fixNotMavenizedArtifactInfo.getProperties("/opt/wildfly/standalone/tmp/vfs/temp/temp9c869d5d938ad120/content-b548cc99838ba656/WEB-INF/lib/xxjunit-4.12.jar");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testBuildPOM() {
        final FixNotMavenizedArtifactInfo fixNotMavenizedArtifactInfo = new FixNotMavenizedArtifactInfo();

        final GAV gav = new GAV("junit", "junit", "4.12");

        final String pomResult = fixNotMavenizedArtifactInfo.buildPom(gav);
        assertThat(pomResult).isEqualTo("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                                                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                                                "\n" +
                                                "  <modelVersion>4.0.0</modelVersion>\n" +
                                                "  <groupId>junit</groupId>\n" +
                                                "  <artifactId>junit</artifactId>\n" +
                                                "  <version>4.12</version>\n" +
                                                "  <packaging>jar</packaging>\n" +
                                                "</project>");
    }
}
