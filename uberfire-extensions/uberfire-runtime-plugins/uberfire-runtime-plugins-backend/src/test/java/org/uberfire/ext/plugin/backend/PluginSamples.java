/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.Language;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;

public class PluginSamples {

    private static final String ANGULAR_MAIN = "angular_main.txt";
    private static final String ANGULAR_TEMPLATE = "angular_template.txt";
    private static final String ANGULAR_CSS = "angular_css.txt";
    public static final String ANGULAR_TODO_REGISTRY = "angular_todo_registry.txt";
    public static final String EMPTY_SCREEN_REGISTRY = "empty_screen_registry.txt";
    public static final String SCREEN_WITH_TITLE_REGISTRY = "screen_with_title_registry.txt";
    public static final String SCREEN_WITH_TITLE_AND_NAME_REGISTRY = "screen_with_title_and_name_registry.txt";
    private static final String SPLASH_TEMPLATE = "splash_template.txt";
    private static final String SPLASH_MAIN = "splash_main.txt";
    public static final String SPLASH_SCREEN_REGISTRY = "splash_screen_registry.txt";

    public static PluginSimpleContent getTodoAngularPluginSimpleContent() {
        Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
        codeMap.put( CodeType.MAIN, loadSample( ANGULAR_MAIN ) );
        Path path = null;
        Set<Framework> frameworks = new HashSet<Framework>();
        frameworks.add( Framework.ANGULAR );
        PluginSimpleContent plugin = new PluginSimpleContent( "Yo", PluginType.SCREEN, path, loadSample( ANGULAR_TEMPLATE ), loadSample( ANGULAR_CSS ), codeMap, frameworks, Language.JAVASCRIPT );
        return plugin;
    }

    public static PluginSimpleContent getSplashScreen() {
        Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
        codeMap.put( CodeType.MAIN, loadSample( SPLASH_MAIN ) );
        codeMap.put( CodeType.ON_CLOSE, loadSample( SPLASH_MAIN ) );
        codeMap.put( CodeType.ON_CONCURRENT_COPY, loadSample( SPLASH_MAIN ) );
        codeMap.put( CodeType.TITLE, "Title" );
        Path path = null;
        Set<Framework> frameworks = new HashSet<Framework>();
        PluginSimpleContent plugin = new PluginSimpleContent( "Splash", PluginType.SPLASH, path, loadSample( SPLASH_TEMPLATE ), "", codeMap, frameworks, Language.JAVASCRIPT );
        return plugin;
    }

    public static PluginSimpleContent getEmptyScreen() {
        Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
        codeMap.put( CodeType.MAIN, "" );
        Path path = null;
        Set<Framework> frameworks = new HashSet<Framework>();
        PluginSimpleContent plugin = new PluginSimpleContent( "Yo", PluginType.SCREEN, path, "", "", codeMap, frameworks, Language.JAVASCRIPT );
        return plugin;
    }

    public static PluginSimpleContent getScreenWithTitle() {
        Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
        codeMap.put( CodeType.MAIN, "" );
        codeMap.put( CodeType.TITLE, "My Title" );
        Path path = null;
        Set<Framework> frameworks = new HashSet<Framework>();
        PluginSimpleContent plugin = new PluginSimpleContent( "ScreenWithTitle", PluginType.SCREEN, path, "", "", codeMap, frameworks, Language.JAVASCRIPT );
        return plugin;
    }

    public static PluginSimpleContent getScreenWithMainAndTitle() {
        Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
        codeMap.put( CodeType.MAIN, "alert('main');" );
        codeMap.put( CodeType.TITLE, "My Title" );
        Path path = null;
        Set<Framework> frameworks = new HashSet<Framework>();
        PluginSimpleContent plugin = new PluginSimpleContent( "ScreenWithTitle", PluginType.SCREEN, path, "", "", codeMap, frameworks, Language.JAVASCRIPT );
        return plugin;
    }

    public static String loadSample( String file ) {
        try {
            return IOUtils.toString( new PluginSamples().getClass().getResourceAsStream( file ),
                                     "UTF-8" );
        } catch ( IOException e ) {
            return "";
        }
    }

}
