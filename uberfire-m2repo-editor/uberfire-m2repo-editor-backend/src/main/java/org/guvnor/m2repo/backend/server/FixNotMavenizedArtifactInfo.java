package org.guvnor.m2repo.backend.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.guvnor.common.services.project.model.GAV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Comparator.naturalOrder;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.ARTIFACT_ID;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.GROUP_ID;
import static org.guvnor.m2repo.backend.server.M2ServletContextListener.VERSION;

/**
 * This class aim to provide maven meta information for artifacts (.jar) that don't
 * have maven metadata built-in.
 * <p>
 * Maven auto-generate a `pom.properties` to store GAV information, and it also
 * embeds the original pom file into the produced artifacts. However, this meta information
 * is not always available if the artifact was created using a different system like ant.
 * Even if the artifact doesn't have the necessary embedded information, those artifacts can
 * be available in maven repositories, but the metadata are provided differently.
 * <p>
 * To build the GAV information out of a .jar file, this class uses as input a
 * `not-mavenized-artifacts.properties` from classpath that provides the feedback needed
 * to produce the required metadata. In case of an artifact is not listed in the input file,
 * this class won't be able to provide the GAV.
 */
public class FixNotMavenizedArtifactInfo {

    private static final Logger logger = LoggerFactory.getLogger(FixNotMavenizedArtifactInfo.class);

    //sort artifact names from longer to shorter, to prevent false-positive matches with artifactIds which are
    // prefixes of other artifactIds (e.g. ant vs. ant-launcher)
    private static final Comparator<String> LONG_BEFORE_SHORT_COMPARATOR = Comparator.comparing(String::length).reversed().thenComparing(naturalOrder());

    private final TreeMap<String, String[]> notMavenizedArtifacts = new TreeMap<>(LONG_BEFORE_SHORT_COMPARATOR);

    private static final String POM_TEMPLATE = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "\n" +
            "  <modelVersion>4.0.0</modelVersion>\n" +
            "  <groupId>{groupId}</groupId>\n" +
            "  <artifactId>{artifactId}</artifactId>\n" +
            "  <version>{version}</version>\n" +
            "  <packaging>jar</packaging>\n" +
            "</project>";

    public FixNotMavenizedArtifactInfo() {
        final Properties notMavenizedArtifactsProps = new Properties();
        try {
            final InputStream isNotMavenizedArtifacts = M2ServletContextListener.class.getResourceAsStream("/not-mavenized-artifacts.properties");
            if (isNotMavenizedArtifacts != null) {
                notMavenizedArtifactsProps.load(isNotMavenizedArtifacts);
            }
            for (Map.Entry<Object, Object> entry : notMavenizedArtifactsProps.entrySet()) {
                notMavenizedArtifacts.put(entry.getKey().toString() + "-", entry.getValue().toString().split(":"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Properties getProperties(final String filePath) {
        final Properties result = new Properties();
        final String fullFileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        for (Map.Entry<String, String[]> entry : notMavenizedArtifacts.entrySet()) {
            if (fullFileName.startsWith(entry.getKey())) {
                final String fileVersion = fullFileName.substring(entry.getKey().length()).replace(".jar", "");

                result.put(GROUP_ID, entry.getValue()[0]);
                result.put(ARTIFACT_ID, entry.getValue()[1]);
                result.put(VERSION, fileVersion);
                break;
            }
        }
        return result;
    }

    public String buildPom(GAV gav) {
        return POM_TEMPLATE.replace("{groupId}", gav.getGroupId())
                .replace("{artifactId}", gav.getArtifactId())
                .replace("{version}", gav.getVersion());
    }
}
