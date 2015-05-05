package org.uberfire.ext.plugin.backend;

import org.junit.Test;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.PluginSimpleContent;

import static org.junit.Assert.*;
import static org.uberfire.ext.plugin.backend.PluginSamples.*;

public class JSRegistryTest {

    @Test
    public void createEmptyScreenRegistry() {
        PluginSimpleContent pluginContent = getEmptyScreen();
        assertEquals( loadSample( EMPTY_SCREEN_REGISTRY ), JSRegistry.convertToJSRegistry( pluginContent ) );
    }

    @Test
    public void createScreenWithTitleRegistry() {
        PluginSimpleContent pluginContent = getScreenWithTitle();
        assertEquals( loadSample( SCREEN_WITH_TITLE_REGISTRY ), JSRegistry.convertToJSRegistry( pluginContent ) );
    }

    @Test
    public void createScreenWithTitleAndMainRegistry() {
        PluginSimpleContent pluginContent = getScreenWithMainAndTitle();
        assertEquals( loadSample( SCREEN_WITH_TITLE_AND_NAME_REGISTRY ), JSRegistry.convertToJSRegistry( pluginContent ) );
    }

    @Test
    public void createAngularScreenRegistry() {
        PluginSimpleContent pluginContent = getTodoAngularPluginSimpleContent();
        assertEquals( loadSample( ANGULAR_TODO_REGISTRY ), JSRegistry.convertToJSRegistry( pluginContent ) );
    }

    @Test
    public void createSplashScreenRegistry() {
        PluginSimpleContent pluginContent = getSplashScreen();
        assertEquals( loadSample( SPLASH_SCREEN_REGISTRY ), JSRegistry.convertToJSRegistry( pluginContent ) );
    }

    @Test
    public void prepareTextualEntry() {
        assertEquals( "function (){return \"\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, null ) );
        assertEquals( "function (){return \"\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, "" ) );
        assertEquals( "function (){return \" \";}", JSRegistry.prepareEntryValue( CodeType.TITLE, " " ) );
        assertEquals( "function (){return \"Title\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, "Title" ) );
        assertEquals( "function (){return \"Title\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, "\"Title\"" ) );
        assertEquals( "function (){return \"Long Title\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, "\"Long Title\"" ) );
    }

    @Test
    public void prepareJSEntry() {
        assertEquals( "function () { return \"ko\";}", JSRegistry.prepareEntryValue( CodeType.TITLE, "function () { return \"ko\";}" ) );
        assertEquals( "function (){alert('hi');;}", JSRegistry.prepareEntryValue( CodeType.ON_CLOSE, "alert('hi');" ) );
    }
}

