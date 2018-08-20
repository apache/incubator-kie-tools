/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;

public class MavenAPIUtil {

    public final static String TRUE = "true";

    public static Plugin getPlugin(String groupdID, String artifactID, String version, Boolean extensions) {
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupdID);
        plugin.setArtifactId(artifactID);
        plugin.setVersion(version);
        plugin.setExtensions(extensions);
        return plugin;
    }

    public static Plugin getPlugin(String groupdID, String artifactID, String version) {
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupdID);
        plugin.setArtifactId(artifactID);
        plugin.setVersion(version);
        return plugin;
    }

    public static Plugin getNewCompilerPlugin(Map<ConfigurationKey, String> conf) {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP));
        newCompilerPlugin.setArtifactId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT));
        newCompilerPlugin.setVersion(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION));

        Xpp3Dom compilerId = new Xpp3Dom(MavenConfig.MAVEN_COMPILER_ID);
        compilerId.setValue(conf.get(ConfigurationKey.COMPILER));
        Xpp3Dom sourceVersion = new Xpp3Dom(MavenConfig.MAVEN_SOURCE);
        sourceVersion.setValue(conf.get(ConfigurationKey.SOURCE_VERSION));
        Xpp3Dom targetVersion = new Xpp3Dom(MavenConfig.MAVEN_TARGET);
        targetVersion.setValue(conf.get(ConfigurationKey.TARGET_VERSION));

        Xpp3Dom failOnError = new Xpp3Dom(MavenConfig.FAIL_ON_ERROR);
        failOnError.setValue(conf.get(ConfigurationKey.FAIL_ON_ERROR));

        Xpp3Dom configuration = new Xpp3Dom(MavenConfig.MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(compilerId);
        configuration.addChild(sourceVersion);
        configuration.addChild(targetVersion);
        configuration.addChild(failOnError);
        newCompilerPlugin.setConfiguration(configuration);

        PluginExecution execution = new PluginExecution();
        execution.setId(MavenCLIArgs.DEFAULT_COMPILE);
        execution.setGoals(Arrays.asList(MavenCLIArgs.COMPILE));
        execution.setPhase(MavenCLIArgs.COMPILE);

        newCompilerPlugin.setExecutions(Arrays.asList(execution));

        return newCompilerPlugin;
    }

    public static void disableMavenCompilerAlreadyPresent(Plugin plugin) {
        Xpp3Dom skipMain = new Xpp3Dom(MavenConfig.MAVEN_SKIP_MAIN);
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(MavenConfig.MAVEN_SKIP);
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(MavenConfig.MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        plugin.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(MavenConfig.MAVEN_DEFAULT_COMPILE);
        exec.setPhase(MavenConfig.MAVEN_PHASE_NONE);
        List<PluginExecution> executions = new ArrayList<>();
        executions.add(exec);
        plugin.setExecutions(executions);
    }
}
