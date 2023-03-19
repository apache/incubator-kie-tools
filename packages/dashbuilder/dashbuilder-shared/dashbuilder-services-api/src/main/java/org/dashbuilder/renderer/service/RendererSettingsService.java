package org.dashbuilder.renderer.service;

import org.dashbuilder.renderer.RendererSettings;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 *  Provide access to Renderer settings.
 *  
 */
@Remote
public interface RendererSettingsService {
    
    final static String DEFAULT_RENDERER_PROPERTY = "org.dashbuilder.renderer.default";
    
    final static String OFFLINE_RENDERER_PROPERTY = "org.dashbuilder.renderer.offline";
    
    public RendererSettings getSettings();

}
