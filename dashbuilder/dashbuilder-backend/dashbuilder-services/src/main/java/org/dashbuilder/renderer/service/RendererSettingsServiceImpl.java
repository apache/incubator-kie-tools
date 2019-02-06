package org.dashbuilder.renderer.service;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.renderer.RendererSettings;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RendererSettingsServiceImpl implements RendererSettingsService {

    private RendererSettings settings;
    
    @PostConstruct
    public void loadSettings() {
        String defaultRenderer = System.getProperty(DEFAULT_RENDERER_PROPERTY, "");
        String offlineStr = System.getProperty(OFFLINE_RENDERER_PROPERTY, "false");
        boolean offline=  Boolean.parseBoolean(offlineStr);
        settings = new RendererSettings(defaultRenderer, offline);
    }
    
    @Override
    public RendererSettings getSettings() {
        return settings;
    }

}