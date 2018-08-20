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
package org.kie.workbench.common.services.backend.compiler.impl.external339;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.cli.CommandLine;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.bridge.MavenRepositorySystem;
import org.apache.maven.building.Source;
import org.apache.maven.cli.CLIManager;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

/**
 * Used to open the API of Maven embedder
 * original version: https://maven.apache.org/ref/3.3.9/maven-embedder/xref/org/apache/maven/cli/configuration/SettingsXmlConfigurationProcessor.html
 * Changed java.io.File to java.impl.file.Path when possible
 * IMPORTANT: Preserve the structure for an easy update when the maven version will be updated
 */
@Named
@Component(role = AFConfigurationProcessor.class, hint = AFSettingsXmlConfigurationProcessor.HINT)
public class AFSettingsXmlConfigurationProcessor implements AFConfigurationProcessor {

    public static final String HINT = "settings";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final Path USER_MAVEN_CONFIGURATION_HOME = Paths.get(USER_HOME,
                                                                       ".m2");

    public static final Path DEFAULT_USER_SETTINGS_FILE = Paths.get(USER_MAVEN_CONFIGURATION_HOME.toString(),
                                                                    "settings.xml");

    public static final Path DEFAULT_GLOBAL_SETTINGS_FILE = Paths.get(System.getProperty("maven.home",
                                                                                         System.getProperty("user.dir",
                                                                                                            "")),
                                                                      "conf/settings.xml");

    @Requirement
    private Logger logger;

    @Requirement
    private SettingsBuilder settingsBuilder;

    @Requirement
    private SettingsDecrypter settingsDecrypter;

    static Path resolvePath(Path file, String workingDirectory) {
        if (file == null) {
            return null;
        } else if (file.isAbsolute()) {
            return file;
        } else if (file.getFileName().startsWith(File.separator)) {
            return file.toAbsolutePath();
        } else {
            return Paths.get(workingDirectory, file.getFileName().toString());
        }
    }

    @Override
    public void process(AFCliRequest cliRequest)
            throws Exception {
        CommandLine commandLine = cliRequest.getCommandLine();
        String workingDirectory = cliRequest.getWorkingDirectory();
        MavenExecutionRequest request = cliRequest.getRequest();

        Path userSettingsFile;

        if (commandLine.hasOption(CLIManager.ALTERNATE_USER_SETTINGS)) {
            String settingsFromCLi = commandLine.getOptionValue(CLIManager.ALTERNATE_USER_SETTINGS);
            logger.info("userSettings:" + settingsFromCLi);
            if (settingsFromCLi != null) {
                userSettingsFile = Paths.get(settingsFromCLi.trim());
                /*userSettingsFile = resolvePath(userSettingsFile,
                                           workingDirectory); why this override of the value ? */

                if (!Files.isRegularFile(userSettingsFile)) {
                    throw new FileNotFoundException("The specified user settings file does not exist: "
                                                            + userSettingsFile);
                }
            } else {
                userSettingsFile = DEFAULT_USER_SETTINGS_FILE;
                logger.info("Using default userSettings:" + userSettingsFile);
            }
        } else {
            userSettingsFile = DEFAULT_USER_SETTINGS_FILE;
        }

        Path globalSettingsFile;

        if (commandLine.hasOption(CLIManager.ALTERNATE_GLOBAL_SETTINGS)) {
            globalSettingsFile = Paths.get(commandLine.getOptionValue(CLIManager.ALTERNATE_GLOBAL_SETTINGS));
            globalSettingsFile = resolvePath(globalSettingsFile,
                                             workingDirectory);

            if (!Files.isRegularFile(globalSettingsFile)) {
                throw new FileNotFoundException("The specified global settings file does not exist: "
                                                        + globalSettingsFile);
            }
        } else {
            globalSettingsFile = DEFAULT_GLOBAL_SETTINGS_FILE;
        }

        request.setGlobalSettingsFile(globalSettingsFile.toFile());
        request.setUserSettingsFile(userSettingsFile.toFile());

        AFSettingsBuildingRequest settingsRequest = new AFSettingsBuildingRequest();
        settingsRequest.setGlobalSettingsFile(globalSettingsFile.toFile());
        settingsRequest.setUserSettingsFile(userSettingsFile.toFile());
        settingsRequest.setSystemProperties(cliRequest.getSystemProperties());
        settingsRequest.setUserProperties(cliRequest.getUserProperties());

        if (request.getEventSpyDispatcher() != null) {
            request.getEventSpyDispatcher().onEvent(settingsRequest);
        }

        logger.debug("Reading global settings from "
                             + getLocation(settingsRequest.getGlobalSettingsSource(),
                                           settingsRequest.getGlobalSettingsPath()));
        logger.debug("Reading user settings from "
                             + getLocation(settingsRequest.getUserSettingsSource(),
                                           settingsRequest.getUserSettingsPath()));

        SettingsBuildingResult settingsResult = settingsBuilder.build(settingsRequest);

        if (request.getEventSpyDispatcher() != null) {
            request.getEventSpyDispatcher().onEvent(settingsResult);
        }

        populateFromSettings(request,
                             settingsResult.getEffectiveSettings());

        if (!settingsResult.getProblems().isEmpty() && logger.isWarnEnabled()) {
            logger.warn("");
            logger.warn("Some problems were encountered while building the effective settings");

            for (SettingsProblem problem : settingsResult.getProblems()) {
                logger.warn(problem.getMessage() + " @ " + problem.getLocation());
            }
            logger.warn("");
        }
    }

    private MavenExecutionRequest populateFromSettings(MavenExecutionRequest request,
                                                       Settings settings)
            throws MavenExecutionRequestPopulationException {
        if (settings == null) {
            return request;
        }

        request.setOffline(settings.isOffline());

        request.setInteractiveMode(settings.isInteractiveMode());

        request.setPluginGroups(settings.getPluginGroups());

        request.setLocalRepositoryPath(settings.getLocalRepository());

        for (Server server : settings.getServers()) {
            server = server.clone();

            request.addServer(server);
        }

        //  <proxies>
        //    <proxy>
        //      <active>true</active>
        //      <protocol>http</protocol>
        //      <host>proxy.somewhere.com</host>
        //      <port>8080</port>
        //      <username>proxyuser</username>
        //      <password>somepassword</password>
        //      <nonProxyHosts>www.google.com|*.somewhere.com</nonProxyHosts>
        //    </proxy>
        //  </proxies>

        for (Proxy proxy : settings.getProxies()) {
            if (!proxy.isActive()) {
                continue;
            }

            proxy = proxy.clone();

            request.addProxy(proxy);
        }

        // <mirrors>
        //   <mirror>
        //     <id>nexus</id>
        //     <mirrorOf>*</mirrorOf>
        //     <url>http://repository.sonatype.org/content/groups/public</url>
        //   </mirror>
        // </mirrors>

        for (Mirror mirror : settings.getMirrors()) {
            mirror = mirror.clone();

            request.addMirror(mirror);
        }

        request.setActiveProfiles(settings.getActiveProfiles());

        for (org.apache.maven.settings.Profile rawProfile : settings.getProfiles()) {
            request.addProfile(SettingsUtils.convertFromSettingsProfile(rawProfile));

            if (settings.getActiveProfiles().contains(rawProfile.getId())) {
                List<Repository> remoteRepositories = rawProfile.getRepositories();
                for (Repository remoteRepository : remoteRepositories) {
                    try {
                        request.addRemoteRepository(
                                MavenRepositorySystem.buildArtifactRepository(remoteRepository));
                    } catch (InvalidRepositoryException e) {
                        // do nothing for now
                    }
                }

                List<Repository> pluginRepositories = rawProfile.getPluginRepositories();
                for (Repository pluginRepository : pluginRepositories) {
                    try {
                        request.addPluginArtifactRepository(
                                MavenRepositorySystem.buildArtifactRepository(pluginRepository));
                    } catch (InvalidRepositoryException e) {
                        // do nothing for now
                    }
                }
            }
        }
        return request;
    }

    private Object getLocation(Source source,
                               Path defaultLocation) {
        if (source != null) {
            return source.getLocation();
        }
        return defaultLocation;
    }
}
