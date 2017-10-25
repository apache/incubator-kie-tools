/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.executor;

import static org.appformer.maven.integration.embedder.MavenSettings.CUSTOM_SETTINGS_PROPERTY;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.appformer.maven.integration.embedder.MavenSettings;
import org.guvnor.ala.build.Binary;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.util.MavenBuildExecutor;
import org.guvnor.ala.openshift.dns.OpenShiftNameService;
import org.guvnor.ala.registry.BuildRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test helper class to deploy into a nexus maven repo.
 */
public class OpenShiftMavenDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftMavenDeployer.class);

    private final File workDir;
    private final String nexusHostPrefix;

    public OpenShiftMavenDeployer(File workDir, String appName, String prjName) throws Exception {
        this.workDir = workDir;
        this.nexusHostPrefix = appName + "-nexus-" + prjName;
    }

    public boolean deploy(BuildRegistry buildRegistry) throws Exception {
        return deploy(buildRegistry.getAllBinaries());
    }

    public boolean deploy(List<Binary> binaries) throws Exception {
        URL nexusContentURL = getNexusContentURL(true);
        if (nexusContentURL == null) {
            return false;
        }
        String origCustomSettingsProp = System.getProperty(CUSTOM_SETTINGS_PROPERTY);
        try {
            System.setProperty(CUSTOM_SETTINGS_PROPERTY, generateSettingsXml());
            MavenSettings.reinitSettings();
            for (Binary binary : binaries) {
                MavenBinary mavenBinary = (MavenBinary)binary;
                File pom = new File(mavenBinary.getProject().getTempDir(),  "pom.xml");
                Properties props = new Properties();
                boolean isSnapshot = mavenBinary.getVersion().endsWith("SNAPSHOT");
                String nexusRepoUrl = nexusContentURL + "repositories/" + (isSnapshot ? "snapshots/" : "releases/");
                props.setProperty("altDeploymentRepository", "nexus::default::" + nexusRepoUrl);
                MavenBuildExecutor.executeMaven(pom, props, new String[]{"deploy"});
            }
        } finally {
            if (origCustomSettingsProp != null) {
                System.setProperty(CUSTOM_SETTINGS_PROPERTY, origCustomSettingsProp);
            } else {
                System.clearProperty(CUSTOM_SETTINGS_PROPERTY);
            }
            MavenSettings.reinitSettings();
        }
        return true;
    }

    private URL getNexusContentURL(boolean checkConnection) throws Exception {
        URL nexusContentURL = null;
        for (String hostName : OpenShiftNameService.getHosts()) {
            if (hostName.startsWith(nexusHostPrefix)) {
                nexusContentURL = new URL("http://" + hostName + "/nexus/content/");
                break;
            }
        }
        if (nexusContentURL == null) {
            LOG.warn(String.format("Unknown Nexus host with prefix: %s", nexusHostPrefix));
        } else if (checkConnection) {
            if (!OpenShiftExecutorTest.checkConnection(nexusContentURL, 200, 60, 1000)) {
                LOG.warn(String.format("%s is not reachable.", nexusContentURL));
                nexusContentURL = null;
            }
        }
        return nexusContentURL;
    }

    private String generateSettingsXml() throws IOException {
        String settingsXml =
                "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
                        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\n" +
                        "                       http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
                        "  <servers>\n" +
                        "      <server>\n" +
                        "          <id>nexus</id>\n" +
                        "          <username>admin</username>\n" +
                        "          <password>admin123</password>\n" +
                        "      </server>\n" +
                        "  </servers>\n" +
                        "</settings>\n";
        Path settingsXmlPath = Files.createTempFile(workDir.toPath(), "settings-", ".xml");
        Files.write(settingsXmlPath, settingsXml.getBytes());
        return settingsXmlPath.toAbsolutePath().toString();
    }

}
