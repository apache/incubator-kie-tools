package org.dashbuilder.renderer.service;

import org.dashbuilder.renderer.RendererSettings;


/**
 *  Provide access to Renderer settings.
 *  
 */

public interface RendererSettingsService {
    
    final static String DEFAULT_RENDERER_PROPERTY = "org.dashbuilder.renderer.default";
    
    final static String OFFLINE_RENDERER_PROPERTY = "org.dashbuilder.renderer.offline";
    
    public RendererSettings getSettings();

}
