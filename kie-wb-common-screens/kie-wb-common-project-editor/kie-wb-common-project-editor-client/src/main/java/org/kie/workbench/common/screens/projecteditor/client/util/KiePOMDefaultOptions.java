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

package org.kie.workbench.common.screens.projecteditor.client.util;

import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import org.guvnor.common.services.project.model.Plugin;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

@ApplicationScoped
@Specializes
public class KiePOMDefaultOptions
        extends org.guvnor.common.services.project.client.util.POMDefaultOptions {

    private static final String KIE_MAVEN_PLUGIN_GROUP_ID = "org.kie";
    private static final String KIE_MAVEN_PLUGIN_ARTIFACT_ID = "kie-maven-plugin";

    @Override
    public ArrayList<Plugin> getBuildPlugins() {
        ArrayList<Plugin> plugins = new ArrayList<Plugin>( );
        plugins.add( getKieMavenPlugin( ApplicationPreferences.getCurrentDroolsVersion() ) );
        return plugins;
    }

    @Override
    public String getPackaging() {
        return "kjar";
    }

    private Plugin getKieMavenPlugin( final String kieVersion ) {
        final Plugin plugin = new Plugin();
        plugin.setGroupId( KIE_MAVEN_PLUGIN_GROUP_ID );
        plugin.setArtifactId( KIE_MAVEN_PLUGIN_ARTIFACT_ID );
        plugin.setVersion( kieVersion );
        plugin.setExtensions( true );
        return plugin;
    }
}