package org.dashbuilder.renderer.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dashbuilder.renderer.RendererSettings;
import org.junit.Test;

public class RendererSettingsServiceTest {
    
    private static final String SOME_UUID = "SOME_UUID";

    @Test
    public void propertiesLoadTest() {
        RendererSettingsServiceImpl serviceImpl = new RendererSettingsServiceImpl();
        System.setProperty(RendererSettingsService.DEFAULT_RENDERER_PROPERTY, SOME_UUID);
        System.setProperty(RendererSettingsService.OFFLINE_RENDERER_PROPERTY, "not valid property");
        serviceImpl.loadSettings();
        RendererSettings settings = serviceImpl.getSettings();
        assertEquals(settings.getDefaultRenderer(), SOME_UUID);
        assertTrue(!settings.isOffline());
        
        System.setProperty(RendererSettingsService.DEFAULT_RENDERER_PROPERTY, "");
        System.setProperty(RendererSettingsService.OFFLINE_RENDERER_PROPERTY, "true");
        serviceImpl.loadSettings();
        settings = serviceImpl.getSettings();
        assertTrue(settings.getDefaultRenderer().isEmpty());
        assertTrue(settings.isOffline());
    }

}
