package org.dashbuilder.renderer.service;


import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RendererSettingsServiceImpl implements RendererSettingsService {

    String DEFAULT_RENDERER_PROPERTY = "org.dashbuilder.renderer.default";
    
    @Override
    public String userDefaultRenderer() {
        return System.getProperty(DEFAULT_RENDERER_PROPERTY, "");
    }

}