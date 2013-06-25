package org.uberfire.backend.plugin;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RuntimePluginsService {

    Collection<String> listPluginsContent();

    String getTemplateContent( final String url );

}
