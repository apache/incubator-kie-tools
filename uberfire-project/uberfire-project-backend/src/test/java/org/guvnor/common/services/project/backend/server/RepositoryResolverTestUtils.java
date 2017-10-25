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

package org.guvnor.common.services.project.backend.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.appformer.maven.integration.Aether;
import org.appformer.maven.integration.MavenRepository;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.util.artifact.SubArtifact;

import static org.appformer.maven.integration.MavenRepository.toFileName;

public class RepositoryResolverTestUtils {

    private static final String REPO_1 = "<repository>\n" +
            "<id>jboss-origin-repository-group</id>\n" +
            "<name>JBoss.org Public Repository Group</name>\n" +
            "<url>https://origin-repository.jboss.org/nexus/content/groups/ea/</url>\n" +
            "</repository>\n";

    private static final String REPO_2 = "<repository>\n" +
            "<id>jboss-developer-repository-group</id>\n" +
            "<name>JBoss.org Developer Repository Group</name>\n" +
            "<url>https://repository.jboss.org/nexus/content/groups/developer/</url>\n" +
            "</repository>\n";

    private static final String REPO_3 = "<repository>\n" +
            "<id>jboss-public-repository-group</id>\n" +
            "<name>JBoss Public Repository Group</name>\n" +
            "<url>http://repository.jboss.org/nexus/content/groups/public/</url>\n" +
            "</repository>\n";

    //This intentionally has the same "id" as REPO_3
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1319046
    private static final String PLUGIN_REPO_1 = "<pluginRepository>\n" +
            "<id>jboss-public-repository-group</id>\n" +
            "<name>JBoss Public Repository Group</name>\n" +
            "<url>https://repository.jboss.org/nexus/content/repositories/snapshots/</url>\n" +
            "</pluginRepository>\n";

    /**
     * Install a Maven Project to the local Maven Repository
     * @param mavenProject
     * @param pomXml
     */
    public static void installArtifact(final MavenProject mavenProject,
                                       final String pomXml) {
        final AFReleaseId releaseId = new AFReleaseIdImpl(mavenProject.getGroupId(),
                                                          mavenProject.getArtifactId(),
                                                          mavenProject.getVersion());

        final Aether aether = new Aether(mavenProject);
        final MavenRepository mavenRepository = new MavenRepository(aether) {
            //Nothing to override, just a sub-class to expose Constructor
        };

        mavenRepository.installArtifact(releaseId,
                                        "content".getBytes(),
                                        pomXml.getBytes());
    }

    /**
     * Deploy a Maven Project to the 'Remote' Maven Repository defined in the Project's {code}<distribtionManagement>{code} section.
     * @param mavenProject
     * @param pomXml
     */
    public static void deployArtifact(final MavenProject mavenProject,
                                      final String pomXml) {
        final AFReleaseId releaseId = new AFReleaseIdImpl(mavenProject.getGroupId(),
                                                          mavenProject.getArtifactId(),
                                                          mavenProject.getVersion());

        //Create temporary files for the JAR and POM
        final Aether aether = new Aether(mavenProject);
        final File jarFile = new File(System.getProperty("java.io.tmpdir"),
                                      toFileName(releaseId,
                                                 null) + ".jar");
        try {
            FileOutputStream fos = new FileOutputStream(jarFile);
            fos.write("content".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final File pomFile = new File(System.getProperty("java.io.tmpdir"),
                                      toFileName(releaseId,
                                                 null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomXml.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Artifact representing the JAR
        Artifact jarArtifact = new DefaultArtifact(releaseId.getGroupId(),
                                                   releaseId.getArtifactId(),
                                                   "jar",
                                                   releaseId.getVersion());
        jarArtifact = jarArtifact.setFile(jarFile);

        //Artifact representing the POM
        Artifact pomArtifact = new SubArtifact(jarArtifact,
                                               "",
                                               "pom");
        pomArtifact = pomArtifact.setFile(pomFile);

        //Read <distributionManagement> section
        final DistributionManagement distributionManagement = mavenProject.getDistributionManagement();
        if (distributionManagement != null) {
            final DeployRequest deployRequest = new DeployRequest();
            deployRequest
                    .addArtifact(jarArtifact)
                    .addArtifact(pomArtifact)
                    .setRepository(getRemoteRepoFromDeployment(distributionManagement.getRepository(),
                                                               aether.getSession()));

            try {
                aether.getSystem().deploy(aether.getSession(),
                                          deployRequest);
            } catch (DeploymentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Convert a DeploymentRepository to a RemoteRepository
    private static RemoteRepository getRemoteRepoFromDeployment(final DeploymentRepository deploymentRepository,
                                                                final RepositorySystemSession mavenSession) {
        final RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder(deploymentRepository.getId(),
                                                                                        deploymentRepository.getLayout(),
                                                                                        deploymentRepository.getUrl())
                .setSnapshotPolicy(new RepositoryPolicy(true,
                                                        RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                .setReleasePolicy(new RepositoryPolicy(true,
                                                       RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                       RepositoryPolicy.CHECKSUM_POLICY_WARN));

        final Settings settings = MavenSettings.getSettings();
        final Server server = settings.getServer(deploymentRepository.getId());

        if (server != null) {
            final Authentication authentication = mavenSession
                    .getAuthenticationSelector()
                    .getAuthentication(remoteRepoBuilder.build());
            remoteRepoBuilder.setAuthentication(authentication);
        }

        return remoteRepoBuilder.build();
    }

    /**
     * Generate a temporary settings.xml file.
     * @param m2Folder
     * @return
     * @throws IOException
     */
    public static java.nio.file.Path generateSettingsXml(final java.nio.file.Path m2Folder) throws IOException {
        final java.nio.file.Path settingsXmlPath = Files.createTempFile(m2Folder,
                                                                        "settings",
                                                                        ".xml");

        final List<String> settingsXmlLines = new ArrayList<String>();
        final List<String> additionalRepositories = new ArrayList<String>() {{
            add(REPO_1);
            add(REPO_2);
            add(REPO_3);
        }};
        settingsXmlLines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        settingsXmlLines.add("<settings>\n");
        settingsXmlLines.add("  <localRepository>" + m2Folder.toString() + "</localRepository>\n");
        if (additionalRepositories.size() > 0) {
            settingsXmlLines.add("  <profiles>\n");
            settingsXmlLines.add("    <profile>\n");
            settingsXmlLines.add("      <id>standard-extra-repos</id>\n");
            settingsXmlLines.add("      <activation>\n");
            settingsXmlLines.add("        <activeByDefault>true</activeByDefault>\n");
            settingsXmlLines.add("      </activation>\n");
            settingsXmlLines.add("      <repositories>\n");
            settingsXmlLines.addAll(additionalRepositories);
            settingsXmlLines.add("      </repositories>\n");
            settingsXmlLines.add("      <pluginRepositories>\n");
            settingsXmlLines.add(PLUGIN_REPO_1);
            settingsXmlLines.add("        </pluginRepositories>\n");
            settingsXmlLines.add("    </profile>\n");
            settingsXmlLines.add("  </profiles>\n");
        }
        settingsXmlLines.add("</settings>");

        Files.write(settingsXmlPath,
                    settingsXmlLines,
                    StandardCharsets.UTF_8);

        return settingsXmlPath;
    }
}
