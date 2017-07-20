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

package org.kie.workbench.common.services.backend.compiler.external339;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.bridge.MavenRepositorySystem;
import org.apache.maven.building.Source;
import org.apache.maven.cli.configuration.ConfigurationProcessor;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

/**
 * Used to open the API of the maven embedder
 * Original version: https://maven.apache.org/ref/3.3.9/maven-embedder/xref/org/apache/maven/cli/configuration/ConfigurationProcessor.html
 * IMPORTANT: Preserve the structure for an easy update when the maven version will be updated
 */
@Component(role = ConfigurationProcessor.class, hint = AFConfigurationProcessor.HINT)
public class AFConfigurationProcessor {

    public static final String HINT = "settings";

    public final Path USER_HOME;
    public final Path MAVEN_HOME;
    public final Path USER_MAVEN_CONFIGURATION_HOME;
    public final Path DEFAULT_USER_SETTINGS_FILE;
    public final Path DEFAULT_GLOBAL_SETTINGS_FILE;

    @Requirement
    private Logger logger;

    @Requirement
    private SettingsBuilder settingsBuilder;

    @Requirement
    private SettingsDecrypter settingsDecrypter;

    public AFConfigurationProcessor(Path userHome,
                                    Path mavenHome) {
        USER_HOME = userHome;
        MAVEN_HOME = mavenHome;
        USER_MAVEN_CONFIGURATION_HOME = Paths.get(USER_HOME.toString(),
                                                  ".m2");
        DEFAULT_USER_SETTINGS_FILE = Paths.get(USER_MAVEN_CONFIGURATION_HOME.toString(),
                                               "settings.xml");
        DEFAULT_GLOBAL_SETTINGS_FILE = Paths.get(System.getProperty("maven.home",
                                                                    System.getProperty("user.dir",
                                                                                       "")),
                                                 "conf/settings.xml");
    }

    static Path resolvePath(Path file,
                            String workingDirectory) {
        return file == null ? null : (file.isAbsolute() ? file : (file.getFileName().startsWith(File.separator) ? file.toAbsolutePath() : (Paths.get(workingDirectory,
                                                                                                                                                     file.getFileName().toString()))));
    }

    public Path getUSER_HOME() {
        return USER_HOME;
    }

    public Path getMAVEN_HOME() {
        return MAVEN_HOME;
    }

    public Path getUSER_MAVEN_CONFIGURATION_HOME() {
        return USER_MAVEN_CONFIGURATION_HOME;
    }

    public Path getDEFAULT_USER_SETTINGS_FILE() {
        return DEFAULT_USER_SETTINGS_FILE;
    }

    public Path getDEFAULT_GLOBAL_SETTINGS_FILE() {
        return DEFAULT_GLOBAL_SETTINGS_FILE;
    }

    public void process(AFCliRequest cliRequest) throws Exception {
        CommandLine commandLine = cliRequest.getCommandLine();
        String workingDirectory = cliRequest.getWorkingDirectory();
        MavenExecutionRequest request = cliRequest.getRequest();
        Path userSettingsFile;
        if (commandLine.hasOption('s')) {
            userSettingsFile = Paths.get(commandLine.getOptionValue('s'));
            userSettingsFile = resolvePath(userSettingsFile,
                                           workingDirectory);
            if (!Files.isRegularFile(userSettingsFile)) {
                throw new FileNotFoundException("The specified user settings file does not exist: " + userSettingsFile);
            }
        } else {
            userSettingsFile = DEFAULT_USER_SETTINGS_FILE;
        }

        Path globalSettingsFile;
        if (commandLine.hasOption("gs")) {
            globalSettingsFile = Paths.get(commandLine.getOptionValue("gs"));
            globalSettingsFile = resolvePath(globalSettingsFile,
                                             workingDirectory);
            if (!Files.isRegularFile(globalSettingsFile)) {
                throw new FileNotFoundException("The specified global settings file does not exist: " + globalSettingsFile);
            }
        } else {
            globalSettingsFile = DEFAULT_GLOBAL_SETTINGS_FILE;
        }

        request.setGlobalSettingsFile(globalSettingsFile.toFile());
        request.setUserSettingsFile(userSettingsFile.toFile());
        SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
        settingsRequest.setGlobalSettingsFile(globalSettingsFile.toFile());
        settingsRequest.setUserSettingsFile(userSettingsFile.toFile());
        settingsRequest.setSystemProperties(cliRequest.getSystemProperties());
        settingsRequest.setUserProperties(cliRequest.getUserProperties());
        if (request.getEventSpyDispatcher() != null) {
            request.getEventSpyDispatcher().onEvent(settingsRequest);
        }

        this.logger.debug("Reading global settings from " + this.getLocation(settingsRequest.getGlobalSettingsSource(),
                                                                             settingsRequest.getGlobalSettingsFile()));
        this.logger.debug("Reading user settings from " + this.getLocation(settingsRequest.getUserSettingsSource(),
                                                                           settingsRequest.getUserSettingsFile()));
        SettingsBuildingResult settingsResult = this.settingsBuilder.build(settingsRequest);
        if (request.getEventSpyDispatcher() != null) {
            request.getEventSpyDispatcher().onEvent(settingsResult);
        }

        this.populateFromSettings(request,
                                  settingsResult.getEffectiveSettings());
        if (!settingsResult.getProblems().isEmpty() && this.logger.isWarnEnabled()) {
            this.logger.warn("");
            this.logger.warn("Some problems were encountered while building the effective settings");
            Iterator i$ = settingsResult.getProblems().iterator();

            while (i$.hasNext()) {
                SettingsProblem problem = (SettingsProblem) i$.next();
                this.logger.warn(problem.getMessage() + " @ " + problem.getLocation());
            }

            this.logger.warn("");
        }
    }

    private MavenExecutionRequest populateFromSettings(MavenExecutionRequest request,
                                                       Settings settings) throws MavenExecutionRequestPopulationException {
        if (settings == null) {
            return request;
        } else {
            request.setOffline(settings.isOffline());
            request.setInteractiveMode(settings.isInteractiveMode());
            request.setPluginGroups(settings.getPluginGroups());
            request.setLocalRepositoryPath(settings.getLocalRepository());
            Iterator i$ = settings.getServers().iterator();

            while (i$.hasNext()) {
                Server server = (Server) i$.next();
                server = server.clone();
                request.addServer(server);
            }

            i$ = settings.getProxies().iterator();

            while (i$.hasNext()) {
                Proxy proxy = (Proxy) i$.next();
                if (proxy.isActive()) {
                    proxy = proxy.clone();
                    request.addProxy(proxy);
                }
            }

            i$ = settings.getMirrors().iterator();

            while (i$.hasNext()) {
                Mirror mirror = (Mirror) i$.next();
                mirror = mirror.clone();
                request.addMirror(mirror);
            }

            request.setActiveProfiles(settings.getActiveProfiles());
            i$ = settings.getProfiles().iterator();

            while (true) {
                Profile rawProfile;
                do {
                    if (!i$.hasNext()) {
                        return request;
                    }

                    rawProfile = (Profile) i$.next();
                    request.addProfile(SettingsUtils.convertFromSettingsProfile(rawProfile));
                } while (!settings.getActiveProfiles().contains(rawProfile.getId()));

                List<Repository> remoteRepositories = rawProfile.getRepositories();
                Iterator i$2 = remoteRepositories.iterator();

                while (i$2.hasNext()) {
                    Repository remoteRepository = (Repository) i$.next();

                    try {
                        request.addRemoteRepository(MavenRepositorySystem.buildArtifactRepository(remoteRepository));
                    } catch (InvalidRepositoryException var10) {
                        logger.error(var10.getMessage());
                    }
                }

                List<Repository> pluginRepositories = rawProfile.getPluginRepositories();
                Iterator i$3 = pluginRepositories.iterator();

                while (i$3.hasNext()) {
                    Repository pluginRepository = (Repository) i$.next();

                    try {
                        request.addPluginArtifactRepository(MavenRepositorySystem.buildArtifactRepository(pluginRepository));
                    } catch (InvalidRepositoryException var11) {
                        logger.error(var11.getMessage());
                    }
                }
            }
        }
    }

    private Object getLocation(Source source,
                               File defaultLocation) {
        return source != null ? source.getLocation() : defaultLocation;
    }
}
